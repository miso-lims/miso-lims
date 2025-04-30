package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import static uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils.getStringInput;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferItem;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferNotification;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.GroupService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.TransferNotificationService;
import uk.ac.bbsrc.tgac.miso.core.service.TransferService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
@RequestMapping("/transfer")
public class TransferController {

  private static final List<String> TABS = Arrays.asList("Pending", "Receipt", "Internal", "Distribution");

  @Autowired
  private TransferService transferService;
  @Autowired
  private TransferNotificationService transferNotificationService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private GroupService groupService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryAliquotService libraryAliquotService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private ObjectMapper mapper;

  @Value("${miso.smtp.host:#{null}}")
  private String smtpHost;

  private boolean notificationsEnabled() {
    return smtpHost != null;
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return new TabbedListItemsPage("transfer", "tab", TABS.stream(), (t1, t2) -> 1, Function.identity(),
        String::toLowerCase, mapper).list(model);
  }

  @PostMapping("/new")
  public ModelAndView create(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    String sampleIdString = getStringInput("sampleIds", form, false);
    String libraryIdString = getStringInput("libraryIds", form, false);
    String libraryAliquotIdString = getStringInput("libraryAliquotIds", form, false);
    String poolIdString = getStringInput("poolIds", form, false);
    String boxIdString = getStringInput("boxIds", form, false);

    Transfer transfer = new Transfer();
    addItems("sample", sampleIdString, sampleService, TransferSample::new, transfer::getSampleTransfers);
    addItems("library", libraryIdString, libraryService, TransferLibrary::new, transfer::getLibraryTransfers);
    addItems("library aliquot", libraryAliquotIdString, libraryAliquotService, TransferLibraryAliquot::new,
        transfer::getLibraryAliquotTransfers);
    addItems("pool", poolIdString, poolService, TransferPool::new, transfer::getPoolTransfers);
    addBoxItems(boxIdString, transfer);

    model.put("title", "New Transfer");
    return setupForm(transfer, PageMode.CREATE, true, false, model);
  }

  @GetMapping("/new")
  public ModelAndView create(ModelMap model) throws IOException {
    model.put("title", "New Transfer");
    Transfer transfer = new Transfer();
    return setupForm(transfer, PageMode.CREATE, true, false, model);
  }

  private <T extends Boxable, U extends TransferItem<T>> void addItems(String typeName, String idString,
      ProviderService<T> service,
      Supplier<U> constructor, Supplier<Set<U>> setGetter) throws IOException {
    if (idString == null) {
      return;
    }
    for (Long id : LimsUtils.parseIds(idString)) {
      T item = service.get(id);
      if (item == null) {
        throw new ClientErrorException(String.format("No %s found for ID: %d", typeName, id));
      }
      U transferItem = constructor.get();
      transferItem.setItem(item);
      setGetter.get().add(transferItem);
    }
  }

  private <T extends Boxable, U extends TransferItem<T>> void addBoxItems(String idString, Transfer transfer)
      throws IOException {
    if (idString == null) {
      return;
    }
    for (Long id : LimsUtils.parseIds(idString)) {
      Box box = boxService.get(id);
      if (box == null) {
        throw new ClientErrorException(String.format("No box found for ID: %d", id));
      }
      for (BoxPosition item : box.getBoxPositions().values()) {
        long itemId = item.getBoxableId().getTargetId();
        switch (item.getBoxableId().getTargetType()) {
          case SAMPLE:
            TransferSample transferSample = new TransferSample();
            transferSample.setItem(sampleService.get(itemId));
            transfer.getSampleTransfers().add(transferSample);
            break;
          case LIBRARY:
            TransferLibrary transferLibrary = new TransferLibrary();
            transferLibrary.setItem(libraryService.get(itemId));
            transfer.getLibraryTransfers().add(transferLibrary);
            break;
          case LIBRARY_ALIQUOT:
            TransferLibraryAliquot transferLibraryAliquot = new TransferLibraryAliquot();
            transferLibraryAliquot.setItem(libraryAliquotService.get(itemId));
            transfer.getLibraryAliquotTransfers().add(transferLibraryAliquot);
            break;
          case POOL:
            TransferPool transferPool = new TransferPool();
            transferPool.setItem(poolService.get(itemId));
            transfer.getPoolTransfers().add(transferPool);
            break;
          default:
            throw new IllegalArgumentException("Unexpected boxable type: " + item.getBoxableId().getTargetType());
        }
      }
    }
  }

