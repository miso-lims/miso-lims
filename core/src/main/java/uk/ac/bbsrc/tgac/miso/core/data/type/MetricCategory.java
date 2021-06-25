package uk.ac.bbsrc.tgac.miso.core.data.type;

public enum MetricCategory {

  RECEIPT("Receipt"), //
  EXTRACTION("Extraction"), //
  LIBRARY_PREP("Library Preparation"), //
  LOW_PASS_SEQUENCING("Low Pass Sequencing"), //
  FULL_DEPTH_SEQUENCING("Full Depth Sequencing"), //
  INFORMATICS("Informatics Pipelines + Variant Interpretation");
  
  private final String label;
  
  private MetricCategory(String label) {
    this.label = label;
  }
  
  public String getLabel() {
    return label;
  }
  
}
