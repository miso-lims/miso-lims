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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Alertable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Watchable;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.event.model.ProjectOverviewEvent;
import uk.ac.bbsrc.tgac.miso.core.event.type.MisoEventType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.core.data
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties({ "project", "samples", "libraries", "runs", "qcPassedSamples" })
public class ProjectOverview implements Watchable, Alertable, Nameable, Serializable {

  private static final long serialVersionUID = 1L;

  protected static final Logger log = LoggerFactory.getLogger(ProjectOverview.class);

  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long overviewId = ProjectOverview.UNSAVED_ID;

  private Project project;

  private String principalInvestigator;

  private Set<Sample> sampleGroup;

  private Collection<Library> libraries = new HashSet<>();
  private Collection<Run> runs = new HashSet<>();
  private Collection<Note> notes = new HashSet<>();
  private Date startDate;
  private Date endDate;
  private Integer numProposedSamples;
  private boolean locked;
  private boolean allSampleQcPassed;
  private boolean libraryPreparationComplete;
  private boolean allLibrariesQcPassed;
  private boolean allPoolsConstructed;
  private boolean allRunsCompleted;
  private boolean primaryAnalysisCompleted;
  private final Set<MisoListener> listeners = new HashSet<>();

  // Cascade all operations except delete
  @ManyToMany(targetEntity = UserImpl.class, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
  @JoinTable(name = "ProjectOverview_Watcher", joinColumns = { @JoinColumn(name = "overviewId") },
      inverseJoinColumns = { @JoinColumn(name = "userId") })
  private Set<User> watchUsers = new HashSet<>();

  @Transient
  // not Hibernate-managed
  private Group watchGroup;

  private Date lastUpdated;

  @Override
  public String getName() {
    return "POV" + getId();
  }

  @Override
  public long getId() {
    return overviewId;
  }

  public void setId(Long overviewId) {
    this.overviewId = overviewId;
  }

  @Deprecated
  public Long getOverviewId() {
    return overviewId;
  }

  @Deprecated
  public void setOverviewId(Long overviewId) {
    this.overviewId = overviewId;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public String getPrincipalInvestigator() {
    return principalInvestigator;
  }

  public void setPrincipalInvestigator(String principalInvestigator) {
    this.principalInvestigator = principalInvestigator;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Integer getNumProposedSamples() {
    return numProposedSamples;
  }

  public void setNumProposedSamples(Integer numProposedSamples) {
    this.numProposedSamples = numProposedSamples;
  }

  public boolean getLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public Set<Sample> getSampleGroup() {
    return sampleGroup;
  }

  public void setSampleGroup(Set<Sample> sampleGroup) {
  }

  public Set<Sample> getSamples() {
    return sampleGroup;
  }

  @Deprecated
  public void setSamples(Set<Sample> samples) {
    sampleGroup = samples;
  }

  public Collection<Sample> getQcPassedSamples() {
    List<Sample> qcSamples = new ArrayList<>();
    if (getSampleGroup() != null) {
      for (Sample s : getSampleGroup()) {
        if (s != null && s.getQcPassed() != null && s.getQcPassed()) {
          qcSamples.add(s);
        }
      }
    }
    return qcSamples;
  }

  public Collection<Library> getLibraries() {
    return libraries;
  }

  public void setLibraries(Collection<Library> libraries) {
    this.libraries = libraries;
  }

  public Collection<Run> getRuns() {
    return runs;
  }

  public void setRuns(Collection<Run> runs) {
    this.runs = runs;
  }

  public Collection<Note> getNotes() {
    return notes;
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

  public boolean getAllSampleQcPassed() {
    return allSampleQcPassed;
  }

  public void setAllSampleQcPassed(boolean allSampleQcPassed) {
    if (this.allSampleQcPassed != allSampleQcPassed && allSampleQcPassed) {
      this.allSampleQcPassed = allSampleQcPassed;
      fireSampleQcPassedEvent();
    } else {
      this.allSampleQcPassed = allSampleQcPassed;
    }
  }

  public boolean getLibraryPreparationComplete() {
    return libraryPreparationComplete;
  }

  public void setLibraryPreparationComplete(boolean libraryPreparationComplete) {
    if (this.libraryPreparationComplete != libraryPreparationComplete && libraryPreparationComplete) {
      this.libraryPreparationComplete = libraryPreparationComplete;
      fireLibraryPreparationCompleteEvent();
    } else {
      this.libraryPreparationComplete = libraryPreparationComplete;
    }
  }

  public boolean getAllLibrariesQcPassed() {
    return allLibrariesQcPassed;
  }

  public void setAllLibrariesQcPassed(boolean allLibrariesQcPassed) {
    if (this.allLibrariesQcPassed != allLibrariesQcPassed && allLibrariesQcPassed) {
      this.allLibrariesQcPassed = allLibrariesQcPassed;
      fireLibraryQcPassedEvent();
    } else {
      this.allLibrariesQcPassed = allLibrariesQcPassed;
    }
  }

  public boolean getAllPoolsConstructed() {
    return allPoolsConstructed;
  }

  public void setAllPoolsConstructed(boolean allPoolsConstructed) {
    if (this.allPoolsConstructed != allPoolsConstructed && allPoolsConstructed) {
      this.allPoolsConstructed = allPoolsConstructed;
      firePoolsConstructedEvent();
    } else {
      this.allPoolsConstructed = allPoolsConstructed;
    }
  }

  public boolean getAllRunsCompleted() {
    return allRunsCompleted;
  }

  public void setAllRunsCompleted(boolean allRunsCompleted) {
    if (this.allRunsCompleted != allRunsCompleted && allRunsCompleted) {
      this.allRunsCompleted = allRunsCompleted;
      fireRunsCompletedEvent();
    } else {
      this.allRunsCompleted = allRunsCompleted;
    }
  }

  public boolean getPrimaryAnalysisCompleted() {
    return primaryAnalysisCompleted;
  }

  public void setPrimaryAnalysisCompleted(boolean primaryAnalysisCompleted) {
    if (this.primaryAnalysisCompleted != primaryAnalysisCompleted && primaryAnalysisCompleted) {
      this.primaryAnalysisCompleted = primaryAnalysisCompleted;
      firePrimaryAnalysisCompletedEvent();
    } else {
      this.primaryAnalysisCompleted = primaryAnalysisCompleted;
    }
  }

  protected void fireSampleQcPassedEvent() {
    if (this.getId() != 0L) {
      ProjectOverviewEvent poe = new ProjectOverviewEvent(this, MisoEventType.ALL_SAMPLES_QC_PASSED,
          this.getProject().getAlias() + " : all project samples have passed QC.");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(poe);
      }
    }
  }

  protected void fireLibraryPreparationCompleteEvent() {
    if (this.getId() != 0L) {
      ProjectOverviewEvent poe = new ProjectOverviewEvent(this, MisoEventType.LIBRARY_PREPARATION_COMPLETED,
          this.getProject().getAlias() + " : all libraries have been constructed.");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(poe);
      }
    }
  }

  protected void fireLibraryQcPassedEvent() {
    if (this.getId() != 0L) {
      ProjectOverviewEvent poe = new ProjectOverviewEvent(this, MisoEventType.ALL_LIBRARIES_QC_PASSED,
          this.getProject().getAlias() + " : all project libraries have passed QC.");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(poe);
      }
    }
  }

  protected void firePoolsConstructedEvent() {
    if (this.getId() != 0L) {
      ProjectOverviewEvent poe = new ProjectOverviewEvent(this, MisoEventType.POOL_CONSTRUCTION_COMPLETE,
          this.getProject().getAlias() + " : all project samples have now been pooled.");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(poe);
      }
    }
  }

