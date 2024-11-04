package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Date;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;

@Entity
@Table(name = "SampleNumberPerProject")
public class SampleNumberPerProjectImpl implements SampleNumberPerProject, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sampleNumberPerProjectId;

  @OneToOne(targetEntity = ProjectImpl.class, optional = false)
  @JoinColumn(name = "projectId", nullable = false, unique = true)
  private Project project;

  @Column(nullable = false)
  private Integer highestSampleNumber;

  @Column(nullable = false)
  private Integer padding;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  @Override
  public Long getId() {
    return sampleNumberPerProjectId;
  }

  @Override
  public void setId(Long sampleNumberPerProjectId) {
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

  @Override
  public String toString() {
    return "SampleNumberPerProjectImpl [sampleNumberPerProjectId=" + sampleNumberPerProjectId + ", project=" + project
        + ", highestSampleNumber=" + highestSampleNumber + ", padding=" + padding + ", createdBy=" + createdBy
        + ", creationDate="
        + creationDate + ", updatedBy=" + updatedBy + ", lastUpdated=" + lastUpdated + "]";
  }

}
