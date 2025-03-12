package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletResponse;
import uk.ac.bbsrc.tgac.miso.core.data.Issue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.SampleQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.HandsontableSpreadsheet;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SpreadSheetFormat;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.Spreadsheet;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.ThrowingFunction;
import uk.ac.bbsrc.tgac.miso.core.util.ThrowingSupplier;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.util
 * <p/>
 * Utility class containing static methods for helping with tasks specific to the miso-web module
 * 
 * @author Rob Davey
 * @date 03-Sep-2010
 * @since 0.0.2
 */
public class MisoWebUtils {
  private static final Logger log = LoggerFactory.getLogger(MisoWebUtils.class);

  public static Map<String, String> checkStorageDirectories(String baseStoragePath, String fileStoragePath) {
    Map<String, String> checks = new HashMap<>();
    if (baseStoragePath.endsWith("/")) {
      try {
        File misoDir = new File(baseStoragePath);
        if (LimsUtils.checkDirectory(misoDir, true)) {
          LimsUtils.checkDirectory(new File(baseStoragePath, "log"), true);
          LimsUtils.checkDirectory(new File(baseStoragePath, "temp"), true);
          if (LimsUtils.checkDirectory(new File(fileStoragePath), true)) {
            LimsUtils.checkDirectory(new File(fileStoragePath, "submission"), true);
            checks.put("ok", "All storage directories OK");
          } else {
            checks.put("error", "Error accessing MISO storage files directory.");
          }
        } else {
          checks.put("error", "Error accessing MISO storage base directory.");
        }
      } catch (IOException e) {
        log.error("check storage directories", e);
        checks.put("error", "Cannot access one of the MISO storage directories: " + e.getMessage());
      }
    } else {
      checks.put("error", "MISO storage directory is defined, but must end with a trailing slash!");
    }
    return checks;
  }

  /**
   * Similar to checkDirectory, but for single files.
   * 
   * @param path of type File
   * @return boolean true if the file exists, false if not
   * @throws IOException when the file doesn't exist
   */
  private static boolean checkFile(File path) throws IOException {
    boolean storageOk = path.exists();
    if (!storageOk) {
      StringBuilder sb = new StringBuilder("The file [" + path.toString() + "] doesn't exist.");
      throw new IOException(sb.toString());
    } else {
      log.info("File (" + path + ") OK.");
    }
    return storageOk;
  }

  public static Map<String, String> checkCorePropertiesFiles(String baseStoragePath) {
    Map<String, String> checks = new HashMap<>();
    if (baseStoragePath.endsWith("/")) {
      try {
        checkFile(new File(baseStoragePath, "security.properties"));
        checkFile(new File(baseStoragePath, "submission.properties"));
        checks.put("ok", "All core properties files OK");
      } catch (IOException e) {
        log.error("core properties files check", e);
        checks.put("error", "Cannot access one of the MISO core properties files: " + e.getMessage());
      }
    }
    return checks;
  }

  public static <T> HttpEntity<byte[]> generateSpreadsheet(SpreadsheetRequest request,
      ThrowingFunction<List<Long>, List<T>, IOException> fetcher,
      boolean detailedSample, Function<String, Spreadsheet<T>> formatLibrary, HttpServletResponse response)
      throws IOException {
    List<T> input = fetcher.apply(request.getIds());
    return generateSpreadsheet(request, input.stream(), detailedSample, formatLibrary, response);
  }

  public static <T> HttpEntity<byte[]> generateSpreadsheet(SpreadsheetRequest request, Stream<T> input,
      boolean detailedSample, Function<String, Spreadsheet<T>> formatLibrary, HttpServletResponse response) {
    Spreadsheet<T> spreadsheet = formatLibrary.apply(request.getSheet());
    SpreadSheetFormat formatter = SpreadSheetFormat.valueOf(request.getFormat());
    HttpHeaders headers = makeHttpHeaders(spreadsheet, formatter, response);
    return new HttpEntity<>(formatter.generate(input, detailedSample, spreadsheet), headers);
  }

  public static HttpEntity<byte[]> generateSpreadsheet(List<String> headers, List<List<String>> data,
      boolean detailedSample, SpreadSheetFormat formatter, HttpServletResponse response) {
    Spreadsheet<List<String>> spreadsheet = new HandsontableSpreadsheet(headers);
    HttpHeaders httpHeaders = makeHttpHeaders(spreadsheet, formatter, response);
    return new HttpEntity<>(formatter.generate(data.stream(), detailedSample, spreadsheet), httpHeaders);
  }

