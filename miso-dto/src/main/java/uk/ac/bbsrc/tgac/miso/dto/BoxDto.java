package uk.ac.bbsrc.tgac.miso.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class BoxDto {
  private String alias;
  private String name;
  private Integer cols;
  private String description;
  private Long id;
  private List<BoxableDto> items = new ArrayList<>();
  private Integer rows;
  private Boolean scannable;
  private Long sizeId;
  private String sizeLabel;
  private Long useId;
  private String useAlias;
  private String identificationBarcode;
  private String locationBarcode;
  private int tubeCount;
  private Long storageLocationId;
  private String storageLocationBarcode;
  private String freezerDisplayLocation;
  private Long freezerId;
  private String storageDisplayLocation;

  public int getTubeCount() {
    return tubeCount;
  }

  public void setTubeCount(int tubeCount) {
    this.tubeCount = tubeCount;
  }

  public Long getStorageLocationId() {
    return storageLocationId;
  }

  public void setStorageLocationId(Long storageLocationId) {
    this.storageLocationId = storageLocationId;
  }

  public String getAlias() {
    return alias;
  }

  public Integer getCols() {
    return cols;
  }

  public String getDescription() {
    return description;
  }

  public Long getId() {
    return id;
  }

  public List<BoxableDto> getItems() {
    return items;
  }

  public Integer getRows() {
    return rows;
  }

  public Boolean getScannable() {
    return scannable;
  }

  public Long getSizeId() {
    return sizeId;
  }

  public Long getUseId() {
    return useId;
  }

  public String getUseAlias() {
    return useAlias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setCols(Integer cols) {
    this.cols = cols;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setItems(List<BoxableDto> items) {
    this.items = items;
  }

  public void setRows(Integer rows) {
    this.rows = rows;
  }

  public void setScannable(Boolean scannable) {
    this.scannable = scannable;
  }

  public void setSizeId(Long sizeId) {
    this.sizeId = sizeId;
  }

  public String getSizeLabel() {
    return sizeLabel;
  }

  public void setSizeLabel(String sizeLabel) {
    this.sizeLabel = sizeLabel;
  }

  public void setUseId(Long useId) {
    this.useId = useId;
  }

  public void setUseAlias(String useName) {
    this.useAlias = useName;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  public String getFreezerDisplayLocation() {
    return freezerDisplayLocation;
  }

  public void setFreezerDisplayLocation(String freezerDisplayLocation) {
    this.freezerDisplayLocation = freezerDisplayLocation;
  }

  public Long getFreezerId() {
    return freezerId;
  }

  public void setFreezerId(Long freezerId) {
    this.freezerId = freezerId;
  }

  public String getStorageDisplayLocation() {
    return storageDisplayLocation;
  }

  public void setStorageDisplayLocation(String storageDisplayLocation) {
    this.storageDisplayLocation = storageDisplayLocation;
  }

  public String getStorageLocationBarcode() {
    return storageLocationBarcode;
  }

  public void setStorageLocationBarcode(String storageLocationBarcode) {
    this.storageLocationBarcode = storageLocationBarcode;
  }

}
