package uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet;

public enum SampleSheetFieldCommonSource {

  LIBRARY_ALIQUOT("Library Aliquot"), //
  POOL("POOL"), //
  INSTRUMENT_MODEL("Instrument Model"), //
  INSTRUMENT_POSITION("Instrument Position"), //
  PARTITION("Partition"), //
  SEQUENCING_PARAMETERS("Sequencing Parameters"), //
  CURRENT_TIME("Current Date/Time");

  private final String label;

  private SampleSheetFieldCommonSource(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static SampleSheetFieldCommonSource get(String name) {
    for (SampleSheetFieldCommonSource x : SampleSheetFieldCommonSource.values()) {
      if (x.name().equals(name)) {
        return x;
      }
    }
    return null;
  }
}
