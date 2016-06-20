package uk.ac.bbsrc.tgac.miso.core.data;

public interface SampleTissue extends SampleAdditionalInfo {
  
  public static final String CATEGORY_NAME = "Tissue";

  Integer getPassageNumber();

  void setPassageNumber(Integer passageNumber);

  Integer getTimesReceived();

  void setTimesReceived(Integer timesReceived);

  Integer getTubeNumber();

  void setTubeNumber(Integer tubeNumber);

  Integer getCellularity();

  void setCellularity(Integer cellularity);

  TissueOrigin getTissueOrigin();

  void setTissueOrigin(TissueOrigin tissueOrigin);

  TissueType getTissueType();

  void setTissueType(TissueType tissueType);

  /**
   * @return the sample's identifying name or ID at the source Institute
   */
  String getExternalInstituteIdentifier();

  /**
   * Sets the sample's identifying name or ID at the source Institute
   * 
   * @param externalInstituteIdentifier
   */
  void setExternalInstituteIdentifier(String externalInstituteIdentifier);

  Lab getLab();

  void setLab(Lab lab);

}
