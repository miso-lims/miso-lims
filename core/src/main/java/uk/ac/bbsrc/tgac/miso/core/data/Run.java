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

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * A Run represents a sequencing run on a single sequencing instrument, referenced by a {@link SequencerReference}, comprising one or more
 * {@link SequencerPartitionContainer} objects in which {@link Pool}s are placed on {@link SequencerPoolPartition}s.
 * <p/>
 * Runs can be QCed via {@link RunQC} objects, and are always associated with a given {@link PlatformType}
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties({ "securityProfile", "submissionDocument" })
public interface Run
    extends SecurableByProfile, Comparable<Run>, Reportable<Run>, Watchable, Deletable, Nameable, Alertable, ChangeLoggable, Aliasable {
  /** Field PREFIX */
  public static final String PREFIX = "RUN";

  public void setId(long id);

  /**
   * Returns the sequencerReference of this Run object.
   * 
   * @return SequencerReference sequencerReference.
   */
  public SequencerReference getSequencerReference();

  /**
   * Sets the platformType of this Run object.
   * 
   * @param sequencerReference
   *          SequencerReference.
   */
  public void setSequencerReference(SequencerReference sequencerReference);

  public List<SequencerPartitionContainer> getSequencerPartitionContainers();

  public void setSequencerPartitionContainers(List<SequencerPartitionContainer> containers);

  /**
   * Sets a single SequencerPartitionContainer for this Run as long as the run previously had 0-1 containers.
   * Cannot replace multiple containers with a single container
   * 
   * @param container
   * @throws IllegalArgumentException if the Run already has more than one container
   */
  public void setSequencerPartitionContainer(SequencerPartitionContainer container);

  public void addSequencerPartitionContainer(SequencerPartitionContainer sequencerPartitionContainer);

  /**
   * Returns the platformType of this Run object.
   * 
   * @return PlatformType platformType.
   */
  public PlatformType getPlatformType();

  /**
   * Sets the platformType of this Run object.
   * 
   * @param platformType
   *          PlatformType.
   */
  public void setPlatformType(PlatformType platformType);

  /**
   * Returns the accession of this Run object.
   * 
   * @return String accession.
   */
  public String getAccession();

  /**
   * Sets the accession of this Run object.
   * 
   * @param accession
   *          String.
   */
  public void setAccession(String accession);

  /**
   * Returns the alias of this Run object.
   * 
   * @return String alias.
   */
  @Override
  public String getAlias();

  /**
   * Sets the alias of this Run object.
   * 
   * @param alias
   *          String.
   */
  public void setAlias(String alias);

  /**
   * Sets the name of this Run object.
   * 
   * @param name
   *          name.
   */
  public void setName(String name);

  /**
   * Returns the description of this Run object.
   * 
   * @return String description.
   */
  public String getDescription();

  /**
   * Sets the description of this Run object.
   * 
   * @param description
   *          description.
   */
  public void setDescription(String description);

  /**
   * Returns the platformRunId of this Run object.
   * 
   * @return Integer platformRunId.
   */
  public Integer getPlatformRunId();

  /**
   * Sets the platformRunId of this Run object.
   * 
   * @param platformRunId
   *          the actual run number on the sequencing instrument.
   */
  public void setPlatformRunId(Integer platformRunId);

  /**
   * Returns the cycles of this Run object.
   * 
   * @return Integer cycles.
   */
  public Integer getCycles();

  /**
   * Sets the cycles of this Run object.
   * 
   * @param cycles
   *          cycles.
   */
  public void setCycles(Integer cycles);

  /**
   * Returns the pairedEnd attribute of this Run object.
   * 
   * @return Boolean pairedEnd.
   */
  public Boolean getPairedEnd();

  /**
   * Sets the pairedEnd attribute of this Run object.
   * 
   * @param pairedEnd
   *          pairedEnd.
   */
  public void setPairedEnd(Boolean pairedEnd);

  /**
   * Returns the filePath of this Run object.
   * 
   * @return String filePath.
   */
  public String getFilePath();

  /**
   * Sets the filePath of this Run object.
   * 
   * @param filePath
   *          filePath.
   * 
   */
  public void setFilePath(String filePath);

  /**
   * Returns the status of this Run object.
   * 
   * @return Status status.
   */
  public Status getStatus();

  /**
   * Sets the status of this Run object.
   * 
   * @param status
   *          status.
   */
  public void setStatus(Status status);

  /**
   * Registers that a RunQC has been carried out on this Run
   * 
   * @param runQC
   *          of type RunQC
   * @throws MalformedRunQcException
   *           when the RunQC registered is not valid
   */
  public void addQc(RunQC runQC) throws MalformedRunQcException;

  /**
   * Returns the RunQC carried out on this Run object.
   * 
   * @return Collection<RunQC> runQCs.
   */
  // @JsonManagedReference(value="runqcs")
  public Collection<RunQC> getRunQCs();

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

  Date getLastUpdated();

  public Collection<ChangeLog> getChangeLogs();

  /**
   * Returns the user who last modified this item.
   */
  public User getLastModifier();

  /**
   * Sets the user who last modified this item. It should always be set to the current user on save.
   */
  public void setLastModifier(User user);

  public SequencingParameters getSequencingParameters();

  public void setSequencingParameters(SequencingParameters parameters);

  public void setWatchGroup(Group watchGroup);
}
