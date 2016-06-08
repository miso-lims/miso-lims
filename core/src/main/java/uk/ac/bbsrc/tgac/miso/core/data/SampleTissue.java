package uk.ac.bbsrc.tgac.miso.core.data;

public interface SampleTissue extends SampleAdditionalInfo {
  
  public static final String CATEGORY_NAME = "Tissue";

  Integer getCellularity();

  void setCellularity(Integer cellularity);

}
