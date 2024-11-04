package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;
import uk.ac.bbsrc.tgac.miso.core.service.ServiceRecordService;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ServiceRecordDto;
import uk.ac.bbsrc.tgac.miso.dto.StorageLocationDto;

@Controller
@RequestMapping("/rest/storagelocations")
public class StorageLocationRestController extends RestController {

  @Autowired
  private StorageLocationService storageLocationService;

  @Autowired
  private ServiceRecordService serviceRecordService;

  private final SaveService<StorageLocation> freezerSaveService = new SaveService<>() {

    @Override
    public StorageLocation get(long id) throws IOException {
      return storageLocationService.get(id);
    }

    @Override
    public long create(StorageLocation object) throws IOException {
      return storageLocationService.saveFreezer(object);
    }

    @Override
    public long update(StorageLocation object) throws IOException {
      return storageLocationService.saveFreezer(object);
    }

  };

  @GetMapping(value = "/freezers")
  public @ResponseBody List<StorageLocationDto> getFreezers() {
    return storageLocationService.listFreezers().stream()
        .map(freezer -> StorageLocationDto.from(freezer, false, false))
        .collect(Collectors.toList());
  }

  @GetMapping(value = "/bybarcode")
  public @ResponseBody StorageLocationDto getLocation(@RequestParam(name = "q", required = true) String barcode) {
    StorageLocation location = storageLocationService.getByBarcode(barcode);
    if (location == null) {
      throw new RestException("storage location not found", Status.NOT_FOUND);
    }
    return StorageLocationDto.from(location, true, false);
  }

  @GetMapping(value = "/{id}/children")
  public @ResponseBody List<StorageLocationDto> getChildLocations(@PathVariable(name = "id", required = true) long id)
      throws IOException {
    StorageLocation location = storageLocationService.get(id);
    if (location == null) {
      throw new RestException("storage location not found", Status.NOT_FOUND);
    }
    return location.getChildLocations().stream()
        .map(child -> StorageLocationDto.from(child, false, false))
        .collect(Collectors.toList());
  }

  @PostMapping(value = "/freezers")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody StorageLocationDto createFreezer(@RequestBody StorageLocationDto dto) throws IOException {
    dto.setLocationUnit(LocationUnit.FREEZER.name());
    return RestUtils.createObject("Freezer", dto, StorageLocationDto::to, freezerSaveService,
        freezer -> StorageLocationDto.from(freezer, true, true));
  }

  @PutMapping(value = "/freezers/{id}")
  public @ResponseBody StorageLocationDto update(@PathVariable(name = "id", required = true) long id,
      @RequestBody StorageLocationDto dto)
      throws IOException {
    return RestUtils.updateObject("Freezer", id, dto, StorageLocationDto::to, freezerSaveService,
        freezer -> StorageLocationDto.from(freezer, true, true));
  }

  @PostMapping(value = "/freezers/{id}/shelves")
  public @ResponseBody StorageLocationDto addFreezerShelf(@PathVariable(name = "id", required = true) long id,
      @RequestParam(name = "identificationBarcode", required = false) String barcode) throws IOException {
    StorageLocation freezer = getFreezer(id);
    int lastShelf = freezer.getChildLocations().stream()
        .filter(loc -> loc.getLocationUnit() == LocationUnit.SHELF)
        .map(StorageLocation::getAlias)
        .mapToInt(Integer::parseInt)
        .max().orElse(0);

    StorageLocation shelf = new StorageLocation();
    shelf.setAlias(Integer.toString(lastShelf + 1));
    shelf.setLocationUnit(LocationUnit.SHELF);
    shelf.setParentLocation(freezer);
    if (!LimsUtils.isStringEmptyOrNull(barcode)) {
      shelf.setIdentificationBarcode(barcode);
    }

    long savedId = storageLocationService.addFreezerStorage(shelf);
    StorageLocation saved = storageLocationService.get(savedId);
    return StorageLocationDto.from(saved, false, false);
  }

