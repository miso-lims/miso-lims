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
import java.util.stream.Collectors;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;

/**
 * uk.ac.bbsrc.tgac.miso.core.data
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "ProjectOverview")
public class ProjectOverview implements Nameable, Serializable {

  protected static final Logger log = LoggerFactory.getLogger(ProjectOverview.class);

  private static final String NAME_PREFIX = "POV";

  private static final long serialVersionUID = 1L;

  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long overviewId = ProjectOverview.UNSAVED_ID;

  @Column(name = "allLibraryQcPassed", nullable = false)
  private boolean allLibrariesQcPassed;
  @Column(nullable = false)
  private boolean allPoolsConstructed;
  @Column(nullable = false)
  private boolean allRunsCompleted;
  @Column(nullable = false)
  private boolean locked;
  @Column(nullable = false)
  private boolean primaryAnalysisCompleted;
  @Column(nullable = false)
  private String principalInvestigator;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  @Temporal(TemporalType.DATE)
  private Date startDate;

  @Temporal(TemporalType.DATE)
  private Date endDate;
  private Integer numProposedSamples;
  private boolean allSampleQcPassed;

  @Transient
  private boolean libraryPreparationComplete;

  @OneToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "ProjectOverview_Note", joinColumns = {
      @JoinColumn(name = "overview_overviewId") }, inverseJoinColumns = {
          @JoinColumn(name = "notes_noteId") })
  private Collection<Note> notes = new HashSet<>();

  @ManyToOne(targetEntity = ProjectImpl.class)
  @JoinColumn(name = "project_projectId")
  private Project project;

  @ManyToMany(targetEntity = SampleImpl.class)
  @JoinTable(name = "ProjectOverview_Sample", joinColumns = { @JoinColumn(name = "projectOverview_overviewId") }, inverseJoinColumns = {
      @JoinColumn(name = "sample_sampleId") })
  private Set<Sample> sampleGroup;

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

  public void setAllLibrariesQcPassed(boolean allLibrariesQcPassed) {
    this.allLibrariesQcPassed = allLibrariesQcPassed;
  }

  public void setAllPoolsConstructed(boolean allPoolsConstructed) {
    this.allPoolsConstructed = allPoolsConstructed;
  }

  public void setAllRunsCompleted(boolean allRunsCompleted) {
    this.allRunsCompleted = allRunsCompleted;
  }

  public void setAllSampleQcPassed(boolean allSampleQcPassed) {
    this.allSampleQcPassed = allSampleQcPassed;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  @Override
  public void setId(long overviewId) {
    this.overviewId = overviewId;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public void setLibraryPreparationComplete(boolean libraryPreparationComplete) {
    this.libraryPreparationComplete = libraryPreparationComplete;
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

  public void setPrimaryAnalysisCompleted(boolean primaryAnalysisCompleted) {
    this.primaryAnalysisCompleted = primaryAnalysisCompleted;
  }

  public void setPrincipalInvestigator(String principalInvestigator) {
    this.principalInvestigator = principalInvestigator;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setSampleGroup(Set<Sample> sampleGroup) {
    this.sampleGroup = sampleGroup;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
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
      sb.append(getSampleGroup().stream().map(Object::toString).collect(Collectors.joining(",")));
      sb.append(" ] ");
    }
    return sb.toString();
  }
}
