/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK MISO project contacts: Robert Davey @
 * TGAC *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MISO. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.w3c.dom.Document;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.StudyChangeLog;

/**
 * Concrete implementation of a Study
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "Study")
public class StudyImpl implements Study {
  private static final long UNSAVED_ID = 0L;

  private static final long serialVersionUID = 1L;

  @ManyToOne(targetEntity = ProjectImpl.class)
  @JoinColumn(name = "project_projectId")
  private Project project = null;

  @OneToMany(targetEntity = Experiment.class, mappedBy = "study")
  private Collection<Experiment> experiments = new HashSet<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long studyId = StudyImpl.UNSAVED_ID;

  @Transient
  public transient Document submissionDocument;

  @Column(name = "name", nullable = false)
  private String name;
  @Column(name = "description")
  private String description;
  @Column(name = "accession")
  private String accession;
  @ManyToOne
  @JoinColumn(name = "studyTypeId")
  private StudyType studyType;
  @Column(name = "alias")
  private String alias;
  @OneToMany(targetEntity = StudyChangeLog.class, mappedBy = "study", cascade = {CascadeType.REMOVE})
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  @Column(name = "created", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationTime;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier")
  private User lastModifier;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  /**
   * Construct a new Study with a default empty SecurityProfile
   */
  public StudyImpl() {}

  @Override
  public void addExperiment(Experiment e) {
    this.experiments.add(e);
  }

  @Override
  public int compareTo(Study t) {
    if (getId() < t.getId())
      return -1;
    if (getId() > t.getId())
      return 1;
    return 0;
  }

  /**
   * Equivalency is based on getProjectId() if set, otherwise on name, description and creation date.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof Study))
      return false;
    Study them = (Study) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == StudyImpl.UNSAVED_ID || them.getId() == StudyImpl.UNSAVED_ID) {
      if (getName() != null && them.getName() != null) {
        return getName().equals(them.getName());
      } else {
        return getAlias().equals(them.getAlias());
      }
    } else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public String getAccession() {
    return accession;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Collection<Experiment> getExperiments() {
    return experiments;
  }

  @Override
  public long getId() {
    return studyId;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Project getProject() {
    return project;
  }

  @Override
  public StudyType getStudyType() {
    return studyType;
  }

  @Override
  public int hashCode() {
    if (this.getId() != StudyImpl.UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = 1;
      if (getName() != null)
        hashcode = PRIME * hashcode + getName().hashCode();
      if (getAlias() != null)
        hashcode = PRIME * hashcode + getAlias().hashCode();
      return hashcode;
    }
  }

  @Override
  public void setAccession(String accession) {
    this.accession = accession;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setExperiments(Collection<Experiment> experiments) {
    this.experiments = experiments;
  }

  @Override
  public void setId(long id) {
    this.studyId = id;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setProject(Project project) {
    this.project = project;
  }

  @Override
  public void setStudyType(StudyType studyType) {
    this.studyType = studyType;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getName());
    sb.append(" : ");
    sb.append(getAlias());
    sb.append(" : ");
    sb.append(getDescription());
    sb.append(" : ");
    sb.append(getStudyType());
    sb.append(" : ");

    if (getProject() != null) {
      sb.append(getProject().getTitle());
      sb.append("(" + getProject().getName() + ")");
    }
    return sb.toString();
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    StudyChangeLog changeLog = new StudyChangeLog();
    changeLog.setStudy(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }

  @Override
  public String getDeleteType() {
    return "Study";
  }

  @Override
  public String getDeleteDescription() {
    Project p = getProject();
    return (p.getCode() == null ? p.getTitle() : p.getCode())
        + " " + getName() + " (" + getAlias() + ")";
  }

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreator(User creator) {
    this.creator = creator;
  }

  @Override
  public Date getCreationTime() {
    return creationTime;
  }

  @Override
  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  public Date getLastModified() {
    return lastModified;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

}
