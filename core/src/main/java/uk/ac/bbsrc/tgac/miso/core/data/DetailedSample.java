package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

public interface DetailedSample extends Sample {

  public DetailedSample getParent();

  public void setParent(DetailedSample parent);

  public Set<DetailedSample> getChildren();

  @JsonIgnore
  public void setChildren(Set<DetailedSample> children);

  SampleClass getSampleClass();

  void setSampleClass(SampleClass sampleClass);

  Subproject getSubproject();

  void setSubproject(Subproject subproject);

  Boolean getArchived();

  void setArchived(Boolean archived);

  QcPassedDetail getQcPassedDetail();

  void setQcPassedDetail(QcPassedDetail qcPassedDetail);

  KitDescriptor getPrepKit();

  void setPrepKit(KitDescriptor prepKit);

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

}