  @GetMapping("/{id}")
  public ModelAndView edit(@PathVariable long id, ModelMap model) throws IOException, EmailException {
    Transfer transfer = transferService.get(id);
    if (transfer == null) {
      throw new NotFoundException("No transfer found for ID: " + id);
    }
    model.put("title", "Transfer " + id);

    User user = authorizationManager.getCurrentUser();
    // allow editing receipt if user is admin or recipient
    boolean editReceipt = user.isAdmin()
        || (transfer.getRecipientGroup() != null && userHasGroup(user, transfer.getRecipientGroup()));
    // allow editing send if user is admin, sender, or recipient of external -> internal transfer
    boolean editSend = user.isAdmin()
        || (transfer.getSenderGroup() != null && userHasGroup(user, transfer.getSenderGroup()))
        || (editReceipt && transfer.getSenderGroup() == null);

    List<TransferNotification> notifications = transferNotificationService.listByTransferId(id);
    model.put("notifications",
        mapper.writeValueAsString(notifications.stream().map(Dtos::asDto).collect(Collectors.toList())));
    return setupForm(transfer, PageMode.EDIT, editSend, editReceipt, model);
  }

  public ModelAndView setupForm(Transfer transfer, PageMode pageMode, boolean editSend, boolean editReceipt,
      ModelMap model)
      throws IOException {
    ObjectNode formConfig = mapper.createObjectNode();
    formConfig.put(PageMode.PROPERTY, pageMode.getLabel());
    formConfig.put("editSend", editSend);
    formConfig.put("editReceipt", editReceipt);

    Collection<Group> senderGroups = null;
    User user = authorizationManager.getCurrentUser();
    if (user.isAdmin()) {
      senderGroups = groupService.list();
    } else if (editSend && transfer.getSenderLab() == null) {
      senderGroups = user.getGroups();
    } else if (transfer.getSenderGroup() != null) {
      senderGroups = Collections.singleton(transfer.getSenderGroup());
    } else {
      senderGroups = Collections.emptySet();
    }
    MisoWebUtils.addJsonArray(mapper, formConfig, "senderGroups", senderGroups, Dtos::asDto);

    Collection<Group> recipientGroups = null;
    if (user.isAdmin() || editSend) {
      recipientGroups = groupService.list();
    } else if (transfer.getRecipientGroup() != null) {
      recipientGroups = Collections.singleton(transfer.getRecipientGroup());
    } else {
      recipientGroups = Collections.emptySet();
    }
    MisoWebUtils.addJsonArray(mapper, formConfig, "recipientGroups", recipientGroups, Dtos::asDto);

    ObjectNode itemsListConfig = mapper.createObjectNode();
    itemsListConfig.put("editSend", editSend);
    itemsListConfig.put("editReceipt", editReceipt);

    model.put(PageMode.PROPERTY, pageMode.getLabel());
    model.put("transfer", transfer);
    model.put("transferDto", mapper.writeValueAsString(Dtos.asDto(transfer)));
    model.put("formConfig", mapper.writeValueAsString(formConfig));
    model.put("itemsListConfig", mapper.writeValueAsString(itemsListConfig));
    model.put("notificationsEnabled", notificationsEnabled());
    return new ModelAndView("/WEB-INF/pages/editTransfer.jsp", model);
  }

  private boolean userHasGroup(User user, Group group) {
    return user.getGroups().stream().anyMatch(userGroup -> userGroup.getId() == group.getId());
  }

}