  protected void fireRunsCompletedEvent() {
    if (this.getId() != 0L) {
      ProjectOverviewEvent poe = new ProjectOverviewEvent(this, MisoEventType.ALL_RUNS_COMPLETED,
          this.getProject().getAlias() + " : all project runs have now completed.");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(poe);
      }
    }
  }

  protected void firePrimaryAnalysisCompletedEvent() {
    if (this.getId() != 0L) {
      ProjectOverviewEvent poe = new ProjectOverviewEvent(this, MisoEventType.PRIMARY_ANALYSIS_COMPLETED,
          this.getProject().getAlias() + " : primary analysis has completed.");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(poe);
      }
    }
  }

  public void setWatchUsers(Set<User> watchUsers) {
    this.watchUsers = watchUsers;
  }

  public Set<User> getWatchUsers() {
    return watchUsers;
  }

  public void setWatchGroup(Group watchGroup) {
    this.watchGroup = watchGroup;
  }

  public Group getWatchGroup() {
    return watchGroup;
  }

  @Override
  public Set<User> getWatchers() {
    Set<User> allWatchers = new HashSet<>();
    if (watchGroup != null) allWatchers.addAll(watchGroup.getUsers());
    if (watchUsers != null) allWatchers.addAll(watchUsers);
    return allWatchers;
  }

  @Override
  public void addWatcher(User user) {
    watchUsers.add(user);
  }

  @Override
  public void removeWatcher(User user) {
    watchUsers.remove(user);
  }

  @Override
  public String getWatchableIdentifier() {
    return getName();
  }

  public boolean isDeletable() {
    return getId() != ProjectOverview.UNSAVED_ID;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    if (getSampleGroup() != null && !getSampleGroup().isEmpty()) {
      sb.append(" [ ");
      sb.append(LimsUtils.join(getSampleGroup(), ","));
      sb.append(" ] ");
    }
    return sb.toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 47)
        .append(allLibrariesQcPassed)
        .append(allPoolsConstructed)
        .append(allRunsCompleted)
        .append(allSampleQcPassed)
        .append(endDate)
        .append(libraryPreparationComplete)
        .append(locked)
        .append(numProposedSamples)
        .append(primaryAnalysisCompleted)
        .append(principalInvestigator)
        .append(startDate)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ProjectOverview other = (ProjectOverview) obj;
    return new EqualsBuilder()
        .append(allLibrariesQcPassed, other.allLibrariesQcPassed)
        .append(allPoolsConstructed, other.allPoolsConstructed)
        .append(allRunsCompleted, other.allRunsCompleted)
        .append(allSampleQcPassed, other.allSampleQcPassed)
        .append(endDate, other.endDate)
        .append(libraryPreparationComplete, other.libraryPreparationComplete)
        .append(locked, other.locked)
        .append(numProposedSamples, other.numProposedSamples)
        .append(primaryAnalysisCompleted, other.primaryAnalysisCompleted)
        .append(principalInvestigator, other.principalInvestigator)
        .append(startDate, other.startDate)
        .isEquals();
  }
}
