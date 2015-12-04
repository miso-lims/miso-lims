package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractProject;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;

@Entity
@Table(name = "SampleNumberPerProject")
public class SampleNumberPerProjectImpl implements SampleNumberPerProject {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sampleNumberPerProjectId;

  @OneToOne(targetEntity = AbstractProject.class)
  @JoinColumn(name = "projectId", nullable = false, unique = true)
  private Project project;

  @Column(nullable = false)
  private Integer highestSampleNumber;

  @Column(nullable = false)
  private Integer padding;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  private Date creationDate;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
  private Date lastUpdated;

  @Override
  public Long getSampleNumberPerProjectId() {
    return sampleNumberPerProjectId;
  }

  @Override
  public void setSampleNumberPerProjectId(Long sampleNumberPerProjectId) {
    this.sampleNumberPerProjectId = sampleNumberPerProjectId;
  }

  @Override
  public User getCreatedBy() {
    return createdBy;
  }

  @Override
  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public User getUpdatedBy() {
    return updatedBy;
  }

  @Override
  public void setUpdatedBy(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Override
  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Override
  public Project getProject() {
    return project;
  }

  @Override
  public void setProject(Project project) {
    this.project = project;
  }

  @Override
  public Integer getHighestSampleNumber() {
    return highestSampleNumber;
  }

  @Override
  public void setHighestSampleNumber(Integer highestSampleNumber) {
    this.highestSampleNumber = highestSampleNumber;
  }

  @Override
  public Integer getPadding() {
    return padding;
  }

  @Override
  public void setPadding(Integer padding) {
    this.padding = padding;
  }

}
