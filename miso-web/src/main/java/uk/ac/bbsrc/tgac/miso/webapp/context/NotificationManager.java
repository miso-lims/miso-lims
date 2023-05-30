package uk.ac.bbsrc.tgac.miso.webapp.context;

import static j2html.TagCreator.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eaglegenomics.simlims.core.User;

import io.prometheus.client.Gauge;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.GroupIdentifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferItem;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferNotification;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentTissueAttributes;
import uk.ac.bbsrc.tgac.miso.core.service.TransferNotificationService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Component
public class NotificationManager {

  private static final Logger log = LoggerFactory.getLogger(NotificationManager.class);

  private static final long ONE_MINUTE = 60000;

  private static final String LIST_CONTAINER_STYLE = "display: inline-block;";
  private static final String HEADING_STYLE =
      "background-color: #F0F0FF; color: #666666; margin-bottom: 2px; text-align: center; border-bottom: 1px solid #A9A9A9; border-right: 1px solid #A9A9A9;";
  private static final String TABLE_STYLE = "border-collapse: collapse;";
  private static final String TH_STYLE = "background-color: #E6E6E6; border: 1px solid #D3D3D3; padding: 4px";
  private static final String CELL_STYLE = "padding: 4px;";
  private static final String STRIPED_ROW_STYLE = "background-color: #F0F0FF;";
  private static final String ERROR_STYLE = "font-weight: bold;";

  private static final Gauge smtpWindowSent = Gauge.build().name("miso_notification_manager_smtp_window_sent")
      .help("The number of emails that MISO has sent within the throttle window").register();
  private static final Gauge smtpFailed = Gauge.build().name("miso_notification_manager_smtp_failed")
      .help("The number of emails that MISO has failed to send").register();

  @Value("${miso.smtp.host:#{null}}")
  private String smtpHost;
  @Value("${miso.smtp.port:25}")
  private int smtpPort;
  @Value("${miso.smtp.ssl:false}")
  private boolean smtpUseSsl;
  @Value("${miso.smtp.username:#{null}}")
  private String smtpUser;
  @Value("${miso.smtp.password:#{null}}")
  private String smtpPassword;
  @Value("${miso.smtp.fromName:MISO}")
  private String smtpFromName;
  @Value("${miso.smtp.fromAddress:#{null}}")
  private String smtpFromEmail;
  @Value("${miso.smtp.internalDomain:#{null}}")
  private String internalDomain;

  @Value("${miso.smtp.holdMinutes:60}")
  private int smtpHoldMinutes;
  @Value("${miso.smtp.throttleMinutes:60}")
  private int smtpThrottleMinutes;
  @Value("${miso.smtp.throttleLimit:25}")
  private int smtpThrottleLimit;

  @Value("${miso.internalBaseUrl:#{null}}")
  private String baseUrl;

  @Autowired
  private TransferNotificationService transferNotificationService;

  private final Queue<Date> sendTimes = new LinkedList<>();

  @Scheduled(initialDelay = 300_000, fixedDelay = 300_000)
  public void sendPendingNotifications() throws IOException {
    if (smtpHost == null) {
      return;
    }
    sendPendingFailureNotifications();
    sendPendingTransferNotifications();
    sendPendingFailureNotifications();
  }

  private int getCurrentAllowance() {
    Date pastWindow = new Date(Calendar.getInstance().getTimeInMillis() - smtpThrottleMinutes * ONE_MINUTE);
    while (!sendTimes.isEmpty() && sendTimes.peek().before(pastWindow)) {
      sendTimes.poll();
    }
    smtpWindowSent.set(sendTimes.size());
    return smtpThrottleLimit - sendTimes.size();
  }

  private void sendPendingTransferNotifications() throws IOException {
    List<TransferNotification> pendingNotifications =
        transferNotificationService.listPending(smtpHoldMinutes, getCurrentAllowance());
    for (TransferNotification pending : pendingNotifications) {
      pending.setSentTime(new Date());
      try {
        sendTransferNotification(pending);
        pending.setSendSuccess(true);
      } catch (EmailException e) {
        log.warn("Failed to send email", e);
        smtpFailed.inc();
        pending.setSendSuccess(false);
      }
      transferNotificationService.update(pending);
    }
  }

