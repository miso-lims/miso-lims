/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.PoolBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * A Pool represents a collection of one or more {@link Poolable} objects, which enables multiplexing to be modelled if necessary. Pools
 * provide the link between the {@link Sample} tree and the {@link Run} tree of the MISO data model, which means that multiple samples from
 * multiple {@link Project}s can be pooled together.
 * <p/>
 * Pools are typed by the {@link Poolable} interface type they can accept, and as such, Pools can accept {@link LibraryDilution}
 * objects at present. At creation time, a Pool is said to be "ready to run", which makes it easy to categorise and list Pools according to
 * whether they have been placed on a {@link SequencerPoolPartition} (at which point ready to run becomes false) or not.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Pool
    extends SecurableByProfile, Comparable<Pool>, Barcodable, Watchable, Boxable, Nameable, ChangeLoggable,
    Serializable, Aliasable, QualityControllable<PoolQC>, Deletable, Attachable {

  /**
   * Sets the ID of this Pool object.
   * 
   * @param id long.
   */
  public void setId(long id);

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
  public Set<PoolDilution> getPoolDilutions();

  /**
   * Sets the elements of this Pool object.
   */
  public void setPoolDilutions(Set<PoolDilution> poolDilutions);

  /**
   * Returns the concentration of this Pool object.
   * 
   * @return Double concentration.
   */
  public Double getConcentration();

  /**
   * Sets the concentration of this Pool object.
   * 
   * @param concentration concentration.
   */
  public void setConcentration(Double concentration);

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
   * Sets the qcPassed attribute of this Pool object. This should be true when a suitable QC has been carried out that passes a given
   * result.
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

  Set<String> getDuplicateIndicesSequences();

  Set<String> getNearDuplicateIndicesSequences();

  @Override
  void setWatchGroup(Group group);

  public void setBoxPosition(PoolBoxPosition boxPosition);

  /**
   * @return the user-specified date that this Pool was created
   */
  public Date getCreationDate();

  /**
   * Sets the user-specified date that this Pool was created
   * 
   * @param creationDate
   */
  public void setCreationDate(Date creationDate);

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

}
