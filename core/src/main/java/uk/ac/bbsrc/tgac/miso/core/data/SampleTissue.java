package uk.ac.bbsrc.tgac.miso.core.data;

public interface SampleTissue extends DetailedSample {

  public static final String CATEGORY_NAME = "Tissue";

  public String getSecondaryIdentifier();

  /**
   * Sets the sample's identifying name or ID at the source Institute
   * 
   * @param secondaryIdentifier
   */
  void setSecondaryIdentifier(String secondaryIdentifier);

  Lab getLab();

  void setLab(Lab lab);

  Integer getPassageNumber();

  void setPassageNumber(Integer passageNumber);

  String getRegion();

  void setRegion(String region);

  Integer getTimesReceived();

  void setTimesReceived(Integer timesReceived);

  TissueMaterial getTissueMaterial();

  void setTissueMaterial(TissueMaterial tissueMaterial);

  TissueOrigin getTissueOrigin();

  void setTissueOrigin(TissueOrigin tissueOrigin);

  TissueType getTissueType();

  void setTissueType(TissueType tissueType);

  Integer getTubeNumber();

  void setTubeNumber(Integer tubeNumber);

}
