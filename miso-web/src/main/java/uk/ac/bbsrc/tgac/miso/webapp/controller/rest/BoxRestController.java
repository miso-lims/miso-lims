package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.BoxableDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.spring.util.FormUtils;

@Controller
@RequestMapping("/rest")
public class BoxRestController extends RestController {
  @Autowired
  private BoxService boxService;

  @Autowired
  private MisoFilesManager misoFileManager;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private SampleService sampleService;

  @Autowired
  private LibraryService libraryService;

  @Autowired
  private LibraryDilutionService libraryDilutionService;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSampleEnabled;

  @Resource
  private Map<String, BoxScanner> boxScanners;

  private final JQueryDataTableBackend<Box, BoxDto> jQueryBackend = new JQueryDataTableBackend<Box, BoxDto>() {
    @Override
    protected BoxDto asDto(Box model) {
      return Dtos.asDto(model, false);
    }

    @Override
    protected PaginatedDataSource<Box> getSource() throws IOException {
      return boxService;
    }
  };

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

  public void setBoxScanners(Map<String, BoxScanner> boxScanners) {
    this.boxScanners = boxScanners;
  }

  @RequestMapping(value = "/box/dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<BoxDto> dataTable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(value = "/box/dt/use/{id}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<BoxDto> dataTableByUse(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.boxUse(id));
  }

  @RequestMapping(value = "/boxes/rest/", method = RequestMethod.GET)
  public @ResponseBody Collection<Box> jsonRest() throws IOException {
    return boxService.list(0, 0, true, "id");
  }

  @RequestMapping(value = "/box/{boxId}/position/{position}", method = RequestMethod.PUT, consumes = { "application/json" },
      produces = { "application/json" })
  public @ResponseBody BoxDto setPosition(@PathVariable("boxId") Long boxId, @PathVariable("position") String position,
      @RequestParam("entity") String entity) throws IOException {
    BoxableId id = parseEntityIdentifier(entity);
    Box box = boxService.get(boxId);
    if (!box.isValidPosition(position)) {
      throw new RestException("Invalid position given: " + position, Status.BAD_REQUEST);
    }
    BoxableView boxable = boxService.getBoxableView(id);
    if (boxable == null) {
      throw new RestException(String.format("Item not found (type=%s, id=%s)", id.getTargetType(), id.getTargetId()),
          Status.BAD_REQUEST);
    } else if (boxable.isDiscarded()) {
      throw new RestException("Cannot add discarded item to box", Status.BAD_REQUEST);
    }
    // if the selected item is already in the box, remove it here and add it to the correct position in next step
    if (Long.valueOf(box.getId()).equals(boxable.getBoxId())) {
      box.removeBoxable(boxable.getBoxPosition());
    }
    // if an item already exists at this position, its location will be set to unknown.
    box.setBoxable(position, boxable);
    boxService.save(box);
    Box saved = boxService.get(boxId);
    return Dtos.asDto(saved, true);
  }

  @RequestMapping(value = "/boxes/search")
  public @ResponseBody List<BoxDto> search(@RequestParam("q") String search) {
    List<Box> results = boxService.getBySearch(search);
    return Dtos.asBoxDtos(results, true);
  }

  /**
   * Creates an Excel spreadsheet that contains the list of all Boxable items located in a particular position. Empty positions are not
   * listed.
   *
   * @param boxId ID of the Box
   * @return JSON object with "hashCode" field representing the hash code of the spreadsheet filename
   */
  @RequestMapping(value = "/box/{boxId}/spreadsheet", method = RequestMethod.GET)
  public @ResponseBody JSONObject createSpreadsheet(@PathVariable("boxId") Long boxId) {
    try {
      return exportBoxContentsForm(boxId);
    } catch (Exception e) {
      throw new RestException("Failed to get contents form", Status.BAD_REQUEST);
    }
  }

