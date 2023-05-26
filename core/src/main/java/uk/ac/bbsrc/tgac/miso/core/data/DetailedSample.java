package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentIdentityAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentTissueAttributes;

public interface DetailedSample extends Sample, GroupIdentifiable {

  @Override
  public DetailedSample getParent();

  public void setParent(DetailedSample parent);

  public List<DetailedSample> getChildren();

  public void setChildren(List<DetailedSample> children);

  SampleClass getSampleClass();

  void setSampleClass(SampleClass sampleClass);

  Subproject getSubproject();

  void setSubproject(Subproject subproject);

  Boolean getArchived();

  void setArchived(Boolean archived);

  /**
   * @return the number of this Sample amongst Samples of the same SampleClass sharing the same parent
   */
  Integer getSiblingNumber();

  /**
   * Specifies the number of this Sample amongst Samples of the same SampleClass sharing the same
   * parent
   * 
   * @param siblingNumber
   */
  void setSiblingNumber(Integer siblingNumber);

  /**
   * True if the entity is not a physical sample, but one created to create the appearance of a
   * complete hierarchy when partially processed sample is received by the lab.
   */
  Boolean isSynthetic();

  void setSynthetic(Boolean synthetic);

  /**
   * True if the sample's alias does not pass alias validation but cannot be changed (usually for
   * historical reasons). Setting this to true means the sample will skip alias validation (and
   * uniqueness validation, if enabled) during save.
   */
  boolean hasNonStandardAlias();

  void setNonStandardAlias(boolean nonStandardAlias);

  /**
   * @return the old LIMS' ID for this sample prior to being migrated to MISO
   */
  @Override
  Long getPreMigrationId();

  void setPreMigrationId(Long preMigrationId);

  /**
   * Transient field for storing the ID of the identity which will be at the root of the hierarchy for
   * this Detailed Sample
   * 
   * @return Long identityId
   */
  Long getIdentityId();

  void setIdentityId(Long identityId);

  /**
   * Field for storing the date of sample creation. This is not the date that the sample was entered
   * into MISO. This field is not automatically generated on sample creation and must be specified by
   * a user.
   * 
   * @return creationDate
   */
  LocalDate getCreationDate();

  /**
   * Sets the date of sample creation to the specified date. This is not the date the sample was
   * entered into MISO. This field is not automatically generated on sample creation and must be
   * specified by a user.
   * 
   * @param creationDate
   */
  void setCreationDate(LocalDate creationDate);

  public BigDecimal getNgUsed();

  public void setNgUsed(BigDecimal ngUsed);

  public ParentAttributes getParentAttributes();

  public void setParentAttributes(ParentAttributes parentAttributes);

  public ParentIdentityAttributes getIdentityAttributes();

  public ParentTissueAttributes getTissueAttributes();

}
