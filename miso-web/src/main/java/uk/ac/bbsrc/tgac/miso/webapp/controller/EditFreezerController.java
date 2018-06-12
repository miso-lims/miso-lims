package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.StorageLocationService;

@Controller
@RequestMapping("/freezer")
public class EditFreezerController {

  private static final String JSP = "/pages/editFreezer.jsp";

  private static final String MODEL_ATTR_PAGEMODE = "pageMode";
  private static final String MODEL_ATTR_JSON = "freezerJson";

  @Autowired
  private StorageLocationService storageLocationService;

  @ModelAttribute("maxLengths")
  public Map<String, Integer> getColumnSizes() throws IOException {
    return storageLocationService.getColumnSizes();
  }

  @ModelAttribute("rooms")
  public List<StorageLocation> getRooms() {
    return storageLocationService.listRooms();
  }

  @RequestMapping("/new")
  public ModelAndView newArray(ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_PAGEMODE, "create");
    model.put("title", "New Storage Location");
    return new ModelAndView(JSP, model);
  }

  @RequestMapping("/{locationId}")
  public ModelAndView setupForm(@PathVariable(name = "locationId", required = true) long locationId, ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_PAGEMODE, "edit");
    StorageLocation freezer = storageLocationService.get(locationId);
    if (freezer == null || freezer.getLocationUnit() != LocationUnit.FREEZER) {
      throw new NotFoundException("Freezer not found");
    }

    ObjectMapper mapper = new ObjectMapper();
    model.put("title", "Freezer " + freezer.getId());
    model.addAttribute(MODEL_ATTR_JSON, mapper.writer().writeValueAsString(Dtos.asDto(freezer, true, true)));
    model.put("boxes", boxesInStorage(freezer).map(box -> Dtos.asDto(box, false)).collect(Collectors.toSet()));
    return new ModelAndView(JSP, model);
  }

  private static Stream<Box> boxesInStorage(StorageLocation storage) {
    return Stream.concat(storage.getBoxes().stream(), storage.getChildLocations().stream().flatMap(EditFreezerController::boxesInStorage));
  }

}