  private JSONObject exportBoxContentsForm(Long boxId) throws IOException  {
    Box box = boxService.get(boxId);

    List<List<String>> boxContents = getBoxContents(box);
    String name = box.getName();
    String alias = box.getAlias();

    File f = misoFileManager.getNewFile(Box.class, "forms", "BoxContentsForm-" + getCurrentDateAsString() + ".xlsx");
    if (detailedSampleEnabled) {
      FormUtils.createDetailedBoxSpreadsheet(f, name, alias, boxContents);
    } else {
      FormUtils.createPlainBoxSpreadsheet(f, name, alias, boxContents);
    }

    return JSONObject.fromObject("{hashCode: " + f.getName().hashCode() + "}");
  }

  /**
   * @param box
   * @return List of lists of strings, where each list of string represents a box
   */
  private List<List<String>> getBoxContents(Box box) throws IOException {
    List<List<String>> boxContents = new ArrayList<>();

    // Use TreeMap to iterate through the contents of the box in order of box position
    for (Map.Entry<String, BoxableView> entry : new TreeMap<>(box.getBoxables()).entrySet()) {
      String position = entry.getKey();
      BoxableView boxableView = entry.getValue();

      String name = boxableView.getName();
      String alias = boxableView.getAlias();
      String barcode = boxableView.getIdentificationBarcode();

      if (detailedSampleEnabled) {
        String externalName = findExternalName(boxableView);
        String numSlides = findNumSlides(boxableView);

        boxContents.add(Arrays.asList(position, name, alias, barcode, externalName, numSlides));
      } else {
        boxContents.add(Arrays.asList(position, name, alias, barcode));
      }
    }

    return boxContents;
  }

  private String findExternalName(BoxableView boxableView) throws IOException {
    DetailedSample detailedSample;

    switch(boxableView.getId().getTargetType()) {
    case SAMPLE:
      detailedSample = (DetailedSample) extractSample(boxableView);
      break;
    case LIBRARY:
      detailedSample = (DetailedSample) extractLibrary(boxableView).getSample();
      break;
    case DILUTION:
      detailedSample = (DetailedSample) extractDilution(boxableView).getLibrary().getSample();
      break;
    default: // Can't find external name for a Pool or otherwise
      return "n/a";
    }

    return extractSampleIdentity(detailedSample).getExternalName();
  }

  private SampleIdentity extractSampleIdentity(DetailedSample detailedSample) {
    while (!isIdentitySample(detailedSample)) {
      if (detailedSample == null) {
        throw new IllegalStateException("No identity found in hierarchy");
      }

      detailedSample = detailedSample.getParent();
    }

    // Load all fields from Hibernate object to enable cast to SampleIdentity
    return (SampleIdentity) deproxify(detailedSample);
  }

  private Sample extractSample(BoxableView boxableView) throws IOException {
    return sampleService.get(boxableView.getId().getTargetId());
  }

  private Library extractLibrary(BoxableView boxableView) throws IOException {
    return libraryService.get(boxableView.getId().getTargetId());
  }

  private LibraryDilution extractDilution(BoxableView boxableView) throws IOException {
    return libraryDilutionService.get(boxableView.getId().getTargetId());
  }

  private String findNumSlides(BoxableView boxableView) throws IOException {
    if (isSample(boxableView)) {
      DetailedSample sample = (DetailedSample) extractSample(boxableView);

      if (isSampleSlide(sample)) {
        return Integer.toString(((SampleSlide) sample).getSlides());
      }
    }

    return "n/a";
  }

  private boolean isSample(BoxableView boxableView) {
    return boxableView.getId().getTargetType() == EntityType.SAMPLE;
  }

  private static BoxableId parseEntityIdentifier(String identifier) {
    try {
      String[] pieces = identifier.split(":");
      EntityType et = EntityType.valueOf(pieces[0]);
      long id = Long.parseLong(pieces[1]);
      return new BoxableId(et, id);
    } catch (NullPointerException | IllegalArgumentException e) {
      throw new RestException("Invalid entity identifier: " + identifier, Status.BAD_REQUEST);
    }
  }

