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

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;

/**
 * Concrete implementation of a Submission that
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "Submission")
public class SubmissionImpl implements Submission {

  private static final long serialVersionUID = 1L;

  protected static final Logger log = LoggerFactory.getLogger(SubmissionImpl.class);

  public static final Long UNSAVED_ID = 0L;

  private String accession;
  private String alias;
  private boolean completed;

  @Temporal(TemporalType.DATE)
  private Date creationDate;

  private String description;

  @ManyToMany(targetEntity = PartitionImpl.class)
  @JoinTable(name = "Submission_Partition_Dilution", joinColumns = { @JoinColumn(name = "submission_submissionId") }, inverseJoinColumns = {
      @JoinColumn(name = "partition_partitionId") })
  @MapKeyJoinColumn(name = "dilution_dilutionId")
  @MapKeyClass(LibraryDilution.class)
  private Map<LibraryDilution, Partition> dilutions;

  @ManyToMany(targetEntity = ExperimentImpl.class)
  @JoinTable(name = "Submission_Experiment", joinColumns = {
      @JoinColumn(name = "submission_submissionId") }, inverseJoinColumns = {
          @JoinColumn(name = "experiments_experimentId") })
  private Set<Experiment> experiments;

  private String name;

  @ManyToMany(targetEntity = SampleImpl.class)
  @JoinTable(name = "Submission_Sample", joinColumns = { @JoinColumn(name = "submission_submissionId") }, inverseJoinColumns = {
      @JoinColumn(name = "samples_sampleId") })
  private Set<Sample> samples;

  @ManyToMany(targetEntity = StudyImpl.class)
  @JoinTable(name = "Submission_Study", joinColumns = { @JoinColumn(name = "submission_submissionId") }, inverseJoinColumns = {
      @JoinColumn(name = "studies_studyId") })
  private Set<Study> studies;

  @Transient
  private SubmissionActionType submissionActionType;

  @Id
  private long submissionId = UNSAVED_ID;

  @Temporal(TemporalType.DATE)
  private Date submittedDate;

  private String title;
  private boolean verified;

  @Override
  public int compareTo(Submission t) {
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
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
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Map<LibraryDilution, Partition> getDilutions() {
    return dilutions;
  }

  @Override
  public Set<Experiment> getExperiments() {
    return experiments;
  }

  @Override
  public long getId() {
    return submissionId;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Set<Sample> getSamples() {
    return samples;
  }

  @Override
  public Set<Study> getStudies() {
    return studies;
  }

  @Override
  public SubmissionActionType getSubmissionActionType() {
    return submissionActionType;
  }

  @Override
  public Date getSubmissionDate() {
    return submittedDate;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public boolean isCompleted() {
    return completed;
  }

  @Override
  public boolean isVerified() {
    return verified;
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
  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  @Override
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setDilutions(Map<LibraryDilution, Partition> dilutions) {
    this.dilutions = dilutions;
  }

  @Override
  public void setExperiments(Set<Experiment> experiments) {
    this.experiments = experiments;
  }

  @Override
  public void setId(long id) {
    this.submissionId = id;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setSamples(Set<Sample> samples) {
    this.samples = samples;
  }

  @Override
  public void setStudies(Set<Study> studies) {
    this.studies = studies;
  }

  @Override
  public void setSubmissionActionType(SubmissionActionType submissionActionType) {
    this.submissionActionType = submissionActionType;
  }

  @Override
  public void setSubmissionDate(Date submissionDate) {
    this.submittedDate = submissionDate;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public void setVerified(boolean verified) {
    this.verified = verified;
  }
}
