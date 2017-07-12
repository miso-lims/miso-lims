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

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * A Sample contains information about the original material upon which a sequencing experiment is to be based.
 * <p/>
 * Samples can be used in any number of sequencing {@link Experiment}s in the form of a {@link Library} that is processed further into
 * pooled {@link LibraryDilution}s. Samples can be described further by a scientific name which, when enabled, will be checked against the
 * NCBI
 * Taxonomy database.
 * <p/>
 * Sample properties are specified mainly by the SRA schema requirements, e.g. they have a Sample type string based on an SRA enumeration.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("sample")
@JsonIgnoreProperties({ "securityProfile", "submissionDocument", "children", "parent" })
public interface Sample
    extends SecurableByProfile, Locatable, Comparable<Sample>, Deletable, Boxable, ChangeLoggable, Aliasable, Serializable {

  /** Field UNSAVED_ID */
  public static final Long UNSAVED_ID = 0L;
  /** Field PREFIX */
  public static final String PREFIX = "SAM";
  public void setId(long id);

  /**
   * Returns the accession of this Sample object.
   * 
   * @return String accession.
   */
  public String getAccession();

  /**
   * Sets the accession of this Sample object.
   * 
   * @param accession
   *          accession.
   */
  public void setAccession(String accession);

  /**
   * Sets the name of this Sample object.
   * 
   * @param name
   *          name.
   */
  public void setName(String name);

  /**
   * Returns the description of this Sample object.
   * 
   * @return String description.
   */
  public String getDescription();

  /**
   * Sets the description of this Sample object.
   * 
   * @param description
   *          description.
   */
  public void setDescription(String description);

  /**
   * Returns the scientificName of this Sample object. This should ideally match a taxon name of a species in the NCBI Taxonomy database.
   * 
   * @return String scientificName.
   */
  public String getScientificName();

  /**
   * Sets the scientificName of this Sample object. This should ideally match a taxon name of a species in the NCBI Taxonomy database.
   * 
   * @param scientificName
   *          scientificName.
   */
  public void setScientificName(String scientificName);

  /**
   * Returns the taxonIdentifier of this Sample object. This should ideally match a taxon ID of a strain in the NCBI Taxonomy database.
   * 
   * @return String taxonIdentifier.
   */
  public String getTaxonIdentifier();

  /**
   * Sets the taxonIdentifier of this Sample object. This should ideally match a taxon ID of a strain in the NCBI Taxonomy database.
   * 
   * @param taxonIdentifier
   *          taxonIdentifier.
   */
  public void setTaxonIdentifier(String taxonIdentifier);

  /**
   * Returns the project of this Sample object.
   * 
   * @return Project project.
   */
  // @JsonBackReference(value="project")
  public Project getProject();

  /**
   * Sets the project of this Sample object.
   * 
   * @param project
   *          project.
   */
  public void setProject(Project project);

  /**
   * Sets the notes of this Sample object.
   * 
   * @param notes
   *          notes.
   */
  public void setNotes(Collection<Note> notes);

  /**
   * Adds a Note to the Set of notes of this Sample object.
   * 
   * @param note
   *          Note.
   */
  public void addNote(Note note);

  /**
   * Returns the notes of this Sample object.
   * 
   * @return Collection<Note> notes.
   */
  public Collection<Note> getNotes();

  /**
   * Returns the change logs of this Sample object.
   * 
   * @return Collection<ChangeLog> change logs.
   */
  @Override
  public Collection<ChangeLog> getChangeLog();

  /**
   * Adds a Library that has been prepared from this Sample
   * 
   * @param library
   *          of type Library
   * @throws MalformedLibraryException
   *           when the Library added is not valid
   */
  public void addLibrary(Library library) throws MalformedLibraryException;

  /**
   * Returns the libraries prepared from this Sample object.
   * 
   * @return Collection<Library> libraries.
   */
  // @JsonManagedReference
  public Collection<Library> getLibraries();

  /**
   * Registers that a SampleQC has been carried out on this Library
   * 
   * @param sampleQc
   *          of type SampleQC
   */
  public void addQc(SampleQC sampleQc);

  /**
   * Returns the sampleType of this Sample object.
   * 
   * @return String sampleType.
   */
  public String getSampleType();

  /**
   * Sets the sampleType of this Sample object.
   * 
   * @param string
   *          sampleType.
   */
  public void setSampleType(String string);

  /**
   * Returns the receivedDate of this Sample object.
   * 
   * @return Date receivedDate.
   */
  public Date getReceivedDate();

  /**
   * Sets the receivedDate of this Sample object.
   * 
   * @param date
   *          receivedDate.
   */
  public void setReceivedDate(Date date);

  /**
   * Returns the qcPassed of this Sample object.
   * 
   * @return Boolean qcPassed.
   */
  public Boolean getQcPassed();

  /**
   * Sets the qcPassed attribute of this Sample object. This should be true when a suitable QC has been carried out that passes a given
   * result.
   * 
   * @param qcPassed
   *          qcPassed.
   */
  public void setQcPassed(Boolean qcPassed);

  /**
   * Returns the sampleQCs carried out on this Sample object.
   * 
   * @return Collection<SampleQC> sampleQCs.
   */
  public Collection<SampleQC> getSampleQCs();

  /**
   * Registers a collection of QCs to this Sample object.
   * 
   * @param qcs
   *          qcs.
   */
  void setQCs(Collection<SampleQC> qcs);

  // TODO: remove below fields to ChangeLoggable interface
  public User getLastModifier();

  public void setLastModifier(User user);

  @Override
  public Date getLastModified();

  public void setLastModified(Date lastModified);

  public User getCreator();

  public void setCreator(User user);

  public Date getCreationTime();

  public void setCreationTime(Date creationTime);

}
