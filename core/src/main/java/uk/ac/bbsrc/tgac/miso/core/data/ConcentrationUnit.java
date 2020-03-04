package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.stream.Stream;

public enum ConcentrationUnit {

  NANOGRAMS_PER_MICROLITRE("ng/&#181;L", "µL"), //
  NANOMOLAR("nM"), //
  PICOMOLAR("pM");

  private final String units;
  private final String rawLabel;

  private ConcentrationUnit(String units) {
    this.units = units;
    this.rawLabel = units;
  }

  private ConcentrationUnit(String units, String rawLabel) {
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
   * Finds the ConcentrationUnit corresponding to a unit String, or null if no ConcentrationUnit is found.
   * 
   * @param units The units of the ConcentrationUnit as a String
   * @return ConcentrationUnit The ConcentrationUnit with the specified units, or null if no ConcentrationUnits have the specified units
   */
  public static ConcentrationUnit getFromString(String units) {
    return Stream.of(ConcentrationUnit.values()).filter(concentrationUnit -> concentrationUnit.getUnits().equals(units)).findFirst()
        .orElse(null);
  }

}
