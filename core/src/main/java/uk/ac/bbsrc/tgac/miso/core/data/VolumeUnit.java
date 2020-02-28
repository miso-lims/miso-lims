package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.stream.Stream;

public enum VolumeUnit {

  MICROLITRES("&#181;L", "µL");

  private final String units;
  private final String rawLabel;

  private VolumeUnit(String units, String rawLabel) {
    this.units = units;
    this.rawLabel = rawLabel;
  }

  public String getUnits() {
    return units;
  }

  public String getRawLabel() {
    return rawLabel;
  }

  /**
   * Finds the VolumeUnit corresponding to a unit String, or null if no VolumeUnit is found.
   * 
   * @param units The units of the VolumeUnit as a String
   * @return VolumeUnit The VolumeUnit with the specified units, or null if no VolumeUnits have the specified units
   */
  public static VolumeUnit getFromString(String units) {
    return Stream.of(VolumeUnit.values()).filter(volumeUnit -> volumeUnit.getUnits().equals(units)).findFirst()
        .orElse(null);
  }

}
