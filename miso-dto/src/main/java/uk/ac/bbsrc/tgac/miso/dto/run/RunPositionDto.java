package uk.ac.bbsrc.tgac.miso.dto.run;

import uk.ac.bbsrc.tgac.miso.dto.ContainerModelDto;

public class RunPositionDto {

  private Long positionId;

  private String positionAlias;

  private Long id;

  private String identificationBarcode;

  private ContainerModelDto containerModel;

  private String lastModified;

  public Long getPositionId() {
    return positionId;
  }

  public void setPositionId(Long positionId) {
    this.positionId = positionId;
  }

  public String getPositionAlias() {
    return positionAlias;
  }

  public void setPositionAlias(String positionAlias) {
    this.positionAlias = positionAlias;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public ContainerModelDto getContainerModel() {
    return containerModel;
  }

  public void setContainerModel(ContainerModelDto containerModel) {
    this.containerModel = containerModel;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

}
