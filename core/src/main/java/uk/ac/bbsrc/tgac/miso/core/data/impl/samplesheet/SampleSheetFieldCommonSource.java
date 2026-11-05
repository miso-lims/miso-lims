package uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet;

public enum SampleSheetFieldCommonSource {

  LIBRARY_ALIQUOT("Library Aliquot") {

    @Override
    public LabelledEnum[] getProperties() {
      return LibraryAliquotProperty.values();
    }

  }, //
  POOL("Pool") {

    @Override
    public LabelledEnum[] getProperties() {
      return PoolProperty.values();
    }

  }, //
  REQUISITION("Requisition") {

    @Override
    public LabelledEnum[] getProperties() {
      return RequisitionProperty.values();
    }

  }, //
  INSTRUMENT_MODEL("Instrument Model"), //
  INSTRUMENT_POSITION("Instrument Position"), //
  PARTITION("Partition"), //
  SEQUENCING_PARAMETERS("Sequencing Parameters") {

    @Override
    public LabelledEnum[] getProperties() {
      return SequencingParametersProperty.values();
    }

  }, //
  CURRENT_TIME("Current Date/Time");

  private final String label;

  private SampleSheetFieldCommonSource(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public LabelledEnum[] getProperties() {
    return null;
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
