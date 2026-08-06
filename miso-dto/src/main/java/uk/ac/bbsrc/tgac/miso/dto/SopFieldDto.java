package uk.ac.bbsrc.tgac.miso.dto;

public class SopFieldDto {

  private Long id;
  private String name;
  private String units;
  private String fieldType;
  private Long instrumentModelId;
  private String instrumentModelAlias;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public String getFieldType() {
    return fieldType;
  }

  public void setFieldType(String fieldType) {
    this.fieldType = fieldType;
  }

  public Long getInstrumentModelId() {
    return instrumentModelId;
  }

  public void setInstrumentModelId(Long instrumentModelId) {
    this.instrumentModelId = instrumentModelId;
  }

  public String getInstrumentModelAlias() {
    return instrumentModelAlias;
  }

  public void setInstrumentModelAlias(String instrumentModelAlias) {
    this.instrumentModelAlias = instrumentModelAlias;
  }
}
