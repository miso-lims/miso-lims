package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.stream.Stream;

public enum VolumeUnit {

  MICROLITRES("&#181;L", "µL", "uL"), //
  MILLIGRAMS("mg");

  private final String units;
  private final String rawLabel;
  private final String alternateLabel;

  private VolumeUnit(String units) {
    this.units = units;
    this.rawLabel = units;
    this.alternateLabel = null;
  }

  private VolumeUnit(String units, String rawLabel, String alternateLabel) {
    this.units = units;
    this.rawLabel = rawLabel;
    this.alternateLabel = alternateLabel;
  }

  public String getUnits() {
    return units;
  }

  public String getRawLabel() {
    return rawLabel;
  }

  public String getAlternateLabel() {
    return alternateLabel;
  }

  /**
   * Finds the VolumeUnit corresponding to a unit String, or null if no VolumeUnit is found.
   * 
   * @param units The units of the VolumeUnit as a String
   * @return VolumeUnit The VolumeUnit with the specified units, or null if no VolumeUnits have the
   *         specified units
   */
  public static VolumeUnit getFromString(String units) {
    if (units == null) {
      return null;
    }
    return Stream.of(VolumeUnit.values())
        .filter(volumeUnit -> units.equalsIgnoreCase(volumeUnit.getRawLabel())
            || units.equalsIgnoreCase(volumeUnit.getAlternateLabel()))
        .findFirst().orElse(null);
  }

}
