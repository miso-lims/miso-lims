package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.ChangeLogDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.StorageLocationDto;
import uk.ac.bbsrc.tgac.miso.service.StorageLocationService;

@Controller
@RequestMapping("/rest/storagelocations")
public class StorageLocationRestController extends RestController {

  @Autowired
  private StorageLocationService storageLocationService;

  @GetMapping(value = "/freezers")
  public @ResponseBody List<StorageLocationDto> getFreezers() {
    return storageLocationService.listFreezers().stream()
        .map(freezer -> Dtos.asDto(freezer, false, false))
        .collect(Collectors.toList());
  }

  @GetMapping(value = "/bybarcode")
  public @ResponseBody StorageLocationDto getLocation(@RequestParam(name = "q", required = true) String barcode) {
    StorageLocation location = storageLocationService.getByBarcode(barcode);
    if (location == null) {
      throw new RestException("storage location not found", Status.NOT_FOUND);
    }
    return Dtos.asDto(location, true, false);
  }

  @GetMapping(value = "/{id}/children")
  public @ResponseBody List<StorageLocationDto> getChildLocations(@PathVariable(name = "id", required = true) long id) {
    StorageLocation location = storageLocationService.get(id);
    if (location == null) {
      throw new RestException("storage location not found", Status.NOT_FOUND);
    }
    return location.getChildLocations().stream()
        .map(child -> Dtos.asDto(child, false, false))
        .collect(Collectors.toList());
  }

  @PostMapping(value = "/freezers")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody StorageLocationDto createFreezer(@RequestBody StorageLocationDto dto) throws IOException {
    return doSave(dto);
  }

  @PutMapping(value = "/freezers/{id}")
  public @ResponseBody StorageLocationDto update(@PathVariable(name = "id", required = true) long id, @RequestBody StorageLocationDto dto)
      throws IOException {
    if (dto.getId() != id) {
      throw new RestException("Location ID mismatch", Status.BAD_REQUEST);
    }
    getFreezer(id); // checks existing freezer
    return doSave(dto);
  }

  private StorageLocationDto doSave(StorageLocationDto dto) throws IOException {
    StorageLocation freezer = Dtos.to(dto);
    long savedId = storageLocationService.saveFreezer(freezer);
    StorageLocation saved = storageLocationService.get(savedId);
    return Dtos.asDto(saved, true, true);
  }

  @PostMapping(value = "/freezers/{id}/shelves")
  public @ResponseBody StorageLocationDto addFreezerShelf(@PathVariable(name = "id", required = true) long id) throws IOException {
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

  @PostMapping(value = "/freezers/{id}/stacks")
  public @ResponseBody StorageLocationDto addFreezerStack(@PathVariable(name = "id", required = true) long id,
      @RequestParam(name = "height", required = true) int height) throws IOException {
    StorageLocation freezer = getFreezer(id);
    if (height < 1) {
      throw new RestException("Invalid stack height", Status.BAD_REQUEST);
    }

    return createStack(freezer, height);
  }

  @PostMapping(value = "/rooms")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public StorageLocationDto addRoom(@RequestParam(name = "alias", required = true) String alias,
      @RequestParam(name = "identificationBarcode", required = false) String identificationBarcode) throws IOException {
    StorageLocation room = makeStorage(alias, LocationUnit.ROOM, null);
    room.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(identificationBarcode));
    long savedId = storageLocationService.createRoom(room);
    StorageLocation saved = storageLocationService.get(savedId);
    return Dtos.asDto(saved, false, false);
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

  private StorageLocationDto createStack(StorageLocation parent, int height) throws IOException {
    String stackNumber = findNextNumber(parent, LocationUnit.STACK);
    StorageLocation stack = makeStorage(stackNumber, LocationUnit.STACK, parent);
    for (int i = 0; i < height; i++) {
      makeStorage(Integer.toString(i + 1), LocationUnit.STACK_POSITION, stack);
    }
    return doSave(stack);
  }

  private StorageLocationDto doSave(StorageLocation storage) throws IOException {
    long savedId = storageLocationService.addFreezerStorage(storage);
    StorageLocation saved = storageLocationService.get(savedId);
    return Dtos.asDto(saved, false, false);
  }

  @PostMapping(value = "/freezers/{freezerId}/shelves/{shelfId}/stacks")
  public @ResponseBody StorageLocationDto addShelfStack(@PathVariable(name = "freezerId", required = true) long freezerId,
      @PathVariable(name = "shelfId", required = true) long shelfId, @RequestParam(name = "height", required = true) int height)
      throws IOException {
    StorageLocation freezer = getFreezer(freezerId);
    StorageLocation shelf = getShelf(freezer, shelfId);
    if (height < 1) {
      throw new RestException("Invalid stack height", Status.BAD_REQUEST);
    }
    return createStack(shelf, height);
  }

  @PostMapping(value = "/freezers/{freezerId}/shelves/{shelfId}/racks")
  public @ResponseBody StorageLocationDto addShelfRack(@PathVariable(name = "freezerId", required = true) long freezerId,
      @PathVariable(name = "shelfId", required = true) long shelfId, @RequestParam(name = "depth", required = true) int depth,
      @RequestParam(name = "height", required = true) int height) throws IOException {
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

  @PostMapping(value = "/freezers/{freezerId}/shelves/{shelfId}/loose")
  public @ResponseBody StorageLocationDto addShelfLooseStorage(@PathVariable(name = "freezerId", required = true) long freezerId,
      @PathVariable(name = "shelfId", required = true) long shelfId) throws IOException {
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

  @GetMapping(value = "/freezers/{freezerId}/changelog")
  public @ResponseBody List<ChangeLogDto> getFreezerChangelog(@PathVariable(name = "freezerId", required = true) long freezerId)
      throws IOException {
    StorageLocation freezer = storageLocationService.get(freezerId);
    if (freezer == null) {
      throw new RestException("Freezer not found", Status.NOT_FOUND);
    }
    return freezer.getChangeLog().stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
  }

}
