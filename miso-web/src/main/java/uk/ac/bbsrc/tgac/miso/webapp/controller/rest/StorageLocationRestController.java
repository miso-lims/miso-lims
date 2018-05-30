package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
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
        .map(freezer -> Dtos.asDto(freezer, false, false))
        .collect(Collectors.toList());
  }

  @RequestMapping(value = "/bybarcode", method = RequestMethod.GET)
  public @ResponseBody StorageLocationDto getLocation(@RequestParam(name = "q", required = true) String barcode) {
    StorageLocation location = storageLocationService.getByBarcode(barcode);
    if (location == null) {
      throw new RestException("storage location not found", Status.NOT_FOUND);
    }
    return Dtos.asDto(location, true, false);
  }

  @RequestMapping(value = "/{id}/children", method = RequestMethod.GET)
  public @ResponseBody List<StorageLocationDto> getChildLocations(@PathVariable(name = "id", required = true) long id) {
    StorageLocation location = storageLocationService.get(id);
    if (location == null) {
      throw new RestException("storage location not found", Status.NOT_FOUND);
    }
    return location.getChildLocations().stream()
        .map(child -> Dtos.asDto(child, false, false))
        .collect(Collectors.toList());
  }

  @RequestMapping(value = "/freezers", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody StorageLocationDto createFreezer(@RequestBody StorageLocationDto dto) {
    return doSave(dto);
  }

  @RequestMapping(value = "/freezers/{id}", method = RequestMethod.PUT)
  public @ResponseBody StorageLocationDto update(@PathVariable(name = "id", required = true) long id, @RequestBody StorageLocationDto dto)
      throws IOException {
    if (dto.getId() != id) {
      throw new RestException("Location ID mismatch", Status.BAD_REQUEST);
    }
    getFreezer(id); // checks existing freezer
    return doSave(dto);
  }

  private StorageLocationDto doSave(StorageLocationDto dto) {
    StorageLocation freezer = Dtos.to(dto);
    long savedId = storageLocationService.saveFreezer(freezer);
    StorageLocation saved = storageLocationService.get(savedId);
    return Dtos.asDto(saved, true, true);
  }

  @RequestMapping(value = "/freezers/{id}/shelves", method = RequestMethod.POST)
  public @ResponseBody StorageLocationDto addFreezerShelf(@PathVariable(name = "id", required = true) long id) {
    StorageLocation freezer = getFreezer(id);
    int lastShelf = freezer.getChildLocations().stream().filter(loc -> {
      return loc.getLocationUnit() == LocationUnit.SHELF;
    }).map(StorageLocation::getAlias).mapToInt(Integer::parseInt).max().orElse(0);

    StorageLocation shelf = new StorageLocation();
    shelf.setAlias(Integer.toString(lastShelf + 1));
    shelf.setLocationUnit(LocationUnit.SHELF);
    shelf.setParentLocation(freezer);

    long savedId = storageLocationService.addFreezerStorage(shelf);
    StorageLocation saved = storageLocationService.get(savedId);
    return Dtos.asDto(saved, false, false);
  }

  private StorageLocation getFreezer(long id) {
    StorageLocation freezer = storageLocationService.get(id);
    if (freezer == null || freezer.getLocationUnit() != LocationUnit.FREEZER) {
      throw new RestException("Freezer not found", Status.NOT_FOUND);
    }
    return freezer;
  }

  @RequestMapping(value = "/freezers/{id}/stacks", method = RequestMethod.POST)
  public @ResponseBody StorageLocationDto addFreezerStack(@PathVariable(name = "id", required = true) long id,
      @RequestParam(name = "height", required = true) int height) {
    StorageLocation freezer = getFreezer(id);
    if (height < 1) {
      throw new RestException("Invalid stack height", Status.BAD_REQUEST);
    }

    return createStack(freezer, height);
  }

  private StorageLocation makeStorage(String alias, LocationUnit locationUnit, StorageLocation parent) {
    StorageLocation storage = new StorageLocation();
    storage.setAlias(alias);
    storage.setLocationUnit(locationUnit);
    storage.setParentLocation(parent);
    return storage;
  }

  private String findNextNumber(StorageLocation parent, LocationUnit childType) {
    return Integer.toString(parent.getChildLocations().stream().filter(loc -> {
      return loc.getLocationUnit() == childType;
    }).map(StorageLocation::getAlias).mapToInt(Integer::parseInt).max().orElse(0) + 1);
  }

  private StorageLocationDto createStack(StorageLocation parent, int height) {
    String stackNumber = findNextNumber(parent, LocationUnit.STACK);
    StorageLocation stack = makeStorage(stackNumber, LocationUnit.STACK, parent);
    for (int i = 0; i < height; i++) {
      makeStorage(Integer.toString(i + 1), LocationUnit.STACK_POSITION, stack);
    }
    return doSave(stack);
  }

  private StorageLocationDto doSave(StorageLocation storage) {
    long savedId = storageLocationService.addFreezerStorage(storage);
    StorageLocation saved = storageLocationService.get(savedId);
    return Dtos.asDto(saved, false, false);
  }

  @RequestMapping(value = "/freezers/{freezerId}/shelves/{shelfId}/stacks", method = RequestMethod.POST)
  public @ResponseBody StorageLocationDto addShelfStack(@PathVariable(name = "freezerId", required = true) long freezerId,
      @PathVariable(name = "shelfId", required = true) long shelfId, @RequestParam(name = "height", required = true) int height) {
    StorageLocation freezer = getFreezer(freezerId);
    StorageLocation shelf = getShelf(freezer, shelfId);
    if (height < 1) {
      throw new RestException("Invalid stack height", Status.BAD_REQUEST);
    }
    return createStack(shelf, height);
  }

  @RequestMapping(value = "/freezers/{freezerId}/shelves/{shelfId}/racks", method = RequestMethod.POST)
  public @ResponseBody StorageLocationDto addShelfRack(@PathVariable(name = "freezerId", required = true) long freezerId,
      @PathVariable(name = "shelfId", required = true) long shelfId, @RequestParam(name = "depth", required = true) int depth,
      @RequestParam(name = "height", required = true) int height) {
    StorageLocation freezer = getFreezer(freezerId);
    StorageLocation shelf = getShelf(freezer, shelfId);
    if (height < 1) {
      throw new RestException("Invalid rack height", Status.BAD_REQUEST);
    }
    if (depth < 2) {
      throw new RestException("Invalid rack depth", Status.BAD_REQUEST);
    }
    StorageLocation rack = makeStorage(findNextNumber(shelf, LocationUnit.RACK), LocationUnit.RACK, shelf);
    for (int i = 0; i < depth; i++) {
      StorageLocation stack = makeStorage(Integer.toString(i + 1), LocationUnit.STACK, rack);
      for (int j = 0; j < height; j++) {
        makeStorage(Integer.toString(j + 1), LocationUnit.STACK_POSITION, stack);
      }
    }
    return doSave(rack);
  }

  @RequestMapping(value = "/freezers/{freezerId}/shelves/{shelfId}/loose", method = RequestMethod.POST)
  public @ResponseBody StorageLocationDto addShelfLooseStorage(@PathVariable(name = "freezerId", required = true) long freezerId,
      @PathVariable(name = "shelfId", required = true) long shelfId) {
    StorageLocation freezer = getFreezer(freezerId);
    StorageLocation shelf = getShelf(freezer, shelfId);
    StorageLocation storage = makeStorage(findNextNumber(shelf, LocationUnit.LOOSE_STORAGE), LocationUnit.LOOSE_STORAGE, shelf);
    return doSave(storage);
  }

  private StorageLocation getShelf(StorageLocation freezer, long shelfId) {
    StorageLocation shelf = freezer.getChildLocations().stream()
        .filter(loc -> loc.getId() == shelfId && loc.getLocationUnit() == LocationUnit.SHELF)
        .findFirst().orElse(null);
    if (shelf == null) {
      throw new RestException("Shelf not found", Status.NOT_FOUND);
    }
    return shelf;
  }

}
