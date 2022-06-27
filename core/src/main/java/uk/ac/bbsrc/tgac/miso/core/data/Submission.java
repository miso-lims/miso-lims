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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "Submission")
public class Submission implements Comparable<Submission>, Deletable, Serializable, Identifiable
{

  private static final long UNSAVED_ID = 0L;

  private static final long serialVersionUID = 1L;

  private String accession;
  private String alias;
  private boolean completed;

  @Temporal(TemporalType.DATE)
  private Date creationDate;

  private String description;

  @ManyToMany(targetEntity = Experiment.class)
  @JoinTable(name = "Submission_Experiment", joinColumns = {
      @JoinColumn(name = "submission_submissionId") }, inverseJoinColumns = {
          @JoinColumn(name = "experiments_experimentId") })
  private Set<Experiment> experiments = new HashSet<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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

  public String getAccession() {
    return accession;
  }

  public String getAlias() {
    return alias;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public String getDescription() {
    return description;
  }

  public Set<Experiment> getExperiments() {
    return experiments;
  }

  @Override
  public long getId() {
    return submissionId;
  }

  public Date getSubmissionDate() {
    return submittedDate;
  }

  public String getTitle() {
    return title;
  }

  public boolean isCompleted() {
    return completed;
  }

  public boolean isVerified() {
    return verified;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  public void setExperiments(Set<Experiment> experiments) {
    this.experiments = experiments;
  }

  @Override
  public void setId(long id) {
    this.submissionId = id;
  }

  public void setSubmissionDate(Date submissionDate) {
    this.submittedDate = submissionDate;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Submission";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }
}