  private void sendTransferNotification(TransferNotification notification) throws EmailException {
    HtmlEmail email = new HtmlEmail();
    email.addTo(notification.getRecipientEmail(), notification.getRecipientName());
    email.setSubject(makeTransferNotificationSubject(notification.getTransfer()));
    email.setHtmlMsg(makeTransferNotificationHtmlMessage(notification));
    sendMail(email);
  }

  private void sendPendingFailureNotifications() throws IOException {
    List<TransferNotification> pendingFailures = transferNotificationService.listFailurePending(getCurrentAllowance());
    for (TransferNotification pending : pendingFailures) {
      pending.setFailureSentTime(new Date());
      try {
        sendFailedNotification(pending);
      } catch (EmailException e) {
        log.warn("Failed to send notification failure email", e);
        smtpFailed.inc();
      }
      transferNotificationService.update(pending);
    }
  }

  private void sendFailedNotification(TransferNotification notification) throws EmailException {
    HtmlEmail email = new HtmlEmail();
    email.addTo(notification.getCreator().getEmail(), notification.getCreator().getFullName());
    email.setSubject("Transfer Notification Failed");
    ContainerTag error = p("Failed to send the following transfer notification to ")
        .withText(notification.getRecipientName())
        .withText(" <")
        .with(a(notification.getRecipientEmail()).withHref("mailto:" + notification.getRecipientEmail()))
        .withText(">:");
    email.setHtmlMsg(makeTransferNotificationHtmlMessage(notification, error));
    sendMail(email);
  }

  private void sendMail(Email email) throws EmailException {
    if (sendTimes.size() >= smtpThrottleLimit) {
      throw new IllegalStateException("Attempted to surpass outgoing email limit");
    }
    email.setHostName(smtpHost);
    email.setSSLOnConnect(smtpUseSsl);
    email.setSmtpPort(smtpPort);
    if (smtpUseSsl) {
      email.setSslSmtpPort(Integer.toString(smtpPort));
    }
    if (smtpUser != null && smtpPassword != null) {
      email.setAuthentication(smtpUser, smtpPassword);
    }
    email.setFrom(smtpFromEmail, smtpFromName);
    email.send();
    sendTimes.offer(new Date());
  }

  private String makeTransferNotificationSubject(Transfer transfer) {
    Map<Project, Integer> counts = new HashMap<>();
    for (TransferSample sam : transfer.getSampleTransfers()) {
      addCount(sam.getItem().getProject(), counts);
    }
    for (TransferLibrary lib : transfer.getLibraryTransfers()) {
      addCount(lib.getItem().getSample().getProject(), counts);
    }
    for (TransferLibraryAliquot ali : transfer.getLibraryAliquotTransfers()) {
      addCount(ali.getItem().getLibrary().getSample().getProject(), counts);
    }

    Project mainProject = null;
    for (Project project : counts.keySet()) {
      if (mainProject == null || counts.get(project) > counts.get(mainProject)) {
        mainProject = project;
      }
    }

    boolean otherProjects = counts.size() > 1;

    StringBuilder sb = new StringBuilder();

    boolean hasSamples = !transfer.getSampleTransfers().isEmpty();
    boolean hasLibraries = !transfer.getLibraryTransfers().isEmpty();
    boolean hasLibraryAliquots = !transfer.getLibraryAliquotTransfers().isEmpty();
    boolean hasPools = !transfer.getPoolTransfers().isEmpty();

    if (hasSamples && !hasLibraries && !hasLibraryAliquots && !hasPools) {
      sb.append("Sample ");
    } else if (!hasSamples && hasLibraries && !hasLibraryAliquots && !hasPools) {
      sb.append("Library ");
    } else if (!hasSamples && !hasLibraries && hasLibraryAliquots && !hasPools) {
      sb.append("Library Aliquot ");
    } else if (!hasSamples && !hasLibraries && !hasLibraryAliquots && hasPools) {
      sb.append("Pool ");
    }
    sb.append("Transfer - ");

    if (mainProject == null) {
      sb.append("Undetermined project");
    } else {
      if (transfer.isDistribution() || mainProject.getCode() == null) {
        sb.append(mainProject.getTitle());
      } else {
        sb.append(mainProject.getCode());
      }
      if (otherProjects) {
        sb.append(" and others");
      }
    }

    return sb.toString();
  }

