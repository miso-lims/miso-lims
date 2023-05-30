package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;

/**
 * A Project represents the top level object in the MISO data model. A Project couples together
 * {@link Study} and {@link Sample} objects to record information about a given sequencing project.
 * <p/>
 * A Project's status is tracked by its {@link StatusType} enumeration.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Project extends Comparable<Project>, Deletable, Nameable, Serializable, Attachable, ChangeLoggable {

  /** Field PREFIX */
  public static final String PREFIX = "PRO";

  String getDescription();

  void setDescription(String description);

  void setName(String name);

  /**
   * Returns the title of this Project object.
   * 
   * @return String title.
   */
  String getTitle();

  /**
   * Sets the title of this Project object.
   * 
   * @param title title.
   */
  void setTitle(String title);

  /**
   * Returns the code, used as a prefix for generating sample names, or the title if not specified.
   */
  String getCode();

  /**
   * Sets the code, used as a prefix for generating sample names.
   */
  void setCode(String code);

  /**
   * @return the status of this Project
   */
  StatusType getStatus();

  /**
   * Sets the status of this Project
   * 
   * @param status
   */
  void setStatus(StatusType status);

  /**
   * Returns the registered studies of this Project object.
   * 
   * @return Collection<Study> studies.
   */
  Collection<Study> getStudies();

  /**
   * Registers a collection of studies to this Project object.
   * 
   * @param studies studies.
   */
  void setStudies(Collection<Study> studies);

  public ReferenceGenome getReferenceGenome();

  public void setReferenceGenome(ReferenceGenome referenceGenome);

  public TargetedSequencing getDefaultTargetedSequencing();

  public void setDefaultTargetedSequencing(TargetedSequencing defaultTargetedSequencing);

  @Override
  public boolean isSaved();

  public Pipeline getPipeline();

  public void setPipeline(Pipeline pipeline);

  public boolean isSecondaryNaming();

  public void setSecondaryNaming(boolean secondaryNaming);

  public String getRebNumber();

  public void setRebNumber(String rebNumber);

  public LocalDate getRebExpiry();

  public void setRebExpiry(LocalDate rebExpiry);

  public Integer getSamplesExpected();

  public void setSamplesExpected(Integer samplesExpected);

  public Contact getContact();

  public void setContact(Contact contact);

}
