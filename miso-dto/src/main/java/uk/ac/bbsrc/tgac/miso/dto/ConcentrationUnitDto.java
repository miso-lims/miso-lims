package uk.ac.bbsrc.tgac.miso.dto;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;

public class ConcentrationUnitDto {
  private ConcentrationUnit name;
  private String units;

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public ConcentrationUnit getName() {
    return name;
  }

  public void setName(ConcentrationUnit name) {
    this.name = name;
  }

}
