package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StorageLocationDto {

  private long id;
  private long parentLocationId;
  private String locationUnit;
  private boolean availableStorage;
  private String alias;
  private String identificationBarcode;
  private String displayLocation;
  private String fullDisplayLocation;
  private List<StorageLocationDto> childLocations;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getParentLocationId() {
    return parentLocationId;
  }

  public void setParentLocationId(long parentLocationId) {
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

}
