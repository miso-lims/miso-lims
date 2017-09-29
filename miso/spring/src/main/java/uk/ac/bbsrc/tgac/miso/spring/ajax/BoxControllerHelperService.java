package uk.ac.bbsrc.tgac.miso.spring.ajax;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.BoxableDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.spring.util.FormUtils;

@Ajaxified
public class BoxControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(BoxControllerHelperService.class);

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private BoxService boxService;

  @Autowired
  private MisoFilesManager misoFileManager;

  @Autowired
  private BoxScanner boxScanner;

  /**
   * Return one boxable associated with a particular barcode. json must contain the following entries: "barcode": barcode
   * 
   * @param HttpSession
   *          session
   * @param JSONObject
   *          json
   * @return JSONObject Boxable
   */
  public JSONObject lookupBoxableByBarcode(HttpSession session, JSONObject json) {
    JSONObject response = new JSONObject();
    if (json.has("barcode")) {
      BoxableView boxable;
      String barcode = json.getString("barcode");
      try {
        boxable = boxService.getViewByBarcode(barcode);
      } catch (IOException e) {
        log.debug("Error getting boxable by barcode", e);
        return JSONUtils.SimpleJSONError("Error looking up barcode " + json.getString("barcode") + ": " + e.getMessage());
      }

      if (boxable == null) {
        return JSONUtils.SimpleJSONError(
            "Could not find barcode " + barcode + ". Confirm that it is associated with a sample, library, or pool before retrying.");
      }

      response.put("boxable", Dtos.asDto(boxable));
      if (boxable.isDiscarded())
        response.put("trashed", boxable.getName() + " (" + boxable.getAlias() + ") has been discarded, and can not be added to the box.");
      return response;
    } else {
      return JSONUtils.SimpleJSONError("Please enter a barcode.");
    }
  }

  /**
   * Deletes a box from the database. Requires admin permissions.
   * 
   * @param session
   * @param json
   *          must contain the entry "boxId"
   * @return message indicating success or error
   */
  public JSONObject deleteBox(HttpSession session, JSONObject json) {
    User user;
    try {
      user = authorizationManager.getCurrentUser();
    } catch (IOException e) {
      log.debug("Error getting currently logged in user", e);
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null) { // && user.isAdmin()) {
      if (json.has("boxId")) {
        Long boxId = json.getLong("boxId");
        try {
          boxService.deleteBox(boxService.get(boxId));
          return JSONUtils.SimpleJSONResponse("Box deleted");
        } catch (IOException e) {
          log.debug("Cannot delete box", e);
          return JSONUtils.SimpleJSONError("Cannot delete box: " + e.getMessage());
        }
      } else {
        log.debug("No box specified to delete");
        return JSONUtils.SimpleJSONError("No box specified to delete.");
      }
    } else {
      log.debug("Only logged-in admins can delete objects.");
      return JSONUtils.SimpleJSONError("Only logged-in admins can delete objects.");
    }
  }

  /**
   * Saves the relationships between box positions and the boxables within them. Note: this method will delete everything that was
   * previously in the box, and then replace them with the boxables as per the input json (all modifications happen in BoxPosition table).
   * The boxables themselves are not modified in the database.
   * 
   * @param HttpSession
   *          session
   * @param JSONObject
   *          json
   * @return JSON message indicating success or error
   */
  public JSONObject saveBoxContents(HttpSession session, JSONObject json) {
    if (!json.has("boxId")) {
      return JSONUtils.SimpleJSONError("Cannot find box.");
    }
    if (!json.has("items")) {
      return JSONUtils.SimpleJSONError("Cannot find items.");
    }
    Box box;
    long boxId = json.getLong("boxId");
    try {
      box = boxService.get(boxId);
      if (box == null)
        return JSONUtils.SimpleJSONError("Cannot find box.");
    } catch (IOException e) {
      log.debug("Error getting box with ID " + boxId, e);
      return JSONUtils.SimpleJSONError("Error looking up this box: " + e.getMessage());
    }

    JSONArray array = json.getJSONArray("items");
    Map<String, String> positionToBarcode = new HashMap<>();
    for (int i = 0; i < array.size(); i++) {
      JSONObject item = array.getJSONObject(i);
      positionToBarcode.put(item.getString("coordinates"), item.getString("identificationBarcode"));
    }

    try {
      Map<String, BoxableView> barcodesToBoxables = boxService.getViewsFromBarcodeList(positionToBarcode.values()).stream()
          .collect(Collectors.toMap(BoxableView::getIdentificationBarcode, Function.identity()));
      box.setBoxables(positionToBarcode.entrySet().stream()
          .collect(Collectors.toMap(Map.Entry::getKey, entry -> barcodesToBoxables.get(entry.getValue()))));
    } catch (IOException e) {
      log.debug("Error getting boxable", e);
      return JSONUtils.SimpleJSONError("Error finding item: " + e.getMessage());
    }

    try {
      boxService.save(box);
    } catch (IOException e) {
      log.debug("Error saving box", e);
      return JSONUtils.SimpleJSONError("Error saving box contents: " + e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Box was successfully saved");
  }

  private static final Map<String, Function<BoxSize, BiFunction<Integer, Integer, String>>> SUFFIXES = ImmutableMap
      .<String, Function<BoxSize, BiFunction<Integer, Integer, String>>> builder().put("standard", size -> BoxUtils::getPositionString)
      .put("numeric", size -> (row, column) -> String.format("%03d", row * size.getColumns() + column)).build();

  public JSONObject recreateBoxFromPrefix(HttpSession session, JSONObject json) {
    if (!json.has("boxId")) {
      return JSONUtils.SimpleJSONError("Cannot find box.");
    }
    if (!json.has("prefix")) {
      return JSONUtils.SimpleJSONError("Cannot find prefix.");
    }
    if (!json.has("suffix")) {
      return JSONUtils.SimpleJSONError("Cannot find suffix.");
    }
    Box box;
    long boxId = json.getLong("boxId");
    try {
      box = boxService.get(boxId);
      if (box == null)
        return JSONUtils.SimpleJSONError("Cannot find box.");
    } catch (IOException e) {
      log.debug("Error getting box with ID " + boxId, e);
      return JSONUtils.SimpleJSONError("Error looking up this box: " + e.getMessage());
    }

    String prefix = json.getString("prefix");
    String suffix = json.getString("suffix");
    if (!SUFFIXES.containsKey(suffix)) {
      return JSONUtils.SimpleJSONError("Invalid suffix.");
    }
    BiFunction<Integer, Integer, String> suffixGenerator = SUFFIXES.get(suffix).apply(box.getSize());
    Map<String, String> positionToBarcode = new HashMap<>();
    for (int row = 0; row < box.getSize().getRows(); row++) {
      for (int column = 0; column < box.getSize().getColumns(); column++) {
        positionToBarcode.put(BoxUtils.getPositionString(row, column), prefix + suffixGenerator.apply(row, column));
      }
    }
    try {
      Map<String, BoxableView> barcodesToBoxables = boxService.getViewsFromBarcodeList(positionToBarcode.values()).stream()
          .collect(Collectors.toMap(BoxableView::getIdentificationBarcode, Function.identity()));
      box.setBoxables(positionToBarcode.entrySet().stream().filter(entry -> barcodesToBoxables.containsKey(entry.getValue()))
          .collect(Collectors.toMap(Map.Entry::getKey,
              entry -> barcodesToBoxables.get(entry.getValue()))));
    } catch (IOException e) {
      log.debug("Error getting boxable", e);
      return JSONUtils.SimpleJSONError("Error finding item: " + e.getMessage());
    }

    try {
      boxService.save(box);
    } catch (IOException e) {
      log.debug("Error saving box", e);
      return JSONUtils.SimpleJSONError("Error saving box contents: " + e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Box was successfully saved");
  }

  /**
   * Confirms that a box has an alias.
   * 
   * @param HttpSession
   *          session
   * @param JSONObject
   *          json
   * @return JSON message indicating success or error
   */
  public JSONObject validateBoxInput(HttpSession session, JSONObject json) {
    if (json.has("alias")) {
      String alias = json.getString("alias");
      if (alias.equals("")) {
        log.error("Alias field cannot be empty!");
        return JSONUtils.SimpleJSONError("The Alias cannot be empty!");
      } else {
        return JSONUtils.SimpleJSONResponse("OK");
      }
    } else {
      return JSONUtils.SimpleJSONError("Please specify a box alias.");
    }
  }

  /**
   * <p>
   * Scans an individual item (using handheld scanner or keyboard) and: <br>
   * a) if the item doesn't exist in the box: add the item to the selected position <br>
   * b) if the item does exist in the box, but in a different location: move the item to the selected position
   * </p>
   * 
   * <p>
   * Note: this method saves all box contents to the database.
   * </p>
   *
   * @param session
   * @param json
   *          must contain the following entries:
   *          <ul>
   *          <li>"boxId" : boxId</li>
   *          <li>"barcode" : barcode of boxable to store in the position</li>
   *          <li>"position" : a String representing the box row and column to store the boxable in. eg. "H12"</li>
   *          </ul>
   * @return JSONObject message indicating success or error
   */
  public JSONObject updateOneItem(HttpSession session, JSONObject json) {
    if (!json.has("boxId") || !json.has("barcode") || !json.has("position")) {
      return JSONUtils.SimpleJSONError("Invalid boxId, barcode or position given.");
    }

    long boxId = json.getLong("boxId");
    String barcode = json.getString("barcode");
    String position = json.getString("position");

    Box box = null;
    try {
      box = boxService.get(boxId);
      if (box == null) throw new IOException("Box not found");
    } catch (IOException e) {
      log.debug("Error getting box with ID " + boxId, e);
      return JSONUtils.SimpleJSONError("Error looking up this box: " + e.getMessage());
    }

    if (!box.isValidPosition(position)) return JSONUtils.SimpleJSONError("Invalid position given!");

    // get the requested Sample/Library from the db
    BoxableView boxable = null;
    try {
      boxable = boxService.getViewByBarcode(barcode);
    } catch (IOException e) {
      log.debug("Error getting boxable", e);
      return JSONUtils.SimpleJSONError("Error finding item with barcode " + barcode + ": " + e.getMessage());
    }

    if (boxable == null) return JSONUtils.SimpleJSONError("Could not find sample, library or pool with barcode " + barcode
        + ". Please associate this barcode with a sample, library or pool before retrying.");
    if (boxable.isDiscarded()) return JSONUtils
        .SimpleJSONError(boxable.getName() + " (" + boxable.getAlias() + ") has been discarded, and can not be added to the box.");

    // if the selected item is already in the box, remove it here and add it to the correct position in next step
    String oldPos = null;
    for (Map.Entry<String, BoxableView> entry : box.getBoxables().entrySet()) {
      if (entry.getValue().getId().equals(boxable.getId())) {
        oldPos = entry.getKey();
        break;
      }
    }
    if (oldPos != null) box.removeBoxable(oldPos);

    // if an item already exists at this position, its location will be set to unknown.
    box.setBoxable(position, boxable);
    log.info("Adding " + boxable.getName() + " to " + box.getName());

    Map<String, Object> response = new HashMap<>();
    try {
      box.setLastModifier(authorizationManager.getCurrentUser());
      boxService.save(box);

      ObjectMapper mapper = new ObjectMapper();
      response.put("boxJSON", mapper.writer().writeValueAsString(Dtos.asDto(boxService.get(box.getId()), true)));
      response.put("addedToBox", boxable.getName() + " was successfully added to position " + position);
    } catch (IOException e) {
      log.debug("Error updating one boxable item", e);
      return JSONUtils.SimpleJSONError("Error updating one item:" + e.getMessage());
    }

    return JSONUtils.JSONObjectResponse(response);
  }

  /**
   * Removes one Boxable element from box (sets its location to unknown)
   * 
   * @param session
   * @param json
   *          must contain the following fields:
   *          <ul>
   *          <li>"boxId": boxId</li>
   *          <li>"position": a String representing the box row and column to store the boxable in. eg. "H12"</li>
   *          </ul>
   *          </p>
   * @return JSONObject message indicating failure or success
   */
  public JSONObject removeTubeFromBox(HttpSession session, JSONObject json) {
    if (!json.has("boxId") || !json.has("position")) {
      return JSONUtils.SimpleJSONError("Invalid boxId or position given.");
    }

    long boxId = json.getLong("boxId");
    Box box = null;
    try {
      box = boxService.get(boxId);
      if (box == null) throw new IOException("Box not found");
    } catch (IOException e) {
      log.debug("Error getting box with ID " + boxId, e);
      return JSONUtils.SimpleJSONError("Error looking up this box: " + e.getMessage());
    }

    String position = json.getString("position");
    if (!box.isValidPosition(position)) return JSONUtils.SimpleJSONError("Invalid position given!");

    box.removeBoxable(position);

    try {
      box.setLastModifier(authorizationManager.getCurrentUser());
      boxService.save(box);

      Map<String, Object> response = new HashMap<>();
      ObjectMapper mapper = new ObjectMapper();
      response.put("boxJSON", mapper.writer().writeValueAsString(Dtos.asDto(boxService.get(box.getId()), true)));
      return JSONUtils.JSONObjectResponse(response);
    } catch (IOException e) {
      log.debug("Error removing one boxable item", e);
      return JSONUtils.SimpleJSONError("Error removing one item: " + e.getMessage());
    }
  }

  /**
   * Modifies one Boxable element for the purpose of being thrown out by the user. Sets boxable.volume to 0, boxable.discarded to true, and
   * removes the boxable element from the box.
   * 
   * Note: json must contain the following key-value pairs: "boxId": boxId, "position": position
   * 
   * @param HttpSession
   *          session, JSONObject json
   * @returns JSONObject message indicating failure or success
   */
  public JSONObject discardSingleTube(HttpSession session, JSONObject json) {
    User user;
    Box box;
    JSONObject response = new JSONObject();
    try {
      user = authorizationManager.getCurrentUser();
    } catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null) {
      if (json.has("boxId") && json.has("position")) {
        String position = json.getString("position");
        Long boxId = json.getLong("boxId");
        try {
          box = boxService.get(boxId);
        } catch (IOException e) {
          log.debug("Error: ", e);
          return JSONUtils.SimpleJSONError("Cannot get the Box: " + e.getMessage());
        }

        if (box.isFreePosition(position)) {
          return JSONUtils.SimpleJSONError("No item to discard at position " + position + "!");
        }
        if (!box.isValidPosition(position)) {
          return JSONUtils.SimpleJSONError("Invalid position selected!");
        }

        try {
          boxService.discardSingleTube(box, position);
          box = boxService.get(boxId);
          ObjectMapper mapper = new ObjectMapper();
          response.put("boxJSON", mapper.writer().writeValueAsString(Dtos.asDto(boxService.get(box.getId()), true)));
        } catch (IOException e) {
          log.debug("Failed to discard single tube", e);
          return JSONUtils.SimpleJSONError("Failed to discard single tube: " + e.getMessage());
        }

      } else {
        return JSONUtils.SimpleJSONError("Please select a position to discard");
      }
    }
    return response;
  }

  /**
   * Modifies all Boxable elements to be thrown in the trash. For each boxable element, boxable.volume is set to 0, boxable.discarded to
   * true,
   * and all boxable elements are removed from the box. Note: json must contain the key-value pair of "boxId" : boxId
   *
   * @param session
   * @param json
   *          must contain a "boxId" field
   * @returns JSONObject message indicating failure or success
   */
  public JSONObject discardEntireBox(HttpSession session, JSONObject json) {
    User user;
    Box box;
    JSONObject response = new JSONObject();
    try {
      user = authorizationManager.getCurrentUser();
    } catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("boxId")) {
        Long boxId = json.getLong("boxId");
        try {
          box = boxService.get(boxId);
        } catch (IOException e) {
          log.debug("Error", e);
          return JSONUtils.SimpleJSONError("Error getting box: " + e.getMessage());
        }

        try {
          boxService.discardAllTubes(box); // box save is performed as part of this method
          box = boxService.get(boxId);
          ObjectMapper mapper = new ObjectMapper();
          response.put("boxJSON", mapper.writer().writeValueAsString(Dtos.asDto(boxService.get(box.getId()), true)));
          return response;
        } catch (IOException e) {
          log.debug("Error discarding box", e);
          return JSONUtils.SimpleJSONError("Error discarding box: " + e.getMessage());
        }
      } else {
        return JSONUtils.SimpleJSONError("No box specified to discard.");
      }
    } else {
      return JSONUtils.SimpleJSONError("Only logged-in admins can discard boxes.");
    }
  }

  /**
   * Prepares the associated Box Scanner for scanning.
   * 
   * Note: json must contain the following key-value pairs: "rows": rows in box "columns": columns in box
   * 
   * @param HttpSession
   *          session
   * @param JSONObject
   *          json
   * @return JSON message indicating success or error
   */
  public JSONObject prepareBoxScanner(HttpSession session, JSONObject json) {
    if (json.has("rows") && json.has("columns")) {
      int rows = json.getInt("rows");
      int columns = json.getInt("columns");
      try {
        boxScanner.prepareScan(rows, columns);
      } catch (IntegrationException e) {
        log.error("Integration error preparing scanner", e);
        return JSONUtils.SimpleJSONError("Error: could not find the scanner");
      } catch (Exception e) {
        log.error("General error preparing scanner", e);
        return JSONUtils.SimpleJSONError("Error: could not find the scanner");
      }
      return JSONUtils.SimpleJSONResponse("OK");
    } else {
      return JSONUtils.SimpleJSONError("Could not determine box size. Please save box and try again.");
    }
  }

  private static boolean hasRealBarcode(BoxScan scan, String position) {
    return !position.equals(scan.getNoTubeLabel()) && !position.equals(scan.getNoReadLabel());
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

  /**
   * Gets the Box Scanner scan results (map of box positions and barcodes) and returns a serialized box object containing the Boxable item
   * linked with each barcode at each position indicated by the scan results. Any errors are returned with a message containing the type of
   * error (unable to read at certain positions; multiple items associated with a single barcode; unable to find box scanner), a message
   * about the error, the positions that were successfully read (if applicable) and the positions at which the error was triggered (if
   * applicable).
   * 
   * Note: json must contain the following key-value pairs: "boxId": boxId
   * 
   * @param session
   * @param json
   * @return
   */
  public JSONObject getBoxScan(HttpSession session, JSONObject json) {
    if (!json.has("boxId")) {
      return JSONUtils.SimpleJSONError("Cannot find box.");
    }
    // get scan
    try {
      BoxScan scan = boxScanner.getScan();
      if (scan == null) {
        return JSONUtils.SimpleJSONError("The scanner did not detect a box!");
      }

      Map<String, String> barcodesByPosition = scan.getBarcodesMap();
      // Extract the valid barcodes and build a barcode to item map
      Set<String> validBarcodes = barcodesByPosition.values().stream()
          .filter(position -> hasRealBarcode(scan, position)).collect(Collectors.toSet());
      Map<String, BoxableView> boxablesByBarcode = boxService.getViewsFromBarcodeList(validBarcodes).stream()
          .collect(Collectors.toMap(BoxableView::getIdentificationBarcode, Function.identity()));

      // For all the valid barcodes, build a list of DTOs with the updated positions
      List<BoxableDto> items = barcodesByPosition.entrySet().stream()
          .filter(entry -> hasRealBarcode(scan, entry.getKey()) && boxablesByBarcode.containsKey(entry.getValue()))
          .map(entry -> {
            BoxableDto dto = Dtos.asDto(boxablesByBarcode.get(entry.getValue()));
            dto.setCoordinates(entry.getKey());
            return dto;
          }).collect(Collectors.toList());

      // Collect all the errors
      List<ErrorMessage> errors = new ArrayList<>();

      // If there's a barcode that wasn't found in the DB, create an error.
      barcodesByPosition.entrySet().stream()
          .filter(entry -> hasRealBarcode(scan, entry.getKey()) && !boxablesByBarcode.containsKey(entry.getValue())).map(entry -> {
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

      long totalBarcodes = barcodesByPosition.values().stream().filter(position -> hasRealBarcode(scan, position)).count();
      if (validBarcodes.size() != totalBarcodes) {
        ErrorMessage dto = new ErrorMessage();
        dto.message = "Duplicate barcodes detected!";
        errors.add(dto);
      }

      // Build the diffs for this box
      List<DiffMessage> diffs = new ArrayList<>();
      Box box;
      try {
        box = boxService.get(json.getLong("boxId"));
      } catch (IOException e) {
        log.debug("Error: ", e);
        return JSONUtils.SimpleJSONError("Cannot get the Box: " + e.getMessage());
      }
      if (box.getSize().getRows() != scan.getRowCount() || box.getSize().getColumns() != scan.getColumnCount())
        return JSONUtils.SimpleJSONError(String.format("Box is %d×%d, but scanner detected %d×%d.", box.getSize().getRows(),
            box.getSize().getColumns(), scan.getRowCount(), scan.getColumnCount()));

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

      JSONObject scanResults = new JSONObject();
      scanResults.put("items", items);
      scanResults.put("errors", errors);
      scanResults.put("diffs", diffs);
      scanResults.put("rows", scan.getRowCount());
      scanResults.put("cols", scan.getColumnCount());
      return scanResults;
    } catch (IntegrationException | IOException e) {
      log.error(e.getMessage());
      return JSONUtils.SimpleJSONError("Error scanning box: " + e.getMessage());
    }
  }

  /**
   * Creates an Excel spreadsheet that contains the list of all Boxable items located in a particular position. Empty positions are not
   * listed.
   * 
   * Note: json must contain the following key-value pairs: "boxId": boxId
   * 
   * @param session
   * @param json
   * @return JSON message indicating success (and hash code of newly-created file name on disk) or error
   */
  public JSONObject exportBoxContentsForm(HttpSession session, JSONObject json) {
    if (json.has("boxId")) {
      Long boxId = json.getLong("boxId");
      try {
        ArrayList<String> array = new ArrayList<>();

        Box box = boxService.get(boxId);
        array.add(box.getName() + ":" + box.getAlias());
        Map<String, BoxableView> boxableItems = new TreeMap<>(box.getBoxables()); // sorted by position
        for (Map.Entry<String, BoxableView> entry : boxableItems.entrySet()) {
          String arrayEntry = entry.getKey() + ":" + entry.getValue().getName() + ":" + entry.getValue().getAlias();
          array.add(arrayEntry);
        }

        File f = misoFileManager.getNewFile(Box.class, "forms", "BoxContentsForm-" + LimsUtils.getCurrentDateAsString() + ".xlsx");
        FormUtils.createBoxContentsSpreadsheet(f, array);
        return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
      } catch (Exception e) {
        log.debug("failed to create box contents form");
        return JSONUtils.SimpleJSONError("Failed to get box contents form: " + e.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("Missing boxId");
    }
  }

  @CoverageIgnore
  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  @CoverageIgnore
  public void setBoxScanner(BoxScanner boxScanner) {
    this.boxScanner = boxScanner;
  }

  @CoverageIgnore
  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;

  }
}
