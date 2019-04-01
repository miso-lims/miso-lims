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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

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

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
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
  public static final Long UNSAVED_ID = 0L;

  @Column(updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate = new Date();
  private String description = "";
  private String name = "";
  private String alias = "";
  private String shortName;

  @OneToMany(targetEntity = FileAttachment.class)
  @JoinTable(name = "Project_Attachment", joinColumns = { @JoinColumn(name = "projectId") }, inverseJoinColumns = {
      @JoinColumn(name = "attachmentId") })
  private List<FileAttachment> attachments;

  @Transient
  private List<FileAttachment> pendingAttachmentDeletions;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long projectId = UNSAVED_ID;

  @OneToMany(targetEntity = SampleImpl.class, fetch = FetchType.LAZY, mappedBy = "project")
  private Collection<Sample> samples = new HashSet<>();

  @OneToMany(targetEntity = StudyImpl.class, fetch = FetchType.LAZY, mappedBy = "project")
  private Collection<Study> studies = new HashSet<>();


  @Enumerated(EnumType.STRING)
  private ProgressType progress;

  @ManyToOne(targetEntity = ReferenceGenomeImpl.class)
  @JoinColumn(name = "referenceGenomeId", referencedColumnName = "referenceGenomeId", nullable = false)
  private ReferenceGenome referenceGenome;

  @ManyToOne(targetEntity = TargetedSequencing.class)
  @JoinColumn(name = "targetedSequencingId", referencedColumnName = "targetedSequencingId", nullable = true)
  private TargetedSequencing defaultTargetedSequencing;


  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  /**
   * Construct a new Project
   */
  public ProjectImpl() {
  }

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

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
    return projectId;
  }

  @Override
  public void setId(long id) {
    this.projectId = id;
  }

  @Override
  public Collection<Sample> getSamples() {
    return samples;
  }

  @Override
  public Collection<Study> getStudies() {
    return studies;
  }

  @Override
  public void setCreationDate(Date date) {
    this.creationDate = date;
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
  public void setSamples(Collection<Sample> samples) {
    this.samples = samples;
    Collections.sort(Lists.newArrayList(this.samples), new AliasComparator<Sample>());
  }

  @Override
  public void addSample(Sample sample) {
    this.samples.add(sample);
    Collections.sort(Lists.newArrayList(this.samples), new AliasComparator<Sample>());
  }

  @Override
  public void setStudies(Collection<Study> studies) {
    this.studies = studies;
    Collections.sort(Lists.newArrayList(this.studies), new AliasComparator<Study>());
  }

  @Override
  public ProgressType getProgress() {
    return progress;
  }

  @Override
  public void setProgress(ProgressType progress) {
    this.progress = progress;
  }

  @Override
  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
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
    sb.append(getCreationDate());
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
        .append(progress)
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
        .append(progress, other.progress)
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

}
