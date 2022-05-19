package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLabelService;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationMapService;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.StorageLabelDto;
import uk.ac.bbsrc.tgac.miso.dto.StorageLocationDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/freezer")
public class EditFreezerController {

  private static final String JSP = "/WEB-INF/pages/editFreezer.jsp";

  private static final String MODEL_ATTR_JSON = "freezerJson";

  @Autowired
  private StorageLocationService storageLocationService;
  @Autowired
  private StorageLocationMapService mapService;
  @Autowired
  private StorageLabelService storageLabelService;
  @Autowired
  private ObjectMapper mapper;

  @ModelAttribute("rooms")
  public String getRoomDtos() throws JsonProcessingException {
    List<StorageLocation> rooms = storageLocationService.listRooms();
    return mapper.writeValueAsString(rooms.stream().map(r -> StorageLocationDto.from(r, false, false)).collect(Collectors.toList()));
  }

  @ModelAttribute("locationMaps")
  public String getMapDtos() throws IOException {
    List<StorageLocationMap> maps = mapService.list();
    return mapper.writeValueAsString(maps.stream().map(Dtos::asDto).collect(Collectors.toList()));
  }

  @ModelAttribute("storageLabels")
  public String getStorageLabelDtos() throws IOException {
    return mapper.writeValueAsString(storageLabelService.list()
        .stream()
        .map(StorageLabelDto::from)
        .collect(Collectors.toList()));
  }

  @GetMapping("/new")
  public ModelAndView newStorageLocation(ModelMap model) {
    model.addAttribute(PageMode.PROPERTY, PageMode.CREATE.getLabel());
    model.put("title", "New Freezer");
    return new ModelAndView(JSP, model);
  }

  @GetMapping("/{locationId}")
  public ModelAndView setupForm(@PathVariable(name = "locationId", required = true) long locationId, ModelMap model) throws IOException {
    StorageLocation freezer = storageLocationService.get(locationId);
    return setupFreezerForm(freezer, model);
  }

  @GetMapping("/barcode/{storageBarcode}")
  public ModelAndView setupForm(@PathVariable(name = "storageBarcode", required = true) String storageBarcode, ModelMap model)
      throws IOException {
    StorageLocation freezer = storageLocationService.getFreezerForBarcodedStorageLocation(storageBarcode);
    return setupFreezerForm(freezer, model);
  }

  private ModelAndView setupFreezerForm(StorageLocation freezer, ModelMap model) throws IOException {
    if (freezer == null || freezer.getLocationUnit() != LocationUnit.FREEZER) {
      throw new RestException("Freezer not found");
    }
    model.addAttribute(PageMode.PROPERTY, PageMode.EDIT.getLabel());
    model.put("title", "Freezer " + freezer.getId());
    model.put("freezer", freezer);
    model.addAttribute(MODEL_ATTR_JSON, mapper.writer().writeValueAsString(StorageLocationDto.from(freezer, true, true)));
    model.put("boxes", boxesInStorage(freezer).map(box -> Dtos.asDto(box, false)).collect(Collectors.toSet()));
    return new ModelAndView(JSP, model);
  }

  private static Stream<Box> boxesInStorage(StorageLocation storage) {
    return Stream.concat(storage.getBoxes().stream(), storage.getChildLocations().stream().flatMap(EditFreezerController::boxesInStorage));
  }

}
