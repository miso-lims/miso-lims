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
import java.util.List;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * A Library is the first step in constructing sequenceable material from an initial {@link Sample}. A Library is then diluted down to a
 * {@link LibraryDilution}, and put in a {@link Pool}, which is then sequenced.
 * <p/>
 * Library properties are specified mainly by the SRA schema requirements, i.e. they have a {@link LibraryType}, a
 * {@link LibraryStrategyType} and a {@link LibrarySelectionType} which are SRA enumerations. Libraries also have a target {@link Platform}
 * and can be uniquely identified via {@link Index} objects for multiplexing purposes.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Library
    extends SecurableByProfile, Comparable<Library>, Barcodable, Locatable, Deletable, Boxable, ChangeLoggable, Aliasable, Serializable,
    QualityControllable<LibraryQC> {

  /** Field UNSAVED_ID */
  public static final long UNSAVED_ID = 0L;
  /** Field PREFIX */
  public static final String PREFIX = "LIB";

  public void setId(long id);

  /**
   * Sets the name of this Library object.
   * 
   * @param name
   *          name.
   */
  public void setName(String name);

  /**
   * Returns the description of this Library object.
   * 
   * @return String description.
   */
  public String getDescription();

  /**
   * Sets the description of this Library object.
   * 
   * @param description
   *          description.
   */
  public void setDescription(String description);

  /**
   * Returns the accession of this Library object.
   * 
   * @return String accession.
   */
  public String getAccession();

  /**
   * Sets the accession of this Library object.
   * 
   * @param accession
   *          accession.
   */
  public void setAccession(String accession);

  /**
   * Returns the sample of this Library object.
   * 
   * @return Sample sample.
   */
  public Sample getSample();

  /**
   * Sets the sample of this Library object.
   * 
   * @param sample
   *          sample.
   */
  public void setSample(Sample sample);

  /**
   * Sets the notes of this Library object.
   * 
   * @param notes
   *          notes.
   */
  public void setNotes(Collection<Note> notes);

  /**
   * Adds a Note to the Set of notes of this Library object.
   * 
   * @param note
   *          Note
   */
  public void addNote(Note note);

  /**
   * Returns the notes of this Library object.
   * 
   * @return Collection<Note> notes.
   */
  public Collection<Note> getNotes();

  /**
   * Registers that a LibraryDilution has been carried out using this Library
   * 
   * @param libraryDilution
   *          of type LibraryDilution
   * @throws MalformedDilutionException
   *           when the LibraryDilution being added is not valid
   */
  public void addDilution(LibraryDilution libraryDilution);

  /**
   * Returns the libraryDilutions of this Library object.
   * 
   * @return Collection<LibraryDilution> libraryDilutions.
   */
  public Collection<LibraryDilution> getLibraryDilutions();

  /**
   * Returns the paired attribute of this Library object.
   * 
   * @return Boolean paired.
   */
  Boolean getPaired();

  /**
   * Sets the paired attribute of this Library object, i.e. true is paired, false is single.
   * 
   * @param paired
   *          paired.
   */
  void setPaired(Boolean paired);

  /**
   * Returns the libraryType of this Library object.
   * 
   * @return LibraryType libraryType.
   */
  public LibraryType getLibraryType();

  /**
   * Sets the libraryType of this Library object.
   * 
   * @param libraryType
   *          libraryType.
   */
  public void setLibraryType(LibraryType libraryType);

  /**
   * Returns the librarySelectionType of this Library object.
   * 
   * @return LibrarySelectionType librarySelectionType.
   */
  public LibrarySelectionType getLibrarySelectionType();

  /**
   * Sets the librarySelectionType of this Library object.
   * 
   * @param librarySelectionType
   *          LibrarySelectionType.
   */
  public void setLibrarySelectionType(LibrarySelectionType librarySelectionType);

  /**
   * Returns the libraryStrategyType of this Library object.
   * 
   * @return LibraryStrategyType libraryStrategyType.
   */
  public LibraryStrategyType getLibraryStrategyType();

  /**
   * Sets the libraryStrategyType of this Library object.
   * 
   * @param libraryStrategyType
   *          LibraryStrategyType.
   */
  public void setLibraryStrategyType(LibraryStrategyType libraryStrategyType);

  /**
   * Returns the position-indexed list of Indices for this Library object.
   */
  public List<Index> getIndices();

  /**
   * Sets the position-indexed list of Indices for this Library object.
   */
  public void setIndices(List<Index> indices);

  /**
   * Returns the platformType of this Library object.
   * 
   * @return PlatformType platformType.
   */
  public PlatformType getPlatformType();

  /**
   * Sets the platformType of this Library object.
   * 
   * @param PlatformType platformType.
   * 
   */
  public void setPlatformType(PlatformType platformType);

  /**
   * Sets the platformType of this Library object.
   * 
   * @param String platformType
   */
  public void setPlatformType(String platformName);

  /**
   * Returns the initialConcentration of this Library object.
   * 
   * @return Double initialConcentration.
   */
  public Double getInitialConcentration();

  /**
   * Sets the initialConcentration of this Library object.
   * 
   * @param initialConcentration
   *          initialConcentration.
   */
  public void setInitialConcentration(Double initialConcentration);

  /**
   * Returns the qcPassed of this Library object.
   * 
   * @return Boolean qcPassed.
   */
  public Boolean getQcPassed();

  /**
   * Sets the qcPassed attribute of this Library object. This should be true when a suitable QC has been carried out that passes a given
   * result.
   * 
   * @param qcPassed
   *          qcPassed.
   */
  public void setQcPassed(Boolean qcPassed);

  @Override
  public Collection<ChangeLog> getChangeLog();

  /**
   * Set the flag that this library is sufficiently bad that it is not worth sequencing.
   */
  public void setLowQuality(boolean lowquality);

  public boolean isLowQuality();

  public IndexFamily getCurrentFamily();

  Integer getDnaSize();

  void setDnaSize(Integer dnaSize);

  /**
   * @return the user-specified date that this Library was created
   */
  public Date getCreationDate();

  /**
   * Sets the user-specified date that this Library was created
   * 
   * @param creationDate
   */
  public void setCreationDate(Date creationDate);

  // TODO: remove below fields to ChangeLoggable interface

  /**
   * Returns the user who last modified this item.
   */
  public User getLastModifier();

  /**
   * Sets the user who last modified this item. It should always be set to the current user on save.
   */
  public void setLastModifier(User user);

  @Override
  public Date getLastModified();

  public void setLastModified(Date lastModified);

  public User getCreator();

  public void setCreator(User user);

  /**
   * @return the time this entity was first persisted
   */
  public Date getCreationTime();

  /**
   * Sets the time that this entity was first persisted
   * 
   * @param creationTime
   */
  public void setCreationTime(Date creationTime);

  KitDescriptor getKitDescriptor();

  void setKitDescriptor(KitDescriptor prepKit);

  public Date getReceivedDate();

  public void setReceivedDate(Date date);

}
