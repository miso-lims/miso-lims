/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.event.listener.RunListener;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.visitor.SubmittableVisitor;
import uk.ac.bbsrc.tgac.miso.core.event.model.RunEvent;
import uk.ac.bbsrc.tgac.miso.core.event.model.StatusChangedEvent;
import uk.ac.bbsrc.tgac.miso.core.event.type.MisoEventType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Skeleton implementation of a Run
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "`Run`")
public abstract class AbstractRun implements Run {
  protected static final Logger log = LoggerFactory.getLogger(AbstractRun.class);

  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long runId = AbstractRun.UNSAVED_ID;

  @Transient
  public Document submissionDocument;

  @OneToOne(cascade = CascadeType.ALL)
  private SecurityProfile securityProfile;

  private String name;
  private String alias;
  private String description;
  private String accession;
  private Integer platformRunId;
  private Boolean pairedEnd;
  private Integer cycles;
  private String filePath;

  private Date lastUpdated;

  @OneToOne(targetEntity = StatusImpl.class, cascade = CascadeType.ALL)
  private Status status;

  private Collection<RunQC> runQCs = new TreeSet<RunQC>();

  private Collection<Note> notes = new HashSet<Note>();

  @Transient
  @Enumerated(EnumType.STRING)
  private PlatformType platformType;
  private SequencerReference sequencerReference;

  // listeners
  private Set<MisoListener> listeners = new HashSet<MisoListener>();
  private Set<User> watchers = new HashSet<User>();

  @Deprecated
  public Long getRunId() {
    return runId;
  }

  @Deprecated
  public void setRunId(Long runId) {
    this.runId = runId;
  }

  @Override
  public long getId() {
    return runId;
  }

  public void setId(long id) {
    this.runId = id;
  }

  public SequencerReference getSequencerReference() {
    return sequencerReference;
  }

  public void setSequencerReference(SequencerReference sequencerReference) {
    this.sequencerReference = sequencerReference;
  }

  @Override
  public abstract List<SequencerPartitionContainer<SequencerPoolPartition>> getSequencerPartitionContainers();

  @Override
  public abstract void setSequencerPartitionContainers(List<SequencerPartitionContainer<SequencerPoolPartition>> containers);

  @Override
  public abstract void addSequencerPartitionContainer(SequencerPartitionContainer<SequencerPoolPartition> sequencerPartitionContainer);