  private void addCount(Project project, Map<Project, Integer> counts) {
    Integer count = counts.getOrDefault(project, 0);
    count++;
    counts.put(project, count);
  }

  private String makeTransferNotificationHtmlMessage(TransferNotification notification) {
    return makeTransferNotificationHtmlMessage(notification, null);
  }

  private String makeTransferNotificationHtmlMessage(TransferNotification notification, ContainerTag error) {
    Transfer transfer = notification.getTransfer();
    List<DomContent> bodyElements = new ArrayList<>();
    if (error != null) {
      bodyElements.add(error.withStyle(ERROR_STYLE));
    }
    bodyElements.add(p(String.format("The following items are being transferred from %s to %s, %s.",
        getSender(transfer), getRecipient(transfer),
        LimsUtils.getDateTimeFormat().format(transfer.getTransferTime()))));

    if (!transfer.getSampleTransfers().isEmpty()) {
      bodyElements.add(makeList("Samples", makeSampleTable(transfer.getSampleTransfers())));
    }
    if (!transfer.getLibraryTransfers().isEmpty()) {
      bodyElements.add(makeList("Libraries", makeLibraryTable(transfer.getLibraryTransfers())));
    }
    if (!transfer.getLibraryAliquotTransfers().isEmpty()) {
      bodyElements.add(makeList("Library Aliquots", makeLibraryAliquotTable(transfer.getLibraryAliquotTransfers())));
    }
    if (!transfer.getPoolTransfers().isEmpty()) {
      bodyElements.add(makeList("Pools", makePoolTable(transfer.getPoolTransfers())));
    }

    if (baseUrl != null && internalDomain != null && notification.getRecipientEmail().endsWith("@" + internalDomain)) {
      bodyElements
          .add(p(a("View transfer in MISO").withHref(String.format("%s/miso/transfer/%d", baseUrl, transfer.getId()))));
    }
    bodyElements.add(p("Please do not reply to this email. This address is not monitored."));

    User sender = notification.getCreator();
    bodyElements.add(p("If you no longer wish to receive these notifications, please email ")
        .withText(sender.getFullName())
        .withText(" at ")
        .with(a(sender.getEmail()).withHref("mailto:" + sender.getEmail()))
        .withText(" to let them know."));
    return html(body().with(bodyElements)).render();
  }

  private static String getSender(Transfer transfer) {
    if (transfer.getSenderGroup() != null) {
      return transfer.getSenderGroup().getName();
    } else {
      return transfer.getSenderLab().getAlias();
    }
  }

  private static String getRecipient(Transfer transfer) {
    if (transfer.getRecipientGroup() != null) {
      return transfer.getRecipientGroup().getName();
    } else {
      return transfer.getRecipient();
    }
  }

  private static ContainerTag makeList(String title, ContainerTag table) {
    ContainerTag heading = h1(title).withStyle(HEADING_STYLE);
    return div(heading, table).withStyle(LIST_CONTAINER_STYLE);
  }

