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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
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
@Entity
@Table(name = "ProjectOverview")
public class ProjectOverview implements Watchable, Alertable, Nameable, Serializable {

  protected static final Logger log = LoggerFactory.getLogger(ProjectOverview.class);

  private static final String NAME_PREFIX = "POV";

  private static final long serialVersionUID = 1L;

  public static final Long UNSAVED_ID = 0L;

  @Column(name = "allLibraryQcPassed")
  private boolean allLibrariesQcPassed;

  private boolean allPoolsConstructed;

  private boolean allRunsCompleted;

  private boolean allSampleQcPassed;

  private Date endDate;

  private Date lastUpdated;
  private boolean libraryPreparationComplete;
  @Transient
  private final Set<MisoListener> listeners = new HashSet<>();
  private boolean locked;
  @OneToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "ProjectOverview_Note", joinColumns = {
      @JoinColumn(name = "overview_overviewId") }, inverseJoinColumns = {
          @JoinColumn(name = "notes_noteId") })
  private Collection<Note> notes = new HashSet<>();
  private Integer numProposedSamples;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long overviewId = ProjectOverview.UNSAVED_ID;
  private boolean primaryAnalysisCompleted;
  private String principalInvestigator;
  @ManyToOne(targetEntity = ProjectImpl.class)
  @JoinColumn(name = "project_projectId")
  private Project project;

  @ManyToMany(targetEntity = SampleImpl.class)
  @JoinTable(name = "ProjectOverview_Sample", joinColumns = { @JoinColumn(name = "projectOverview_overviewId") }, inverseJoinColumns = {
      @JoinColumn(name = "sample_sampleId") })
  private Set<Sample> sampleGroup;

  private Date startDate;

  @Transient
  // not Hibernate-managed
  private Group watchGroup;

  @ManyToMany(targetEntity = UserImpl.class)
  @JoinTable(name = "ProjectOverview_Watcher", joinColumns = { @JoinColumn(name = "overviewId") }, inverseJoinColumns = {
      @JoinColumn(name = "userId") })
  private Set<User> watchUsers = new HashSet<>();

  @Override
  public boolean addListener(MisoListener listener) {
    return listeners.add(listener);
  }

  @Override
  public void addWatcher(User user) {
    watchUsers.add(user);
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

  protected void firePrimaryAnalysisCompletedEvent() {
    if (this.getId() != 0L) {
      ProjectOverviewEvent poe = new ProjectOverviewEvent(this, MisoEventType.PRIMARY_ANALYSIS_COMPLETED,
          this.getProject().getAlias() + " : primary analysis has completed.");
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

  protected void fireSampleQcPassedEvent() {
    if (this.getId() != 0L) {
      ProjectOverviewEvent poe = new ProjectOverviewEvent(this, MisoEventType.ALL_SAMPLES_QC_PASSED,
          this.getProject().getAlias() + " : all project samples have passed QC.");
      for (MisoListener listener : getListeners()) {
        listener.stateChanged(poe);
      }
    }
  }

  public boolean getAllLibrariesQcPassed() {
    return allLibrariesQcPassed;
  }

  public boolean getAllPoolsConstructed() {
    return allPoolsConstructed;
  }

  public boolean getAllRunsCompleted() {
    return allRunsCompleted;
  }

  public boolean getAllSampleQcPassed() {
    return allSampleQcPassed;
  }

  public Date getEndDate() {
    return endDate;
  }

  @Override
  public long getId() {
    return overviewId;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public boolean getLibraryPreparationComplete() {
    return libraryPreparationComplete;
  }

  @Override
  public Set<MisoListener> getListeners() {
    return this.listeners;
  }

  public boolean getLocked() {
    return locked;
  }

  @Override
  public String getName() {
    return NAME_PREFIX + getId();
  }

  public Collection<Note> getNotes() {
    return notes;
  }

  public Integer getNumProposedSamples() {
    return numProposedSamples;
  }

  @Deprecated
  public Long getOverviewId() {
    return overviewId;
  }

  public boolean getPrimaryAnalysisCompleted() {
    return primaryAnalysisCompleted;
  }

  public String getPrincipalInvestigator() {
    return principalInvestigator;
  }

  public Project getProject() {
    return project;
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

  public Set<Sample> getSampleGroup() {
    return sampleGroup;
  }

  public Set<Sample> getSamples() {
    return sampleGroup;
  }

  public Date getStartDate() {
    return startDate;
  }

  @Override
  public String getWatchableIdentifier() {
    return getName();
  }

  @Override
  public Set<User> getWatchers() {
    Set<User> allWatchers = new HashSet<>();
    if (watchGroup != null) allWatchers.addAll(watchGroup.getUsers());
    if (watchUsers != null) allWatchers.addAll(watchUsers);
    return allWatchers;
  }

  public Group getWatchGroup() {
    return watchGroup;
  }

  public Set<User> getWatchUsers() {
    return watchUsers;
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

  public boolean isDeletable() {
    return getId() != ProjectOverview.UNSAVED_ID;
  }

  @Override
  public boolean removeListener(MisoListener listener) {
    return listeners.remove(listener);
  }

  @Override
  public void removeWatcher(User user) {
    watchUsers.remove(user);
  }

  public void setAllLibrariesQcPassed(boolean allLibrariesQcPassed) {
    if (this.allLibrariesQcPassed != allLibrariesQcPassed && allLibrariesQcPassed) {
      this.allLibrariesQcPassed = allLibrariesQcPassed;
      fireLibraryQcPassedEvent();
    } else {
      this.allLibrariesQcPassed = allLibrariesQcPassed;
    }
  }

  public void setAllPoolsConstructed(boolean allPoolsConstructed) {
    if (this.allPoolsConstructed != allPoolsConstructed && allPoolsConstructed) {
      this.allPoolsConstructed = allPoolsConstructed;
      firePoolsConstructedEvent();
    } else {
      this.allPoolsConstructed = allPoolsConstructed;
    }
  }

  public void setAllRunsCompleted(boolean allRunsCompleted) {
    if (this.allRunsCompleted != allRunsCompleted && allRunsCompleted) {
      this.allRunsCompleted = allRunsCompleted;
      fireRunsCompletedEvent();
    } else {
      this.allRunsCompleted = allRunsCompleted;
    }
  }

  public void setAllSampleQcPassed(boolean allSampleQcPassed) {
    if (this.allSampleQcPassed != allSampleQcPassed && allSampleQcPassed) {
      this.allSampleQcPassed = allSampleQcPassed;
      fireSampleQcPassedEvent();
    } else {
      this.allSampleQcPassed = allSampleQcPassed;
    }
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public void setId(Long overviewId) {
    this.overviewId = overviewId;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public void setLibraryPreparationComplete(boolean libraryPreparationComplete) {
    if (this.libraryPreparationComplete != libraryPreparationComplete && libraryPreparationComplete) {
      this.libraryPreparationComplete = libraryPreparationComplete;
      fireLibraryPreparationCompleteEvent();
    } else {
      this.libraryPreparationComplete = libraryPreparationComplete;
    }
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

  public void setNumProposedSamples(Integer numProposedSamples) {
    this.numProposedSamples = numProposedSamples;
  }

  @Deprecated
  public void setOverviewId(Long overviewId) {
    this.overviewId = overviewId;
  }

  public void setPrimaryAnalysisCompleted(boolean primaryAnalysisCompleted) {
    if (this.primaryAnalysisCompleted != primaryAnalysisCompleted && primaryAnalysisCompleted) {
      this.primaryAnalysisCompleted = primaryAnalysisCompleted;
      firePrimaryAnalysisCompletedEvent();
    } else {
      this.primaryAnalysisCompleted = primaryAnalysisCompleted;
    }
  }

  public void setPrincipalInvestigator(String principalInvestigator) {
    this.principalInvestigator = principalInvestigator;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setSampleGroup(Set<Sample> sampleGroup) {
  }

  @Deprecated
  public void setSamples(Set<Sample> samples) {
    sampleGroup = samples;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public void setWatchGroup(Group watchGroup) {
    this.watchGroup = watchGroup;
  }

  public void setWatchUsers(Set<User> watchUsers) {
    this.watchUsers = watchUsers;
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
}