  public PlatformType getPlatformType() {
    return platformType;
  }

  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  public String getAccession() {
    return accession;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getPlatformRunId() {
    return platformRunId;
  }

  public void setPlatformRunId(Integer platformRunId) {
    this.platformRunId = platformRunId;
  }

  public Integer getCycles() {
    return cycles;
  }

  public void setCycles(Integer cycles) {
    this.cycles = cycles;
  }

  public Boolean getPairedEnd() {
    return pairedEnd;
  }

  public void setPairedEnd(Boolean pairedEnd) {
    this.pairedEnd = pairedEnd;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    if (this.status != null && status != null) {
      if (!status.getHealth().equals(this.status.getHealth())) {
        this.status = status;

        if (status.getHealth().equals(HealthType.Started)) {
          fireRunStartedEvent();
        }
        else if (status.getHealth().equals(HealthType.Completed)) {
          fireRunCompletedEvent();
        }
        else if (status.getHealth().equals(HealthType.Failed)) {
//          if (status.getCompletionDate() == null) {
//            status.setCompletionDate(new Date());
//          }
          fireRunFailedEvent();
        }
        else {
          fireStatusChangedEvent();
        }
      }
    }
    this.status = status;
  }

  public void addQc(RunQC runQC) throws MalformedRunQcException {
    fireRunQcAddedEvent();

    this.runQCs.add(runQC);
    try {
      runQC.setRun(this);
    }
    catch (MalformedRunException e) {
      e.printStackTrace();
    }
  }

  public Collection<RunQC> getRunQCs() {
    return runQCs;
  }

  public void setQCs(Collection<RunQC> qcs) {
    this.runQCs = qcs;
  }

  public Collection<Note> getNotes() {
    return notes;
  }

  public void addNote(Note note) {
    this.notes.add(note);
  }

  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public Document getSubmissionData() {
    return submissionDocument;
  }

  public void accept(SubmittableVisitor v) {
    v.visit(this);
  }   

  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }

  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
  }

  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  public void inheritPermissions(SecurableByProfile parent) throws SecurityException {
    if (parent.getSecurityProfile().getOwner() != null) {
      setSecurityProfile(parent.getSecurityProfile());
    }
    else {
      throw new SecurityException("Cannot inherit permissions when parent object owner is not set!");
    }
  }  

  public abstract void buildReport();

  @Override
  public Set<MisoListener> getListeners() {
    return this.listeners;
  }

  @Override
  public boolean addListener(MisoListener listener) {
    return listeners.add(listener);
  }

  @Override
  public boolean removeListener(MisoListener listener) {
    return listeners.remove(listener);
  }

  protected void fireRunStartedEvent() {
    if (this.getId() != 0L) {
      RunEvent re = new RunEvent(this, MisoEventType.RUN_STARTED, "Run started");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(re);
      }
    }
  }

  protected void fireRunCompletedEvent() {
    if (this.getId() != 0L) {
      RunEvent re = new RunEvent(this, MisoEventType.RUN_COMPLETED, "Run completed");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(re);
      }
    }
  }

  protected void fireRunFailedEvent() {
    if (this.getId() != 0L) {
      RunEvent re = new RunEvent(this, MisoEventType.RUN_FAILED, "Run failed");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(re);
      }
    }
  }

  protected void fireStatusChangedEvent() {
    if (this.getId() != 0L) {
      StatusChangedEvent<Run> event = new StatusChangedEvent<Run>(this, getStatus());
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(event);
      }
    }
  }

  protected void fireRunQcAddedEvent() {
    if (this.getId() != 0L) {
      RunEvent re = new RunEvent(this, MisoEventType.RUN_QC_ADDED, "Run QC added");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(re);
      }
    }
  }

  @Override
  public Set<User> getWatchers() {
    return watchers;
  }

  @Override
  public void setWatchers(Set<User> watchers) {
    this.watchers = watchers;
  }

  @Override
  public void addWatcher(User user) {
    watchers.add(user);
  }

  @Override
  public void removeWatcher(User user) {
    watchers.remove(user);
  }

  @Override
  public String getWatchableIdentifier() {
    return getName();
  }

  public boolean isDeletable() {
    return getId() != AbstractQC.UNSAVED_ID;
  }

  /**
   * Equivalency is based on getRunId() if set, otherwise on name
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof AbstractRun))
      return false;
    Run them = (Run) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == AbstractRun.UNSAVED_ID
        || them.getId() == AbstractRun.UNSAVED_ID) {
      return getAlias().equals(them.getAlias()); //&& this.getDescription().equals(them.getDescription());
    }
    else {
      return getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != AbstractRun.UNSAVED_ID) {
      return (int)getId();
    }
    else {
      final int PRIME = 37;
      int hashcode = 1;
      if (getAlias() != null) hashcode = PRIME * hashcode + getAlias().hashCode();
      //if (getDescription() != null) hashcode = PRIME * hashcode + getDescription().hashCode();
      //if (getExperiment() != null) hashcode = 37 * hashcode + getExperiment().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    Run t = (Run)o;
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getAccession());
    sb.append(" : ");
    sb.append(getPlatformType());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getDescription());
    sb.append(" : ");
    sb.append(getFilePath());
    sb.append(" : ");

    if (getStatus() != null) {
      sb.append(getStatus().getHealth());
      sb.append("("+getStatus().getStatusId()+")");
    }
    return sb.toString();
  }
}
