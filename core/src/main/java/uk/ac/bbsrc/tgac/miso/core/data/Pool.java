package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.PoolBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControllable;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * A Pool represents a collection of one or more {@link Poolable} objects, which enables
 * multiplexing to be modelled if necessary. Pools provide the link between the {@link Sample} tree
 * and the {@link Run} tree of the MISO data model, which means that multiple samples from multiple
 * {@link Project}s can be pooled together.
 * <p/>
 * Pools are typed by the {@link Poolable} interface type they can accept, and as such, Pools can
 * accept {@link LibraryAliquot} objects at present. At creation time, a Pool is said to be "ready
 * to run", which makes it easy to categorise and list Pools according to whether they have been
 * placed on a {@link SequencerPoolPartition} (at which point ready to run becomes false) or not.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Pool extends Comparable<Pool>, Boxable, QualityControllable<PoolQC>, Deletable, Attachable {

  /**
   * Sets the name of this Pool object.
   * 
   * @param name name.
   * 
   */
  public void setName(String name);

  /**
   * Returns the alias of this Pool object.
   * 
   * @return String alias.
   */
  @Override
  public String getAlias();

  /**
   * Sets the alias of this Pool object.
   * 
   * @param alias alias.
   * 
   */
  @Override
  public void setAlias(String alias);

  /**
   * Returns the elements of this Pool object.
   */
  public Set<PoolElement> getPoolContents();

  /**
   * Sets the elements of this Pool object.
   */
  public void setPoolElements(Set<PoolElement> poolElements);

  /**
   * Returns the concentration of this Pool object.
   * 
   * @return concentration.
   */
  public BigDecimal getConcentration();

  /**
   * Sets the concentration of this Pool object.
   * 
   * @param concentration
   */
  public void setConcentration(BigDecimal concentration);

  public Integer getDnaSize();

  public void setDnaSize(Integer dnaSize);

  /**
   * Returns the platformType of this Platform object.
   * 
   * @return PlatformType platformType.
   */
  public PlatformType getPlatformType();

  /**
   * Sets the platformType of this Platform object.
   * 
   * @param name platformType.
   */
  public void setPlatformType(PlatformType name);

  /**
   * Returns the poolQCs of this Pool object.
   * 
   * @return Collection<PoolQC> poolQCs.
   */
  @Override
  public Collection<PoolQC> getQCs();

  /**
   * Returns the qcPassed of this Pool object.
   * 
   * @return Boolean qcPassed.
   */
  public Boolean getQcPassed();

  /**
   * Sets the qcPassed attribute of this Pool object. This should be true when a suitable QC has been
   * carried out that passes a given result.
   * 
   * @param qcPassed qcPassed.
   */
  public void setQcPassed(Boolean qcPassed);

  @Override
  public Collection<ChangeLog> getChangeLog();

  public boolean getHasLowQualityMembers();

  /**
   * Sets the notes of this Pool object.
   *
   * @param notes notes.
   */
  public void setNotes(Collection<Note> notes);

  /**
   * Adds a Note to the Set of notes of this Pool object.
   *
   * @param note Note.
   */
  public void addNote(Note note);

  /**
   * Returns the notes of this Pool object.
   *
   * @return Collection<Note> notes.
   */
  public Collection<Note> getNotes();

  /**
   * Returns the description of this Pool object.
   * 
   * @return String description;
   */
  public String getDescription();

  /**
   * Adds the description of this Pool object
   * 
   * @param String description
   */
  void setDescription(String description);

  public void setBoxPosition(PoolBoxPosition boxPosition);

  /**
   * @return the user-specified date that this Pool was created
   */
  public LocalDate getCreationDate();

  /**
   * Sets the user-specified date that this Pool was created
   * 
   * @param creationDate
   */
  public void setCreationDate(LocalDate creationDate);

  public String getLongestIndex();

  boolean hasLibrariesWithoutIndex();

  /**
   * Returns the concentration units of this Pool object.
   * 
   * @return ConcentrationUnit concentrationUnits.
   */
  public ConcentrationUnit getConcentrationUnits();

  /**
   * Sets the concentrationUnits of this Pool object.
   * 
   * @param concentrationUnits concentrationUnits.
   */
  public void setConcentrationUnits(ConcentrationUnit concentrationUnits);

  /**
   * Returns the volume units of this Pool object.
   * 
   * @return VolumeUnit volumeUnits.
   */
  public VolumeUnit getVolumeUnits();

  /**
   * Sets the volumeUnits of this Pool object.
   * 
   * @param volumeUnits volumeUnits.
   */
  public void setVolumeUnits(VolumeUnit volumeUnits);

  /**
   * Returns the Aliases of all Subprojects with priority attached to this Pool object.
   * 
   * @return Set<String> The aliases of subprojects with priority.
   */
  public Set<String> getPrioritySubprojectAliases();

  @Override
  public int hashCode();

  @Override
  public boolean equals(Object obj);

  public boolean isMergeChild();

  public void makeMergeChild();

}