  private static ContainerTag makeSampleTable(Collection<TransferSample> transferSamples) {
    boolean detailed = LimsUtils.isDetailedSample(transferSamples.iterator().next().getItem());

    ContainerTag headerRow = tr(makeTh("Alias"));
    if (detailed) {
      headerRow = headerRow.with(makeTh("Type"), makeTh("External Identifier"), makeTh("Tissue Attributes"),
          makeTh("Timepoint"));
    }
    headerRow = headerRow.with(makeTh("VOL (uL)"), makeTh("[] (ng/uL)"), makeTh("Total (ng)"));
    if (detailed) {
      headerRow = headerRow.with(makeTh("Subproject"), makeTh("Group ID"), makeTh("Group Description"));
    }
    headerRow = headerRow.with(makeTh("Barcode"), makeTh("Location"));

    List<ContainerTag> rows = new ArrayList<>();
    List<TransferSample> sorted = sortByAlias(transferSamples);
    for (TransferSample transferSample : sorted) {
      Sample sample = transferSample.getItem();
      DetailedSample detailedSample = detailed ? (DetailedSample) sample : null;
      List<DomContent> cells = new ArrayList<>();
      cells.add(makeTd(sample.getAlias()));
      if (detailed) {
        cells.add(makeTd(dnaOrRna(detailedSample)));
        cells.add(makeTd(detailedSample.getIdentityAttributes().getExternalName()));
        ParentTissueAttributes tissue = detailedSample.getTissueAttributes();
        cells.add(makeTd(
            tissue == null ? null : tissue.getTissueOrigin().getAlias() + "_" + tissue.getTissueType().getAlias()));
        cells.add(makeTd(tissue == null ? null : tissue.getTimepoint()));
      }
      BigDecimal volume =
          transferSample.getDistributedVolume() != null ? transferSample.getDistributedVolume() : sample.getVolume();
      cells.add(makeTd(LimsUtils.toNiceString(volume)));
      cells.add(makeTd(LimsUtils.toNiceString(sample.getConcentration())));
      cells.add(makeTd(getYieldString(volume, sample.getConcentration())));
      if (detailed) {
        cells.add(makeTd(detailedSample.getSubproject() == null ? null : detailedSample.getSubproject().getAlias()));
        GroupIdentifiable groupIdEntity = detailedSample.getEffectiveGroupIdEntity();
        cells.add(makeTd(groupIdEntity == null ? null : groupIdEntity.getGroupId()));
        cells.add(makeTd(groupIdEntity == null ? null : groupIdEntity.getGroupDescription()));
      }
      cells.add(makeTd(sample.getIdentificationBarcode()));
      cells.add(makeTd(makeLocationLabel(transferSample)));
      rows.add(tr().with(cells));
    }

    return makeTable(headerRow, rows);
  }

  private static <T extends TransferItem<?>> List<T> sortByAlias(Collection<T> original) {
    return original.stream()
        .sorted(Comparator.comparing(item -> item.getItem().getAlias()))
        .collect(Collectors.toList());
  }

  private static String makeLocationLabel(TransferItem<?> item) {
    if (item.getDistributedBoxAlias() != null) {
      return item.getDistributedBoxAlias() + " " + item.getDistributedBoxPosition();
    } else if (item.getItem().getBox() != null) {
      return item.getItem().getBox().getAlias() + " " + item.getItem().getBoxPosition();
    } else {
      return "Unknown";
    }
  }

  private static ContainerTag makeLibraryTable(Collection<TransferLibrary> transferLibraries) {
    ContainerTag headerRow =
        tr(makeTh("Alias"), makeTh("Barcode"), makeTh("Location"), makeTh("Platform"), makeTh("Type"),
            makeTh("i7 Index Name"), makeTh("i7 Index"), makeTh("i5 Index Name"), makeTh("i5 Index"));

    List<ContainerTag> rows = new ArrayList<>();
    List<TransferLibrary> sorted = sortByAlias(transferLibraries);
    for (TransferLibrary transferLibrary : sorted) {
      Library library = transferLibrary.getItem();
      List<DomContent> cells = new ArrayList<>();
      cells.add(makeTd(library.getAlias()));
      cells.add(makeTd(library.getIdentificationBarcode()));
      cells.add(makeTd(makeLocationLabel(transferLibrary)));
      cells.add(makeTd(library.getPlatformType().getKey()));
      cells.add(makeTd(library.getLibraryType().getDescription()));
      cells.add(makeTd(library.getIndex1() == null ? null : library.getIndex1().getName()));
      cells.add(makeTd(library.getIndex1() == null ? null : library.getIndex1().getSequence()));
      cells.add(makeTd(library.getIndex2() == null ? null : library.getIndex2().getName()));
      cells.add(makeTd(library.getIndex2() == null ? null : library.getIndex2().getSequence()));
      rows.add(tr().with(cells));
    }

    return makeTable(headerRow, rows);
  }

