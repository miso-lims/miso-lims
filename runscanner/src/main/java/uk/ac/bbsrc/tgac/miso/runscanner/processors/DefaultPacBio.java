package uk.ac.bbsrc.tgac.miso.runscanner.processors;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;
import uk.ac.bbsrc.tgac.miso.dto.PacBioNotificationDto;
import uk.ac.bbsrc.tgac.miso.runscanner.RunProcessor;

/**
 * Scan PacBio runs from a directory. The address
 *
 */
public class DefaultPacBio extends RunProcessor {
  /**
   * Extract data from an XML metadata file and put it in the DTO.
   */
  interface ProcessMetadata {
    public void accept(Document document, PacBioNotificationDto dto) throws XPathException;
  }

  /**
   * This is the response object provided by the PacBio web service when queries about the state of a plate.
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class StatusResponse {

    private String customState;
    private String status;

    public String getCustomState() {
      return customState;
    }

    public String getStatus() {
      return status;
    }

    @JsonProperty("CustomState")
    public void setCustomState(String customState) {
      this.customState = customState;
    }

    @JsonProperty("Status")
    public void setStatus(String status) {
      this.status = status;
    }

    public HealthType translateStatus() {
      if (status == null) {
        return HealthType.Unknown;
      }
      switch (status) {
      case "Ready":
        return HealthType.Started;
      case "Running":
        return HealthType.Running;
      case "Aborted":
        return HealthType.Stopped;
      case "Failed":
        return HealthType.Failed;
      case "Complete":
        return HealthType.Completed;
      default:
        return HealthType.Unknown;
      }
    }

  }

  private static final Predicate<String> CELL_DIRECTORY = Pattern.compile("[A-Z]{1}[0-9]{2}_[0-9]{1}").asPredicate();

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

  private static final Logger log = LoggerFactory.getLogger(DefaultPacBio.class);

  /**
   * These are all the things that can be extracted from the PacBio metadata XML file.
   */
  private static final ProcessMetadata[] METADATA_PROCESSORS = new ProcessMetadata[] {
      processString("//Run/Name", PacBioNotificationDto::setRunAlias),
      processString("//InstrumentName", PacBioNotificationDto::setSequencerName),
      processString("//InstCtrlVer", PacBioNotificationDto::setSoftware),
      processString("//Sample/PlateId", PacBioNotificationDto::setContainerSerialNumber),
      processDate("//Run/WhenStarted", PacBioNotificationDto::setStartDate), processNumber("//Movie/DurationInSec", (dto, duration) -> {
        LocalDateTime start = dto.getStartDate();
        if (start == null) {
          return;
        }
        dto.setCompletionDate(start.plusSeconds(duration.longValue()));
      }), processSampleInformation() };

  private static final Pattern RUN_DIRECTORY = Pattern.compile("^.+_\\d+$");

  private static final DateTimeFormatter URL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

  public static DefaultPacBio create(Builder builder, ObjectNode parameters) {
    JsonNode address = parameters.get("address");
    return address.isTextual() ? new DefaultPacBio(builder, address.textValue().replaceAll("/+$", "")) : null;
  }

  /**
   * Extract a PacBio-formatted string from the metadata file and put the parsed result into the DTO.
   * 
   * @param expression the XPath expression yielding the date
   * @param setter the writer for the date
   */
  private static ProcessMetadata processDate(String expression, BiConsumer<PacBioNotificationDto, LocalDateTime> setter) {
    return processString(expression, (dto, result) -> setter.accept(dto, LocalDateTime.parse(result, DATE_FORMAT)));
  }

  /**
   * Extract a number from the metadata file and put the result into the DTO.
   * 
   * @param expression the XPath expression yielding the number
   * @param setterthe writer for the number
   * @return
   */
  private static ProcessMetadata processNumber(String expression, BiConsumer<PacBioNotificationDto, Double> setter) {
    XPathExpression expr = RunProcessor.compileXPath(expression)[0];
    return (document, dto) -> {
      Double result = (Double) expr.evaluate(document, XPathConstants.NUMBER);
      if (result != null) {
        setter.accept(dto, result);
      }
    };
  }

  /**
   * Create a metadata processor to populate the map of wellname → poolbarcodes in the DTO.
   * 
   * @return
   */
  private static ProcessMetadata processSampleInformation() {
    XPathExpression[] expr = RunProcessor.compileXPath("//Sample/WellName", "//Sample/Name");
    return (document, dto) -> {
      String well = (String) expr[0].evaluate(document, XPathConstants.STRING);
      String name = (String) expr[1].evaluate(document, XPathConstants.STRING);
      if (LimsUtils.isStringBlankOrNull(name) || LimsUtils.isStringBlankOrNull(well)) {
        return;
      }
      Map<String, String> poolInfo = dto.getPoolNames();
      if (poolInfo == null) {
        dto.setPoolNames(poolInfo = new HashMap<>());
      }
      poolInfo.put(well, name);
    };
  }