  private StorageLocation getFreezer(long id) throws IOException {
    StorageLocation freezer = storageLocationService.get(id);
    if (freezer == null || freezer.getLocationUnit() != LocationUnit.FREEZER) {
      throw new RestException("Freezer not found", Status.NOT_FOUND);
    }
    return freezer;
  }

  @PostMapping(value = "/freezers/{id}/stacks")
  public @ResponseBody StorageLocationDto addFreezerStack(@PathVariable(name = "id", required = true) long id,
      @RequestParam(name = "height", required = true) int height,
      @RequestParam(name = "identificationBarcode", required = false) String barcode,
      @RequestBody List<String> childBarcodes)
      throws IOException {
    StorageLocation freezer = getFreezer(id);
    if (height < 1) {
      throw new RestException("Invalid stack height", Status.BAD_REQUEST);
    }

    return createStack(freezer, height, barcode, childBarcodes);
  }

  @PostMapping(value = "/rooms")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public StorageLocationDto addRoom(@RequestParam(name = "alias", required = true) String alias,
      @RequestParam(name = "identificationBarcode", required = false) String identificationBarcode) throws IOException {
    StorageLocation room = makeStorage(alias, LocationUnit.ROOM, null, null);
    room.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(identificationBarcode));
    long savedId = storageLocationService.createRoom(room);
    StorageLocation saved = storageLocationService.get(savedId);
    return StorageLocationDto.from(saved, false, false);
  }

  private StorageLocation makeStorage(String alias, LocationUnit locationUnit, StorageLocation parent, String barcode) {
    StorageLocation storage = new StorageLocation();
    storage.setAlias(alias);
    storage.setLocationUnit(locationUnit);
    storage.setParentLocation(parent);
    storage.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(barcode));
    return storage;
  }

  private String findNextNumber(StorageLocation parent, LocationUnit childType) {
    return Integer.toString(parent.getChildLocations().stream().filter(loc -> {
      return loc.getLocationUnit() == childType;
    }).map(StorageLocation::getAlias).mapToInt(Integer::parseInt).max().orElse(0) + 1);
  }

  private StorageLocationDto createStack(StorageLocation parent, int height, String barcode, List<String> childBarcodes)
      throws IOException {
    String stackNumber = findNextNumber(parent, LocationUnit.STACK);
    StorageLocation stack = makeStorage(stackNumber, LocationUnit.STACK, parent, barcode);
    for (int i = 1; i <= height; i++) {
      makeStorage(Integer.toString(i), LocationUnit.STACK_POSITION, stack, childBarcodes.get(i - 1));
    }
    return doSave(stack);
  }

  private StorageLocationDto doSave(StorageLocation storage) throws IOException {
    long savedId = storageLocationService.addFreezerStorage(storage);
    StorageLocation saved = storageLocationService.get(savedId);
    return StorageLocationDto.from(saved, false, false);
  }

  @PostMapping(value = "/freezers/{freezerId}/shelves/{shelfId}/stacks")
  public @ResponseBody StorageLocationDto addShelfStack(
      @PathVariable(name = "freezerId", required = true) long freezerId,
      @PathVariable(name = "shelfId", required = true) long shelfId,
      @RequestParam(name = "height", required = true) int height,
      @RequestParam(name = "identificationBarcode", required = false) String barcode,
      @RequestBody List<String> childBarcodes)
      throws IOException {
    StorageLocation freezer = getFreezer(freezerId);
    StorageLocation shelf = getShelf(freezer, shelfId);
    if (height < 1) {
      throw new RestException("Invalid stack height", Status.BAD_REQUEST);
    }
    return createStack(shelf, height, barcode, childBarcodes);
  }

  @PostMapping(value = "/freezers/{freezerId}/shelves/{shelfId}/racks")
  public @ResponseBody StorageLocationDto addShelfRack(
      @PathVariable(name = "freezerId", required = true) long freezerId,
      @PathVariable(name = "shelfId", required = true) long shelfId,
      @RequestParam(name = "depth", required = true) int depth,
      @RequestParam(name = "height", required = true) int height,
      @RequestParam(name = "identificationBarcode", required = false) String barcode,
      @RequestBody List<String> childBarcodes)
      throws IOException {
    StorageLocation freezer = getFreezer(freezerId);
    StorageLocation shelf = getShelf(freezer, shelfId);
    if (height < 1) {
      throw new RestException("Invalid rack height", Status.BAD_REQUEST);
    }
    if (depth < 2) {
      throw new RestException("Invalid rack depth", Status.BAD_REQUEST);
    }
    StorageLocation rack = makeStorage(findNextNumber(shelf, LocationUnit.RACK), LocationUnit.RACK, shelf, barcode);
    for (int i = 1; i <= depth; i++) {
      StorageLocation stack = makeStorage(Integer.toString(i), LocationUnit.STACK, rack, null);
      for (int j = 1; j <= height; j++) {
        int barcodeNum = (i - 1) * height + j - 1;
        makeStorage(Integer.toString(j), LocationUnit.STACK_POSITION, stack, childBarcodes.get(barcodeNum));
      }
    }
    return doSave(rack);
  }

  @PostMapping(value = "/freezers/{freezerId}/shelves/{shelfId}/tray-racks")
  public @ResponseBody StorageLocationDto addShelfTrayRack(
      @PathVariable(name = "freezerId", required = true) long freezerId,
      @PathVariable(name = "shelfId", required = true) long shelfId,
      @RequestParam(name = "height", required = true) int height,
      @RequestParam(name = "identificationBarcode", required = false) String barcode,
      @RequestBody List<String> childBarcodes)
      throws IOException {
    StorageLocation freezer = getFreezer(freezerId);
    StorageLocation shelf = getShelf(freezer, shelfId);
    if (height < 1) {
      throw new RestException("Invalid rack height", Status.BAD_REQUEST);
    }
    StorageLocation trayRack =
        makeStorage(findNextNumber(shelf, LocationUnit.TRAY_RACK), LocationUnit.TRAY_RACK, shelf, barcode);
    for (int i = 1; i <= height; i++) {
      makeStorage(Integer.toString(i), LocationUnit.TRAY, trayRack, childBarcodes.get(i - 1));
    }
    return doSave(trayRack);
  }

  @PostMapping(value = "/freezers/{freezerId}/shelves/{shelfId}/loose")
  public @ResponseBody StorageLocationDto addShelfLooseStorage(
      @PathVariable(name = "freezerId", required = true) long freezerId,
      @PathVariable(name = "shelfId", required = true) long shelfId,
      @RequestParam(name = "identificationBarcode", required = false) String barcode) throws IOException {
    StorageLocation freezer = getFreezer(freezerId);
    StorageLocation shelf = getShelf(freezer, shelfId);
    StorageLocation storage =
        makeStorage(findNextNumber(shelf, LocationUnit.LOOSE_STORAGE), LocationUnit.LOOSE_STORAGE, shelf, barcode);
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

  @PutMapping(value = "/{locationId}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void updateStorageComponent(@PathVariable(name = "locationId", required = true) long locationId,
      @RequestBody StorageLocationDto dto) throws IOException {
    StorageLocation component = dto.to();
    storageLocationService.updateStorageComponent(component);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Storage location", ids, storageLocationService);
  }

  @DeleteMapping("/{locationId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void delete(@PathVariable(name = "locationId", required = true) long locationId)
      throws IOException {
    RestUtils.delete("Storage location", locationId, storageLocationService);
  }

  @PostMapping(value = "/{locationId}/servicerecords")
  public @ResponseBody ServiceRecordDto createRecord(
      @PathVariable(name = "locationId", required = true) Long locationId, @RequestBody ServiceRecordDto dto)
      throws IOException {
    StorageLocation freezer = RestUtils.retrieve("Storage location", locationId, storageLocationService);

    ServiceRecord record = Dtos.to(dto);
    long savedId = storageLocationService.addServiceRecord(record, freezer);
    return Dtos.asDto(serviceRecordService.get(savedId));
  }

}
