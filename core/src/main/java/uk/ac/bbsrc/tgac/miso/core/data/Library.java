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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * A Library is the first step in constructing sequenceable material from an initial {@link Sample}. A Library is then diluted down to a
 * {@link Dilution}, and put in a {@link Pool}, which is then sequenced.
 * <p/>
 * Library properties are specified mainly by the SRA schema requirements, i.e. they have a {@link LibraryType}, a
 * {@link LibraryStrategyType} and a {@link LibrarySelectionType} which are SRA enumerations. Libraries also have a target {@link Platform}
 * and can be uniquely identified via {@link Index} objects for multiplexing purposes.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL) // , using = LibrarySerializer.class)
@JsonTypeName("library")
@JsonIgnoreProperties({ "securityProfile" })
public interface Library extends SecurableByProfile, Comparable<Library>, Barcodable, Locatable, Deletable, Boxable {

  /** Field UNSAVED_ID */
  public static final Long UNSAVED_ID = 0L;
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
   * Get the tissue from which this library was derived, if one exists. Null otherwise.
   */
  public SampleTissue getSampleTissue();

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
   * Registers that a LibraryQC has been carried out on this Library
   * 
   * @param libraryQC
   *          of type LibraryQC
   * @throws MalformedLibraryQcException
   *           when the LibraryQC being added is not valid
   */
  public void addQc(LibraryQC libraryQC) throws MalformedLibraryQcException;

  /**
   * Returns the libraryQCs of this Library object.
   * 
   * @return Collection<LibraryQC> libraryQCs.
   */
  public Collection<LibraryQC> getLibraryQCs();

  /**
   * Registers that a LibraryDilution has been carried out using this Library
   * 
   * @param libraryDilution
   *          of type LibraryDilution
   * @throws MalformedDilutionException
   *           when the LibraryDilution being added is not valid
   */
  public void addDilution(LibraryDilution libraryDilution) throws MalformedDilutionException;

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
   * Returns the platformName of this Library object.
   * 
   * @return String platformName.
   */
  public String getPlatformName();

  /**
   * Sets the platformName of this Library object.
   * 
   * @param platformName
   *          platformName.
   * 
   */
  public void setPlatformName(String platformName);

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
   * Returns the creationDate of this Library object.
   * 
   * @return Date creationDate.
   */
  public Date getCreationDate();

  /**
   * Sets the creationDate of this Library object.
   * 
   * @param date
   *          creationDate.
   */
  public void setCreationDate(Date date);

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

  public Collection<ChangeLog> getChangeLog();

  /**
   * Returns the user who last modified this item.
   */
  public User getLastModifier();

  /**
   * Sets the user who last modified this item. It should always be set to the current user on save.
   */
  public void setLastModifier(User user);

  /**
   * Set the flag that this library is sufficiently bad that it is not worth sequencing.
   */
  public void setLowQuality(boolean lowquality);

  public boolean isLowQuality();

  public LibraryAdditionalInfo getLibraryAdditionalInfo();

  public void setLibraryAdditionalInfo(LibraryAdditionalInfo libraryAdditionalInfo);

  public IndexFamily getCurrentFamily();

}