  private static <T> HttpHeaders makeHttpHeaders(Spreadsheet<T> spreadsheet, SpreadSheetFormat formatter,
      HttpServletResponse response) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(formatter.mediaType());
    response.setHeader("Content-Disposition",
        "attachment; filename="
            + String.format("%s-%s.%s", spreadsheet.name(), DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(
                ZonedDateTime.now()), formatter.extension()));
    return headers;
  }

  public static <U, V> void addJsonArray(ObjectMapper mapper, ObjectNode node, String key, Collection<U> items,
      Function<U, V> toDto) {
    ArrayNode array = node.putArray(key);
    for (U item : items) {
      V dto = toDto.apply(item);
      JsonNode itemNode = mapper.valueToTree(dto);
      array.add(itemNode);
    }
  }

  /**
   * Retrieves a String value from form data
   * 
   * @param key
   * @param formData
   * @return the value mapped to the provided key if it is set and not empty; otherwise null
   */
  public static String getStringInput(String key, Map<String, String> formData, boolean required) {
    String stringValue = formData.get(key);
    if (stringValue == null || stringValue.isEmpty()) {
      if (required) {
        throw new ClientErrorException(String.format("Missing parameter '%s'", key));
      }
      return null;
    }
    return stringValue;
  }

  /**
   * Validates and retrieves a Long value from form data
   * 
   * @param key
   * @param formData
   * @param required
   * @return the value mapped to the provided key if it is set and not empty; otherwise null
   * @throws ClientErrorException if the value is required and missing or empty, or if the value is
   *         set and non-empty, but is not a valid Long
   */
  public static Long getLongInput(String key, Map<String, String> formData, boolean required) {
    String stringValue = getStringInput(key, formData, required);
    try {
      return stringValue == null ? null : new Long(stringValue);
    } catch (NumberFormatException e) {
      throw new ClientErrorException(String.format("Invalid value for parameter '%s'", key), e);
    }
  }

  /**
   * Validates and retrieves an Integer value from form data
   * 
   * @param key
   * @param formData
   * @param required
   * @return the value mapped to the provided key if it is set and not empty; otherwise null
   * @throws ClientErrorException if the value is required and missing or empty, or if the value is
   *         set and non-empty, but is not a valid Integer
   */
  public static Integer getIntegerInput(String key, Map<String, String> formData, boolean required) {
    String stringValue = getStringInput(key, formData, required);
    try {
      return stringValue == null ? null : Integer.valueOf(stringValue);
    } catch (NumberFormatException e) {
      throw new ClientErrorException(String.format("Invalid value for parameter '%s'", key), e);
    }
  }

  public static ModelAndView getQcHierarchy(String entityType, long id,
      ThrowingFunction<Long, SampleQcNode, IOException> getter,
      ModelMap model, ObjectMapper mapper) throws IOException {
    SampleQcNode hierarchy = getter.apply(id);
    if (hierarchy == null) {
      throw new NotFoundException(String.format("No %s found with ID %d", entityType, id));
    }

    model.put("title", String.format("%s %d Hierarchy", entityType, hierarchy.getId()));
    model.put("selectedType", entityType);
    model.put("selectedId", id);
    model.put("hierarchy", mapper.writeValueAsString(Dtos.asHierarchyDto(hierarchy)));

    return new ModelAndView("/WEB-INF/pages/qcHierarchy.jsp", model);
  }

  public static void addIssues(IssueTrackerManager issueTrackerManager,
      ThrowingSupplier<List<Issue>, IOException> getIssues,
      ModelMap model) {
    model.put("issueTrackerEnabled", issueTrackerManager != null);
    if (issueTrackerManager != null) {
      try {
        List<Issue> issues = getIssues.get();
        model.put("issueLookupError", false);
        model.put("issues", issues.stream().map(Dtos::asDto).collect(Collectors.toList()));
      } catch (IOException e) {
        log.error("Error retrieving issues", e);
        model.put("issueLookupError", true);
      }
    }
  }

  public static ArrayNode getLibraryQualificationMethodDtos(ObjectMapper mapper) {
    ArrayNode libraryQualificationMethods = mapper.createArrayNode();
    for (AssayTest.LibraryQualificationMethod method : AssayTest.LibraryQualificationMethod.values()) {
      ObjectNode dto = libraryQualificationMethods.addObject();
      dto.put("label", method.getLabel());
      dto.put("value", method.name());
    }
    return libraryQualificationMethods;
  }

}
