package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Set;

public interface DetailedSample extends Sample {

  public DetailedSample getParent();

  public void setParent(DetailedSample parent);

  public Set<DetailedSample> getChildren();

  public void setChildren(Set<DetailedSample> children);

  SampleClass getSampleClass();

  void setSampleClass(SampleClass sampleClass);

  Subproject getSubproject();

  void setSubproject(Subproject subproject);

  Boolean getArchived();

  void setArchived(Boolean archived);

  DetailedQcStatus getDetailedQcStatus();

  void setDetailedQcStatus(DetailedQcStatus detailedQcStatus);

  /**
   * @return the number of this Sample amongst Samples of the same SampleClass sharing the same parent
   */
  Integer getSiblingNumber();

  /**
   * Specifies the number of this Sample amongst Samples of the same SampleClass sharing the same parent
   * 
   * @param siblingNumber
   */
  void setSiblingNumber(Integer siblingNumber);

  /**
   * Gets the Group ID string of this sample analyte.
   * 
   * @return String groupId
   */
  String getGroupId();

  /**
   * Sets the Group ID string of this sample analyte.
   * 
   * @param Long groupId
   */
  void setGroupId(String groupId);

  /**
   * Gets the Group Description string of this sample analyte.
   * 
   * @return String groupDescription
   */
  String getGroupDescription();

  /**
   * Sets the Group Description string of this sample analyte.
   * 
   * @param String groupDescription
   */
  void setGroupDescription(String groupDescription);

  /**
   * True if the entity is not a physical sample, but one created to create the appearance of a complete hierarchy when partially processed
   * sample is received by the lab.
   */
  Boolean isSynthetic();

  void setSynthetic(Boolean synthetic);

  /**
   * True if the sample's alias does not pass alias validation but cannot be changed (usually for historical reasons). Setting this to true
   * means the sample will skip alias validation (and uniqueness validation, if enabled) during save.
   */
  boolean hasNonStandardAlias();

  void setNonStandardAlias(boolean nonStandardAlias);
  
  /**
   * @return the old LIMS' ID for this sample prior to being migrated to MISO
   */
  @Override
  Long getPreMigrationId();
  
  void setPreMigrationId(Long preMigrationId);

  String getDetailedQcStatusNote();

  void setDetailedQcStatusNote(String detailedQcStatusNote);

  /**
   * Transient field for storing the ID of the identity which will be at the root of the hierarchy for this Detailed Sample
   * 
   * @return Long identityId
   */
  Long getIdentityId();

  void setIdentityId(Long identityId);

  Double getConcentration();

  void setConcentration(Double concentration);

}