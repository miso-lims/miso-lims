package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StorageLocationDto {

  private long id;
  private Long parentLocationId;
  private String locationUnit;
  private boolean availableStorage;
  private String alias;
  private String identificationBarcode;
  private String displayLocation;
  private String fullDisplayLocation;
  private List<StorageLocationDto> childLocations;
  private Set<BoxDto> boxes;
  private String mapUrl;

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

  public String getMapUrl() {
    return mapUrl;
  }

  public void setMapUrl(String url) {
    this.mapUrl = url;
  }

}
