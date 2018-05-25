package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.StorageLocationDto;
import uk.ac.bbsrc.tgac.miso.service.StorageLocationService;

@Controller
@RequestMapping("/rest/storagelocations")
public class StorageLocationRestController extends RestController {

  @Autowired
  private StorageLocationService storageLocationService;

  @RequestMapping(value = "/freezers", method = RequestMethod.GET)
  public @ResponseBody List<StorageLocationDto> getFreezers() {
    return storageLocationService.listFreezers().stream()
        .map(freezer -> Dtos.asDto(freezer, false))
        .collect(Collectors.toList());
  }

  @RequestMapping(value = "/bybarcode", method = RequestMethod.GET)
  public @ResponseBody StorageLocationDto getLocation(@RequestParam(name = "q", required = true) String barcode) {
    StorageLocation location = storageLocationService.getByBarcode(barcode);
    if (location == null) {
      throw new RestException("storage location not found", Status.NOT_FOUND);
    }
    return Dtos.asDto(location, true);
  }

  @RequestMapping(value = "/{id}/children", method = RequestMethod.GET)
  public @ResponseBody List<StorageLocationDto> getChildLocations(@PathVariable(name = "id", required = true) long id) {
    StorageLocation location = storageLocationService.get(id);
    if (location == null) {
      throw new RestException("storage location not found", Status.NOT_FOUND);
    }
    return location.getChildLocations().stream()
        .map(child -> Dtos.asDto(child, false))
        .collect(Collectors.toList());
  }

}
