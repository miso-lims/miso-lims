package uk.ac.bbsrc.tgac.miso.dto;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;

public class BoxableDto {
  private String alias;

  private String boxAlias;

  private String boxPosition;

  private String coordinates;

  private Boolean discarded;

  private EntityType entityType;

  private Long sampleClassId;

  /**
   * This is the object's ID (e.g., sampleId, libraryId) used to located it when used in conjunction with the name to determine the type or
   * the barcode.
   */
  private Long id;

  private String identificationBarcode;

  private String name;

  private Double volume;

  public String getAlias() {
    return alias;
  }

  public String getBoxAlias() {
    return boxAlias;
  }

  public String getBoxPosition() {
    return boxPosition;
  }

  public String getCoordinates() {
    return coordinates;
  }

  public Boolean getDiscarded() {
    return discarded;
  }

  public Long getId() {
    return id;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public String getName() {
    return name;
  }

  public Double getVolume() {
    return volume;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setBoxAlias(String boxAlias) {
    this.boxAlias = boxAlias;
  }

  public void setBoxPosition(String boxPosition) {
    this.boxPosition = boxPosition;
  }

  public void setCoordinates(String coordinates) {
    this.coordinates = coordinates;
  }

  public void setDiscarded(Boolean discarded) {
    this.discarded = discarded;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setVolume(Double volume) {
    this.volume = volume;
  }

  public EntityType getEntityType() {
    return entityType;
  }

  public void setEntityType(EntityType entityType) {
    this.entityType = entityType;
  }

  public Long getSampleClassId() {
    return sampleClassId;
  }

  public void setSampleClassId(Long sampleClassId) {
    this.sampleClassId = sampleClassId;
  }

}
