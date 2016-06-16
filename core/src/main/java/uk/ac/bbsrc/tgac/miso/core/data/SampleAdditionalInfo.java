package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

public interface SampleAdditionalInfo extends Sample {

  public Sample getParent();
  
  public void setParent(Sample parent);

  public Set<Sample> getChildren();

  @JsonIgnore
  public void setChildren(Set<Sample> children);

  SampleClass getSampleClass();

  void setSampleClass(SampleClass sampleClass);

  TissueOrigin getTissueOrigin();

  void setTissueOrigin(TissueOrigin tissueOrigin);

  TissueType getTissueType();

  void setTissueType(TissueType tissueType);

  Subproject getSubproject();

  void setSubproject(Subproject subproject);

  Integer getPassageNumber();

  void setPassageNumber(Integer passageNumber);

  Integer getTimesReceived();

  void setTimesReceived(Integer timesReceived);

  Integer getTubeNumber();

  void setTubeNumber(Integer tubeNumber);

  Double getConcentration();

  void setConcentration(Double concentration);

  Boolean getArchived();

  void setArchived(Boolean archived);

  QcPassedDetail getQcPassedDetail();

  void setQcPassedDetail(QcPassedDetail qcPassedDetail);

  KitDescriptor getPrepKit();

  void setPrepKit(KitDescriptor prepKit);

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

  /**
   * This method should ONLY be used for load/save coordination between the Hibernate and old SQL DAOs. For all other purposes, use
   * getPrepKit().getKitDescriptorId()
   * 
   * @return the Kit Descriptor ID loaded by/for Hibernate
   */
  Long getHibernateKitDescriptorId();

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
   * @return Long groupId
   */
  Long getGroupId();

  /**
   * Sets the Group ID string of this sample analyte.
   * 
   * @param Long
   *          groupId
   */
  void setGroupId(Long groupId);

  /**
   * Gets the Group Description string of this sample analyte.
   * 
   * @return String groupDescription
   */
  String getGroupDescription();

  /**
   * Sets the Group Description string of this sample analyte.
   * 
   * @param String
   *          groupDescription
   */
  void setGroupDescription(String groupDescription);

}