  /**
   * Extract a string expression from the metadata file and write it into the DTO.
   * 
   * @param expression the XPath expression yielding the string
   * @param setter writer for the string
   * @return
   */
  private static ProcessMetadata processString(String expression, BiConsumer<PacBioNotificationDto, String> setter) {
    XPathExpression expr = RunProcessor.compileXPath(expression)[0];
    return (document, dto) -> {
      String result = (String) expr.evaluate(document, XPathConstants.STRING);
      if (result != null) {
        setter.accept(dto, result);
      }
    };
  }

  private final String address;

  public DefaultPacBio(Builder builder, String address) {
    super(builder);
    this.address = address;
  }

  @Override
  public Stream<File> getRunsFromRoot(File root) {
    return Arrays.stream(root.listFiles(f -> f.isDirectory() && RUN_DIRECTORY.matcher(f.getName()).matches()));
  }

  protected StatusResponse getStatus(String url) {
    return new RestTemplate().getForObject(url, StatusResponse.class);
  }

  @Override
  public NotificationDto process(File runDirectory, TimeZone tz) throws IOException {
    // We create one DTO for a run, but there are going to be many wells with independent and duplicate metadata that will will simply
    // overwrite in the shared DTO. If the data differs, the last well wins.
    PacBioNotificationDto dto = new PacBioNotificationDto();
    dto.setPairedEndRun(false);
    dto.setSequencerFolderPath(runDirectory.getAbsolutePath());
    // This will be incremented during the metadata scan
    dto.setLaneCount(0);
    // Read all the metadata files and write their results into the DTO.
    Arrays.stream(runDirectory.listFiles(cellDirectory -> cellDirectory.isDirectory() && CELL_DIRECTORY.test(cellDirectory.getName())))
        .flatMap(cellDirectory -> Arrays.stream(cellDirectory.listFiles(file -> file.getName().endsWith(".metadata.xml"))))
        .map(RunProcessor::parseXml).filter(Optional::isPresent)
        .forEach(metadata -> processMetadata(metadata.get(), dto));

    // The current job state is not available from the metadata files, so contact the PacBio instrument's web service.
    String url = String.format("%s/Jobs/Plate/%s/Status", address,
        URLEncoder.encode(dto.getContainerSerialNumber(), "US-ASCII").replaceAll("\\+", "%20"));
    dto.setHealthType(getStatus(url).translateStatus());
    // If the metadata gave us a completion date, but the web service told us the run isn't complete, delete the completion date of lies.
    if (!dto.getHealthType().isDone()) {
      dto.setCompletionDate(null);
    }

    ObjectMapper mapper = createObjectMapper();
    ArrayNode metrics = mapper.createArrayNode();

    try {
      ObjectNode dashboardMetric = metrics.addObject();
      dashboardMetric.put("type", "link");
      dashboardMetric.put("name", "PacBio Dashboard");
      URIBuilder builder = new URIBuilder(address + "/Metrics/RSRunReport");
      builder.addParameter("instrument", dto.getSequencerName());
      builder.addParameter("run", dto.getRunAlias());
      builder.addParameter("from", dto.getStartDate().truncatedTo(ChronoUnit.DAYS).format(URL_DATE_FORMAT));
      builder.addParameter("to", dto.getStartDate().plusDays(1).truncatedTo(ChronoUnit.DAYS).format(URL_DATE_FORMAT));
      dashboardMetric.put("href", builder.build().toASCIIString());
    } catch (URISyntaxException e) {
      throw new IOException(e);
    }

    dto.setMetrics(mapper.writeValueAsString(metrics));

    return dto;
  }

  /**
   * Parse a metadata XML file and put all the relevant data into the DTO.
   * 
   * @param metadataFile the path to the XML file
   * @param dto the DTO to update
   */
  private void processMetadata(Document metadata, PacBioNotificationDto dto) {
    for (ProcessMetadata processor : METADATA_PROCESSORS) {
      try {
        processor.accept(metadata, dto);
      } catch (XPathException e) {
        log.error("Failed to extract metadata", e);
      }
    }
    dto.setLaneCount(dto.getLaneCount() + 1);
  }

}
