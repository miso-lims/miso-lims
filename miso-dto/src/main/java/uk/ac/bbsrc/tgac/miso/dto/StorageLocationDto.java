package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonProperty;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLabel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class StorageLocationDto {

  public static StorageLocationDto from(@Nonnull StorageLocation from, boolean includeChildLocations,
      boolean recursive) {
    StorageLocationDto dto = new StorageLocationDto();
    dto.setId(from.getId());
    if (from.getParentLocation() != null) {
      dto.setParentLocationId(from.getParentLocation().getId());
    }
    Dtos.setId(dto::setFreezerId, from.getFreezerLocation());
    dto.setLocationUnit(from.getLocationUnit().name());
    switch (from.getLocationUnit().getBoxStorageAmount()) {
      case NONE:
        dto.setAvailableStorage(false);
        break;
      case SINGLE:
        dto.setAvailableStorage(from.getBoxes().isEmpty());
        break;
      case MULTIPLE:
        dto.setAvailableStorage(true);
        break;
      default:
        throw new IllegalStateException("Unexpected BoxStorageAmount");
    }
    dto.setAlias(from.getAlias());
    dto.setIdentificationBarcode(from.getIdentificationBarcode());
    dto.setDisplayLocation(from.getDisplayLocation());
    dto.setFullDisplayLocation(from.getFullDisplayLocation());
    dto.setProbeId(from.getProbeId());
    dto.setRetired(from.getRetired());
    dto.setOutOfService(from.isOutOfService());
    Dtos.setId(dto::setMapId, from.getMap());
    setString(dto::setMapFilename, maybeGetProperty(from.getMap(), StorageLocationMap::getFilename));
    setString(dto::setMapAnchor, from.getMapAnchor());
    Dtos.setId(dto::setLabelId, from.getLabel());
    if (includeChildLocations) {
      dto.setChildLocations(from.getChildLocations().stream()
          .map(child -> StorageLocationDto.from(child, recursive, recursive))
          .collect(Collectors.toList()));
    }
    dto.setBoxes(from.getBoxes().stream().map(box -> asDto(box, true)).collect(Collectors.toSet()));
    return dto;
  }

  private long id;
  private Long parentLocationId;
  private Long freezerId;
  private String locationUnit;
  private boolean availableStorage;
  private String alias;
  private String identificationBarcode;
  private String displayLocation;
  private String fullDisplayLocation;
  private List<StorageLocationDto> childLocations;
  private Set<BoxDto> boxes;
  private String probeId;
  private boolean retired;
  private Long mapId;
  private String mapFilename;
  private String mapAnchor;
  private Long labelId;
  private boolean outOfService;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Long getParentLocationId() {
    return parentLocationId;
  }

  public void setParentLocationId(Long parentLocationId) {
    this.parentLocationId = parentLocationId;
  }

  public Long getFreezerId() {
    return freezerId;
  }

  public void setFreezerId(Long freezerId) {
    this.freezerId = freezerId;
  }

  public String getLocationUnit() {
    return locationUnit;
  }

  public void setLocationUnit(String locationUnit) {
    this.locationUnit = locationUnit;
  }

  @JsonProperty(value = "availableStorage")
  public boolean hasAvailableStorage() {
    return availableStorage;
  }

  public void setAvailableStorage(boolean hasAvailableStorage) {
    this.availableStorage = hasAvailableStorage;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getDisplayLocation() {
    return displayLocation;
  }

  public void setDisplayLocation(String displayLocation) {
    this.displayLocation = displayLocation;
  }

  public String getFullDisplayLocation() {
    return fullDisplayLocation;
  }

  public void setFullDisplayLocation(String fullDisplayLocation) {
    this.fullDisplayLocation = fullDisplayLocation;
  }

  public List<StorageLocationDto> getChildLocations() {
    return childLocations;
  }

  public void setChildLocations(List<StorageLocationDto> childLocations) {
    this.childLocations = childLocations;
  }

  public Set<BoxDto> getBoxes() {
    return boxes;
  }

  public void setBoxes(Set<BoxDto> set) {
    this.boxes = set;
  }

  public String getProbeId() {
    return probeId;
  }

  public void setProbeId(String probeId) {
    this.probeId = probeId;
  }

  public boolean getRetired() {
    return retired;
  }

  public void setRetired(boolean retired) {
    this.retired = retired;
  }

  public Long getMapId() {
    return mapId;
  }

  public void setMapId(Long mapId) {
    this.mapId = mapId;
  }

  public String getMapFilename() {
    return mapFilename;
  }

  public void setMapFilename(String mapFilename) {
    this.mapFilename = mapFilename;
  }

  public String getMapAnchor() {
    return mapAnchor;
  }

  public void setMapAnchor(String mapAnchor) {
    this.mapAnchor = mapAnchor;
  }

  public Long getLabelId() {
    return labelId;
  }

  public void setLabelId(Long labelId) {
    this.labelId = labelId;
  }

  public boolean isOutOfService() {
    return outOfService;
  }

  public void setOutOfService(boolean outOfService) {
    this.outOfService = outOfService;
  }

  public StorageLocation to() {
    StorageLocation location = new StorageLocation();
    location.setId(getId());
    location.setAlias(getAlias());
    if (!LimsUtils.isStringEmptyOrNull(getIdentificationBarcode())) {
      location.setIdentificationBarcode(getIdentificationBarcode());
    }
    if (getParentLocationId() != null) {
      location.setParentLocation(new StorageLocation());
      location.getParentLocation().setId(getParentLocationId());
    }
    location.setLocationUnit(LocationUnit.valueOf(getLocationUnit()));
    location.setProbeId(getProbeId());
    location.setRetired(getRetired());
    setObject(location::setMap, StorageLocationMap::new, getMapId());
    setString(location::setMapAnchor, getMapAnchor());
    setObject(location::setLabel, StorageLabel::new, getLabelId());
    return location;
  }

}
