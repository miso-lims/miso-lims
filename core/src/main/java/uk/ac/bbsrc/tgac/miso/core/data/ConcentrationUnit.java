package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.stream.Stream;

public enum ConcentrationUnit {

  NANOGRAMS_PER_MICROLITRE("ng/&#181;L", "ng/µL", "ng/uL"), //
  NANOMOLAR("nM"), //
  PICOMOLAR("pM"), //
  CELLS_PER_MICROLITRE("cells/&#181;L", "cells/µL", "cells/uL"), //
  NUCLEI_PER_MICROLITRE("nuclei/&#181;L", "nuclei/µL", "nuclei/uL"), //
  CELLS_PER_MILLILITRE("cells/mL");

  private final String units;
  private final String rawLabel;
  private final String alternateLabel;

  private ConcentrationUnit(String units) {
    this.units = units;
    this.rawLabel = units;
    this.alternateLabel = null;
  }

  private ConcentrationUnit(String units, String rawLabel, String alternateLabel) {
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
   * Finds the ConcentrationUnit corresponding to a unit String, or null if no ConcentrationUnit is
   * found.
   * 
   * @param units The units of the ConcentrationUnit as a String
   * @return ConcentrationUnit The ConcentrationUnit with the specified units, or null if no
   *         ConcentrationUnits have the specified units
   */
  public static ConcentrationUnit getFromString(String units) {
    if (units == null) {
      return null;
    }
    return Stream.of(ConcentrationUnit.values())
        .filter(concentrationUnit -> units.equalsIgnoreCase(concentrationUnit.getRawLabel())
            || units.equalsIgnoreCase(concentrationUnit.getAlternateLabel()))
        .findFirst().orElse(null);
  }

}
