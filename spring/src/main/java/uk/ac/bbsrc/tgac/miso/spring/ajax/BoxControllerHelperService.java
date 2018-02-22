package uk.ac.bbsrc.tgac.miso.spring.ajax;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Ajaxified
public class BoxControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(BoxControllerHelperService.class);

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private BoxService boxService;

  @Autowired
  private MisoFilesManager misoFileManager;

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
}
