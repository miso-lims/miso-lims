package uk.ac.bbsrc.tgac.miso.core.data.type;

public enum MetricCategory {

  // Note: ordinals are used for sorting
  RECEIPT("Receipt"), //
  EXTRACTION("Extraction"), //
  LIBRARY_PREP("Library Preparation"), //
  LIBRARY_QUALIFICATION("Library Qualification"), //
  FULL_DEPTH_SEQUENCING("Full Depth Sequencing"), //
  ANALYSIS_REVIEW("Analysis Review");

  private final String label;

  private MetricCategory(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public int getSortPriority() {
    return this.ordinal() + 1;
  }

}