  public static class ScannerPreparationRequest {
    private String scannerName;
    private Integer rows;
    private Integer columns;

    public String getScannerName() {
      return scannerName;
    }

    public void setScannerName(String scannerName) {
      this.scannerName = scannerName;
    }

    public Integer getRows() {
      return rows;
    }

    public void setRows(Integer rows) {
      this.rows = rows;
    }

    public Integer getColumns() {
      return columns;
    }

    public void setColumns(Integer columns) {
      this.columns = columns;
    }
  }

  /**
   * Prepares the associated Box Scanner for scanning.
   * 
   * @param requestData
   */
  @RequestMapping(value = "/box/prepare-scan", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void prepareBoxScanner(@RequestBody(required = true) ScannerPreparationRequest requestData) {
    if (requestData.getRows() == null || requestData.getColumns() == null) {
      throw new RestException("Box size not specified", Status.BAD_REQUEST);
    } else if (isStringEmptyOrNull(requestData.getScannerName())) {
      throw new RestException("No scanner specified", Status.BAD_REQUEST);
    }
    BoxScanner boxScanner = boxScanners.get(requestData.getScannerName());
    if (boxScanner == null) {
      throw new RestException("Invalid scanner specified", Status.BAD_REQUEST);
    }
    try {
      boxScanner.prepareScan(requestData.getRows(), requestData.getColumns());
    } catch (IntegrationException e) {
      throw new RestException("Could not find the scanner", Status.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      throw new RestException("Could not find the scanner", Status.INTERNAL_SERVER_ERROR);
    }
  }

  public static class ScanRequest {
    private String scannerName;

    public String getScannerName() {
      return scannerName;
    }

    public void setScannerName(String scannerName) {
      this.scannerName = scannerName;
    }
  }

  public static class DiffMessage {
    private BoxableDto original;
    private BoxableDto modified;
    private String action;

    public BoxableDto getOriginal() {
      return original;
    }

    public void setOriginal(BoxableDto original) {
      this.original = original;
    }

    public BoxableDto getModified() {
      return modified;
    }

    public void setModified(BoxableDto modified) {
      this.modified = modified;
    }

    public String getAction() {
      return action;
    }

    public void setAction(String action) {
      this.action = action;
    }
  }

  public static class ErrorMessage {
    private String coordinates;
    private String message;

    public String getCoordinates() {
      return coordinates;
    }

    public void setCoordinates(String coordinates) {
      this.coordinates = coordinates;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }

  public static class ScanResultsDto {
    private List<BoxableDto> items;
    private List<ErrorMessage> errors;
    private List<DiffMessage> diffs;
    private int rows;
    private int columns;

    public List<BoxableDto> getItems() {
      return items;
    }

    public void setItems(List<BoxableDto> items) {
      this.items = items;
    }

    public List<ErrorMessage> getErrors() {
      return errors;
    }

    public void setErrors(List<ErrorMessage> errors) {
      this.errors = errors;
    }

    public List<DiffMessage> getDiffs() {
      return diffs;
    }

    public void setDiffs(List<DiffMessage> diffs) {
      this.diffs = diffs;
    }

    public int getRows() {
      return rows;
    }

    public void setRows(int rows) {
      this.rows = rows;
    }

    public int getColumns() {
      return columns;
    }

    public void setColumns(int columns) {
      this.columns = columns;
    }
  }

  /**
   * Gets the Box Scanner scan results (map of box positions and barcodes)
   * 
   * @param boxId
   * @param requestData
   * @return a serialized box object containing the Boxable items
   *         linked with each barcode at each position indicated by the scan results. Any errors are returned with a message containing the
   *         type of
   *         error (unable to read at certain positions; multiple items associated with a single barcode; unable to find box scanner), a
   *         message
   *         about the error, the positions that were successfully read (if applicable) and the positions at which the error was triggered
   *         (if
   *         applicable)
   */
  @RequestMapping(value = "/box/{boxId}/scan", method = RequestMethod.POST)
  public @ResponseBody ScanResultsDto getBoxScan(@PathVariable(required = true) int boxId,
      @RequestBody(required = true) ScanRequest requestData) {
    try {
      BoxScanner boxScanner = boxScanners.get(requestData.getScannerName());
      if (boxScanner == null) {
        throw new RestException("Invalid scanner specified", Status.BAD_REQUEST);
      }
      BoxScan scan = boxScanner.getScan();
      if (scan == null) {
        throw new RestException("The scanner did not detect a box!", Status.CONFLICT);
      }

      Map<String, String> barcodesByPosition = scan.getBarcodesMap();
      // Extract the valid barcodes and build a barcode to item map
      Set<String> validBarcodes = barcodesByPosition.values().stream()
          .filter(barcode -> isRealBarcode(scan, barcode)).collect(Collectors.toSet());
      Map<String, BoxableView> boxablesByBarcode = boxService.getViewsFromBarcodeList(validBarcodes).stream()
          .collect(Collectors.toMap(BoxableView::getIdentificationBarcode, Function.identity()));

      // For all the valid barcodes, build a list of DTOs with the updated positions
      List<BoxableDto> items = barcodesByPosition.entrySet().stream()
          .filter(entry -> isRealBarcode(scan, entry.getValue()) && boxablesByBarcode.containsKey(entry.getValue()))
          .map(entry -> {
            BoxableDto dto = Dtos.asDto(boxablesByBarcode.get(entry.getValue()));
            dto.setCoordinates(entry.getKey());
            return dto;
          }).collect(Collectors.toList());

      // Collect all the errors
      List<ErrorMessage> errors = new ArrayList<>();

      // If there's a barcode that wasn't found in the DB, create an error.
      barcodesByPosition.entrySet().stream()
          .filter(entry -> isRealBarcode(scan, entry.getValue()) && !boxablesByBarcode.containsKey(entry.getValue())).map(entry -> {
            ErrorMessage dto = new ErrorMessage();
            dto.setCoordinates(entry.getKey());
            dto.setMessage("Barcode " + entry.getValue() + " not found.");
            return dto;
          }).forEachOrdered(errors::add);
      // If there was a read error, produce an error.
      scan.getReadErrorPositions().stream().map(position -> {
        ErrorMessage dto = new ErrorMessage();
        dto.setCoordinates(position);
        dto.setMessage("Cannot read tube.");
        return dto;
      }).forEachOrdered(errors::add);

      long totalBarcodes = barcodesByPosition.values().stream().filter(barcode -> isRealBarcode(scan, barcode)).count();
      if (validBarcodes.size() != totalBarcodes) {
        ErrorMessage dto = new ErrorMessage();
        dto.message = "Duplicate barcodes detected!";
        errors.add(dto);
      }

      // Build the diffs for this box
      List<DiffMessage> diffs = new ArrayList<>();
      Box box;
      try {
        box = boxService.get(boxId);
      } catch (IOException e) {
        throw new RestException("Cannot get the Box: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
      }
      if (box.getSize().getRows() != scan.getRowCount() || box.getSize().getColumns() != scan.getColumnCount())
        throw new RestException(String.format("Box is %d×%d, but scanner detected %d×%d.", box.getSize().getRows(),
            box.getSize().getColumns(), scan.getRowCount(), scan.getColumnCount()), Status.BAD_REQUEST);

      box.getSize().positionStream().map(position -> {
        BoxableView originalItem = box.getBoxables().containsKey(position) ? box.getBoxables().get(position) : null;
        BoxableView newItem = barcodesByPosition.containsKey(position) && boxablesByBarcode.containsKey(barcodesByPosition.get(position))
            ? boxablesByBarcode.get(barcodesByPosition.get(position)) : null;
        if (originalItem != null && newItem != null &&
            !newItem.getIdentificationBarcode().equals(originalItem.getIdentificationBarcode())) {
          DiffMessage dto = new DiffMessage();
          dto.action = "changed";
          dto.modified = Dtos.asDto(newItem);
          dto.original = Dtos.asDto(originalItem);
          return dto;
        } else if (originalItem != null && newItem == null) {
          DiffMessage dto = new DiffMessage();
          dto.action = "removed";
          dto.original = Dtos.asDto(originalItem);
          return dto;
        } else if (originalItem == null && newItem != null) {
          DiffMessage dto = new DiffMessage();
          dto.action = "added";
          dto.modified = Dtos.asDto(newItem);
          return dto;
        } else {
          return null;
        }
      }).filter(Objects::nonNull);

      ScanResultsDto scanResults = new ScanResultsDto();
      scanResults.setItems(items);
      scanResults.setErrors(errors);
      scanResults.setDiffs(diffs);
      scanResults.setRows(scan.getRowCount());
      scanResults.setColumns(scan.getColumnCount());
      return scanResults;
    } catch (IntegrationException | IOException e) {
      throw new RestException("Error scanning box: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  private static boolean isRealBarcode(BoxScan scan, String barcode) {
    return !barcode.equals(scan.getNoTubeLabel()) && !barcode.equals(scan.getNoReadLabel());
  }

  @RequestMapping(value = "/box/{boxId}/discard-all")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void discardEntireBox(@PathVariable long boxId) throws IOException {
    Box box = boxService.get(boxId);
    if (box == null) {
      throw new RestException("Box " + boxId + " not found", Status.NOT_FOUND);
    }
    boxService.discardAllContents(box);
  }
  
  public static class BulkUpdateRequestItem {
    private String position;
    private String searchString;
    
    public String getPosition() {
      return position;
    }

    public void setPosition(String position) {
      this.position = position;
    }

    public String getSearchString() {
      return searchString;
    }
    
    public void setSearchString(String searchString) {
      this.searchString = searchString;
    }

  }
  
  @RequestMapping(value = "/box/{boxId}/bulk-update", method = RequestMethod.POST)
  public @ResponseBody BoxDto bulkUpdatePositions(@PathVariable long boxId, @RequestBody List<BulkUpdateRequestItem> items) throws IOException {
    Box box = boxService.get(boxId);
    if (box == null) {
      throw new RestException("Box " + boxId + " not found", Status.NOT_FOUND);
    }
    ValidationResult validation = new ValidationResult();
    Map<String, BoxableView> updates = new HashMap<>();
    for (BulkUpdateRequestItem item : items) {
      if (!box.isValidPosition(item.getPosition())) {
        validation.addError(new ValidationError("Invalid position given: " + item.getPosition()));
      }
      List<BoxableView> searchResults = boxService.getBoxableViewsBySearch(item.getSearchString());
      if (searchResults == null || searchResults.isEmpty()) {
        validation.addError(
            new ValidationError("No item found by searching '" + item.getSearchString() + "' for position " + item.getPosition()));
      } else if (searchResults.size() > 1) {
        validation.addError(
            new ValidationError("Multiple items matched search '" + item.getSearchString() + "' for position " + item.getPosition()));
      } else {
        BoxableView boxable = searchResults.get(0);
        // if the selected item is already in the box, remove it here and add it to the correct position in next step
        if (Long.valueOf(box.getId()).equals(boxable.getBoxId())) {
          box.removeBoxable(boxable.getBoxPosition());
        }
        updates.put(item.getPosition(), boxable);
      }
    }
    for (Entry<String, BoxableView> entry : updates.entrySet()) {
      // if an item already exists at this position, its location will be set to unknown.
      box.setBoxable(entry.getKey(), entry.getValue());
    }

    validation.throwIfInvalid();
    boxService.save(box);
    Box updated = boxService.get(boxId);
    return Dtos.asDto(updated, true);
  }

}
