package uk.ac.bbsrc.tgac.miso.dto;

import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;

public class VolumeUnitDto {
  private VolumeUnit name;
  private String units;

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public VolumeUnit getName() {
    return name;
  }

  public void setName(VolumeUnit name) {
    this.name = name;
  }

}