  private static ContainerTag makeLibraryAliquotTable(Collection<TransferLibraryAliquot> transferLibraryAliquots) {
    ContainerTag headerRow =
        tr(makeTh("Alias"), makeTh("Barcode"), makeTh("Location"), makeTh("Platform"), makeTh("Type"),
            makeTh("i7 Index Name"), makeTh("i7 Index"), makeTh("i5 Index Name"), makeTh("i5 Index"),
            makeTh("Targeted Sequencing"));

    List<ContainerTag> rows = new ArrayList<>();
    List<TransferLibraryAliquot> sorted = sortByAlias(transferLibraryAliquots);
    for (TransferLibraryAliquot transferLibraryAliquot : sorted) {
      LibraryAliquot libraryAliquot = transferLibraryAliquot.getItem();
      Library library = libraryAliquot.getLibrary();
      List<DomContent> cells = new ArrayList<>();
      cells.add(makeTd(libraryAliquot.getAlias()));
      cells.add(makeTd(libraryAliquot.getIdentificationBarcode()));
      cells.add(makeTd(makeLocationLabel(transferLibraryAliquot)));
      cells.add(makeTd(library.getPlatformType().getKey()));
      cells.add(makeTd(library.getLibraryType().getDescription()));
      cells.add(makeTd(library.getIndex1() == null ? null : library.getIndex1().getName()));
      cells.add(makeTd(library.getIndex1() == null ? null : library.getIndex1().getSequence()));
      cells.add(makeTd(library.getIndex2() == null ? null : library.getIndex2().getName()));
      cells.add(makeTd(library.getIndex2() == null ? null : library.getIndex2().getSequence()));
      rows.add(tr().with(cells));
    }

    return makeTable(headerRow, rows);
  }

  private static ContainerTag makePoolTable(Collection<TransferPool> transferPools) {
    ContainerTag headerRow = tr(makeTh("Alias"), makeTh("Barcode"), makeTh("Location"));

    List<ContainerTag> rows = new ArrayList<>();
    List<TransferPool> sorted = sortByAlias(transferPools);
    for (TransferPool transferPool : sorted) {
      Pool pool = transferPool.getItem();
      List<DomContent> cells = new ArrayList<>();
      cells.add(makeTd(pool.getAlias()));
      cells.add(makeTd(pool.getIdentificationBarcode()));
      cells.add(makeTd(makeLocationLabel(transferPool)));
      rows.add(tr().with(cells));
    }

    return makeTable(headerRow, rows);
  }

  private static ContainerTag makeTh(String text) {
    return th(text == null ? "" : text).withStyle(TH_STYLE);
  }

  private static ContainerTag makeTd(String text) {
    return td(text == null ? "" : text).withStyle(CELL_STYLE);
  }

  private static ContainerTag makeTable(ContainerTag headerRow, List<ContainerTag> bodyRows) {
    for (int i = 0; i < bodyRows.size(); i += 2) {
      bodyRows.get(i).withStyle(STRIPED_ROW_STYLE);
    }
    return table(thead(headerRow), tbody().with(bodyRows)).withStyle(TABLE_STYLE);
  }

  private static String dnaOrRna(DetailedSample sample) {
    if (sample.getSampleClass().getAlias().contains("DNA")) {
      return "DNA";
    } else if (sample.getSampleClass().getAlias().contains("RNA")) {
      return "RNA";
    } else {
      return "Other";
    }
  }

  private static String getYieldString(BigDecimal volume, BigDecimal concentration) {
    if (volume != null && concentration != null) {
      return LimsUtils.toNiceString(volume.multiply(concentration));
    } else {
      return null;
    }
  }

}
