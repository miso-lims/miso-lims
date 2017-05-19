package uk.ac.bbsrc.tgac.miso.spring.ajax;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;
import static uk.ac.bbsrc.tgac.miso.spring.ControllerHelperServiceUtils.getBarcodeFileLocation;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.BoxableDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import uk.ac.bbsrc.tgac.miso.service.PrinterService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.spring.ControllerHelperServiceUtils;
import uk.ac.bbsrc.tgac.miso.spring.ControllerHelperServiceUtils.BarcodePrintAssister;
import uk.ac.bbsrc.tgac.miso.spring.util.FormUtils;

@Ajaxified
public class BoxControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(BoxControllerHelperService.class);

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private MisoFilesManager misoFileManager;

  @Autowired
  private PrinterService printerService;

  @Autowired
  private BoxScanner boxScanner;

  /**
   * Returns a JSONObject of HTML for making the /miso/boxes table
   * 
   * @param HttpSession
   *          session
   * @param JSONObject
   *          json
   * @return JSONObject data for all boxes
   */
  public JSONObject listAllBoxesTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Box box : requestManager.listAllBoxes()) {
        JSONArray inner = new JSONArray();

        inner.add(TableHelper.hyperLinkify("/miso/box/" + box.getId(), box.getName()));
        inner.add(TableHelper.hyperLinkify("/miso/box/" + box.getId(), box.getAlias()));
        inner.add(box.getLocationBarcode());
        inner.add(box.getTubeCount() + "/" + box.getPositionCount());
        inner.add(box.getSize().getRows() + "x" + box.getSize().getColumns());
        inner.add(box.getUse().getAlias());
        inner.add(isStringEmptyOrNull(box.getIdentificationBarcode()) ? "" : box.getIdentificationBarcode());
        inner.add(box.getId());

        jsonArray.add(inner);
      }
      j.put("array", jsonArray);
      return j;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

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
        boxable = requestManager.getBoxableViewByBarcode(barcode);
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
          requestManager.deleteBox(requestManager.getBoxById(boxId));
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
      box = requestManager.getBoxById(boxId);
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
      Map<String, BoxableView> barcodesToBoxables = requestManager.getBoxableViewsFromBarcodeList(positionToBarcode.values()).stream()
          .collect(Collectors.toMap(BoxableView::getIdentificationBarcode, Function.identity()));
      box.setBoxables(positionToBarcode.entrySet().stream()
          .collect(Collectors.toMap(Map.Entry::getKey, entry -> barcodesToBoxables.get(entry.getValue()))));
    } catch (IOException e) {
      log.debug("Error getting boxable", e);
      return JSONUtils.SimpleJSONError("Error finding item: " + e.getMessage());
    }

    try {
      requestManager.saveBox(box);
    } catch (IOException e) {
      log.debug("Error saving box", e);
      return JSONUtils.SimpleJSONError("Error saving box contents: " + e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Box was successfully saved");
  }

  private Map<String, BoxableView> loadBoxables(JSONObject boxJson) throws IOException {
    JSONArray boxablesJson = boxJson.getJSONArray("boxables");
    Map<String, String> barcodeToPosition = new HashMap<>();
    for (int i = 0; i < boxablesJson.size(); i++) {
      JSONObject entry = boxablesJson.getJSONObject(i);
      barcodeToPosition.put(entry.getString("identificationBarcode"), entry.getString("coordinates"));
    }

    Map<String, BoxableView> map = new HashMap<>();
    for (BoxableView boxable : requestManager.getBoxableViewsFromBarcodeList(barcodeToPosition.keySet())) {
      map.put(barcodeToPosition.get(boxable.getIdentificationBarcode()), boxable);
    }
    if (map.size() != barcodeToPosition.size()) {
      throw new IOException("No boxable found with supplied barcode.");
    }
    return map;
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
      box = requestManager.getBoxById(boxId);
      if (box == null) throw new IOException("Box not found");
    } catch (IOException e) {
      log.debug("Error getting box with ID " + boxId, e);
      return JSONUtils.SimpleJSONError("Error looking up this box: " + e.getMessage());
    }

    if (!box.isValidPosition(position)) return JSONUtils.SimpleJSONError("Invalid position given!");

    // get the requested Sample/Library from the db
    BoxableView boxable = null;
    try {
      boxable = requestManager.getBoxableViewByBarcode(barcode);
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
      requestManager.saveBox(box);

      ObjectMapper mapper = new ObjectMapper();
      response.put("boxJSON", mapper.writer().writeValueAsString(Dtos.asDto(requestManager.getBoxById(box.getId()))));
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
      box = requestManager.getBoxById(boxId);
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
      requestManager.saveBox(box);

      Map<String, Object> response = new HashMap<>();
      ObjectMapper mapper = new ObjectMapper();
      response.put("boxJSON", mapper.writer().writeValueAsString(Dtos.asDto(requestManager.getBoxById(box.getId()))));
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
          box = requestManager.getBoxById(boxId);
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
          requestManager.discardSingleTube(box, position);
          box = requestManager.getBoxById(boxId);
          ObjectMapper mapper = new ObjectMapper();
          response.put("boxJSON", mapper.writer().writeValueAsString(Dtos.asDto(requestManager.getBoxById(box.getId()))));
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
          box = requestManager.getBoxById(boxId);
        } catch (IOException e) {
          log.debug("Error", e);
          return JSONUtils.SimpleJSONError("Error getting box: " + e.getMessage());
        }

        try {
          requestManager.discardAllTubes(box); // box save is performed as part of this method
          box = requestManager.getBoxById(boxId);
          ObjectMapper mapper = new ObjectMapper();
          response.put("boxJSON", mapper.writer().writeValueAsString(Dtos.asDto(requestManager.getBoxById(box.getId()))));
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
   * Changes the Box's location to a user-entered value.
   * 
   * @param HttpSession
   *          session, JSONObject json
   * @return JSON message indicating success or error
   */
  public JSONObject changeBoxLocation(HttpSession session, JSONObject json) {
    Long boxId = json.getLong("boxId");
    String locationBarcode = json.getString("locationBarcode");

    try {
      String newLocation = LimsUtils.lookupLocation(locationBarcode);
      if (!"".equals(newLocation)) {
        Box box = requestManager.getBoxById(boxId);
        box.setLocationBarcode(newLocation);
        box.setLastModifier(authorizationManager.getCurrentUser());
        requestManager.saveBox(box);
      } else {
        return JSONUtils.SimpleJSONError("New location cannot be blank.");
      }
    } catch (IOException e) {
      log.debug("Failed to save new box location", e);
      return JSONUtils.SimpleJSONError("Error saving new location. Please try again.");
    }

    return JSONUtils.SimpleJSONResponse("Box successfully moved.");
  }

  /**
   * Generates the printable 2D identificationBarcode for the Box.
   * 
   * @param HttpSession
   *          session, JSONObject json
   * @return JSON response containing the name of the newly-generated 2D identificationBarcode image on disk
   */
  public JSONObject getBoxBarcode(HttpSession session, JSONObject json) {
    Long boxId = json.getLong("boxId");
    File temploc = getBarcodeFileLocation(session);
    try {
      Box box = requestManager.getBoxById(boxId);
      BarcodeFactory barcodeFactory = new BarcodeFactory();
      barcodeFactory.setPointPixels(1.5f);
      barcodeFactory.setBitmapResolution(600);
      RenderedImage bi = null;

      if (json.has("barcodeGenerator")) {
        BarcodeDimension dim = new BarcodeDimension(100, 100);
        if (json.has("dimensionWidth") && json.has("dimensionHeight")) {
          dim = new BarcodeDimension(json.getDouble("dimensionWidth"), json.getDouble("dimensionHeight"));
        }
        BarcodeGenerator bg = BarcodeFactory.lookupGenerator(json.getString("barcodeGenerator"));
        if (bg != null) {
          bi = barcodeFactory.generateBarcode(box, bg, dim);
        } else {
          return JSONUtils.SimpleJSONError("'" + json.getString("barcodeGenerator") + "' is not a valid barcode generator type");
        }
      } else {
        bi = barcodeFactory.generateSquareDataMatrix(box, 400);
      }

      if (bi != null) {
        File tempimage = misoFileManager.generateTemporaryFile("barcode-", ".png", temploc);
        if (ImageIO.write(bi, "png", tempimage)) {
          return JSONUtils.JSONObjectResponse("img", tempimage.getName());
        }
        return JSONUtils.SimpleJSONError("Writing temp image file failed.");
      } else {
        return JSONUtils.SimpleJSONError("Sample has no parseable barcode");
      }
    } catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage() + ": Cannot seem to access " + temploc.getAbsolutePath());
    }
  }

  /**
   * Send 2D identificationBarcode to printer.
   * 
   * @param HttpSession
   *          session, JSONObject json
   * @return JSON message indicating success or error
   */
  public JSONObject printBoxBarcodes(HttpSession session, JSONObject json) {
    return ControllerHelperServiceUtils.printBarcodes(printerService, json, new BarcodePrintAssister<Box>() {

      @Override
      public Box fetch(long id) throws IOException {
        return requestManager.getBoxById(id);
      }

      @Override
      public void store(Box item) throws IOException {
        requestManager.saveBox(item);
      }

      @Override
      public String getGroupName() {
        return "boxes";
      }

      @Override
      public String getIdName() {
        return "boxId";
      }

      @Override
      public Iterable<Box> fetchAll(long projectId) throws IOException {
        throw new UnsupportedOperationException();
      }
    });
  }

  /**
   * Change the box identificationBarcode to a user-entered value. Only valid if autogenerateIdentificationBarcode = true in miso.properties
   * 
   * @param HttpSession
   *          session, JSONObject json
   * @return JSON message indicating success or error
   */
  public JSONObject changeBoxIdBarcode(HttpSession session, JSONObject json) {
    Long boxId = json.getLong("boxId");
    String idBarcode = json.getString("identificationBarcode");

    try {
      if (!"".equals(idBarcode)) {
        Box box = requestManager.getBoxById(boxId);
        box.setIdentificationBarcode(idBarcode);
        box.setLastModifier(authorizationManager.getCurrentUser());
        requestManager.saveBox(box);
      } else {
        return JSONUtils.SimpleJSONError("New identification barcode cannot be blank.");
      }
    } catch (IOException e) {
      log.debug("Error getting or saving box", e);
      return JSONUtils.SimpleJSONError("Error saving new box barcode. Please try again.");
    }

    return JSONUtils.SimpleJSONResponse("New identification barcode successfully assigned.");
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
        log.debug("Integration error preparing scanner", e);
        return JSONUtils.SimpleJSONError("Error: could not find the scanner");
      } catch (Exception e) {
        log.debug("General error preparing scanner", e);
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
      Map<String, BoxableView> boxablesByBarcode = requestManager.getBoxableViewsFromBarcodeList(validBarcodes).stream()
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
        box = requestManager.getBoxById(json.getLong("boxId"));
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
      log.info(e.getMessage());
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

        Box box = requestManager.getBoxById(boxId);
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
  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @CoverageIgnore
  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  @CoverageIgnore
  public void setPrinterService(PrinterService printerService) {
    this.printerService = printerService;
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
