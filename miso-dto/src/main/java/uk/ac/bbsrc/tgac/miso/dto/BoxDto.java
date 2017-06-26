package uk.ac.bbsrc.tgac.miso.dto;

import java.util.ArrayList;
import java.util.List;

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
  private Long useId;
  private String useAlias;
  private String identificationBarcode;
  private String locationBarcode;

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
}
