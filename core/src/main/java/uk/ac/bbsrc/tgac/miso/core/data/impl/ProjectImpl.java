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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.ProjectChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;

/**
 * Concrete implementation of a Project, inheriting from the simlims core Project
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "Project")
public class ProjectImpl implements Project {

  private static final long serialVersionUID = 1L;

  /**
   * Use this ID to indicate that a project has not yet been saved, and therefore does not yet have a unique ID.
   */
  private static final long UNSAVED_ID = 0L;

  private String description = "";
  private String name = "";
  private String alias = "";
  private String shortName;
  private String rebNumber;

  @Temporal(TemporalType.DATE)
  private Date rebExpiry;

  private Integer samplesExpected;

  @ManyToOne
  @JoinColumn(name = "contactId")
  private Contact contact;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  @Column(name = "created", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationTime;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @OneToMany(targetEntity = ProjectChangeLog.class, mappedBy = "project", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @OneToMany(targetEntity = FileAttachment.class)
  @JoinTable(name = "Project_Attachment", joinColumns = { @JoinColumn(name = "projectId") }, inverseJoinColumns = {
      @JoinColumn(name = "attachmentId") })
  private List<FileAttachment> attachments;

  @Transient
  private List<FileAttachment> pendingAttachmentDeletions;

  @Id
  @Column(name = "projectId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id = UNSAVED_ID;

  @OneToMany(targetEntity = StudyImpl.class, fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.REMOVE)
  private Collection<Study> studies = new HashSet<>();

  @Enumerated(EnumType.STRING)
  private StatusType status;

  @ManyToOne(targetEntity = ReferenceGenomeImpl.class)
  @JoinColumn(name = "referenceGenomeId", referencedColumnName = "referenceGenomeId", nullable = false)
  private ReferenceGenome referenceGenome;

  @ManyToOne(targetEntity = TargetedSequencing.class)
  @JoinColumn(name = "targetedSequencingId", referencedColumnName = "targetedSequencingId", nullable = true)
  private TargetedSequencing defaultTargetedSequencing;

  @ManyToOne
  @JoinColumn(name = "pipelineId")
  private Pipeline pipeline;

  private boolean secondaryNaming;

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public String getShortName() {
    return shortName;
  }

  @Override
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  @Override
  public Collection<Study> getStudies() {
    return studies;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public void setStudies(Collection<Study> studies) {
    this.studies = studies;
    Collections.sort(Lists.newArrayList(this.studies), new AliasComparator<Study>());
  }

  @Override
  public StatusType getStatus() {
    return status;
  }

  @Override
  public void setStatus(StatusType status) {
    this.status = status;
  }

  public void addStudy(Study s) {
    // do study validation
    s.setProject(this);
    // add
    this.studies.add(s);
  }

  @Override
  public List<FileAttachment> getAttachments() {
    return attachments;
  }

  @Override
  public void setAttachments(List<FileAttachment> attachments) {
    this.attachments = attachments;
  }

  @Override
  public String getAttachmentsTarget() {
    return "project";
  }

  @Override
  public List<FileAttachment> getPendingAttachmentDeletions() {
    return pendingAttachmentDeletions;
  }

  @Override
  public void setPendingAttachmentDeletions(List<FileAttachment> pendingAttachmentDeletions) {
    this.pendingAttachmentDeletions = pendingAttachmentDeletions;
  }

  @Override
  public int compareTo(Project o) {
    if (getId() != 0L && o.getId() != 0L) {
      if (getId() < o.getId()) return -1;
      if (getId() > o.getId()) return 1;
    } else if (getAlias() != null && o.getAlias() != null) {
      return getAlias().compareTo(o.getAlias());
    }
    return 0;
  }

  /**
   * Format is "Date : Name : Description".
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getCreationTime());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getDescription());
    return sb.toString();
  }

  @Override
  public ReferenceGenome getReferenceGenome() {
    return referenceGenome;
  }

  @Override
  public void setReferenceGenome(ReferenceGenome referenceGenome) {
    this.referenceGenome = referenceGenome;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(5, 35)
        .append(alias)
        .append(description)
        .append(status)
        .append(referenceGenome)
        .append(shortName)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ProjectImpl other = (ProjectImpl) obj;
    return new EqualsBuilder()
        .append(alias, other.alias)
        .append(description, other.description)
        .append(status, other.status)
        .append(referenceGenome, other.referenceGenome)
        .append(shortName, other.shortName)
        .isEquals();
  }

  @Override
  public TargetedSequencing getDefaultTargetedSequencing() {
    return defaultTargetedSequencing;
  }

  @Override
  public void setDefaultTargetedSequencing(TargetedSequencing targetedSequencing) {
    this.defaultTargetedSequencing = targetedSequencing;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    ProjectChangeLog change = new ProjectChangeLog();
    change.setProject(this);
    change.setSummary(summary);
    change.setColumnsChanged(columnsChanged);
    change.setUser(user);
    return change;
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
  public String getDeleteType() {
    return "Project";
  }

  @Override
  public String getDeleteDescription() {
    if (getAlias() == null) {
      return getShortName();
    } else if (getShortName() == null) {
      return getAlias();
    } else {
      return getAlias() + " (" + getShortName() + ")";
    }
  }

  @Override
  public Pipeline getPipeline() {
    return pipeline;
  }

  @Override
  public void setPipeline(Pipeline pipeline) {
    this.pipeline = pipeline;
  }

  @Override
  public boolean isSecondaryNaming() {
    return secondaryNaming;
  }

  @Override
  public void setSecondaryNaming(boolean secondaryNaming) {
    this.secondaryNaming = secondaryNaming;
  }

  @Override
  public String getRebNumber() {
    return rebNumber;
  }

  @Override
  public void setRebNumber(String rebNumber) {
    this.rebNumber = rebNumber;
  }

  @Override
  public Date getRebExpiry() {
    return rebExpiry;
  }

  @Override
  public void setRebExpiry(Date rebExpiry) {
    this.rebExpiry = rebExpiry;
  }

  @Override
  public Integer getSamplesExpected() {
    return samplesExpected;
  }

  @Override
  public void setSamplesExpected(Integer samplesExpected) {
    this.samplesExpected = samplesExpected;
  }

  @Override
  public Contact getContact() {
    return contact;
  }

  @Override
  public void setContact(Contact contact) {
    this.contact = contact;
  }

}
