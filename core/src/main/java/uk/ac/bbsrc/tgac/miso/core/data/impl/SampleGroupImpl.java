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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

@Entity
@Table(name = "SampleGroup", uniqueConstraints = @UniqueConstraint(columnNames = { "projectId", "groupId" }) )
public class SampleGroupImpl implements SampleGroupId {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sampleGroupId;

  @OneToOne(targetEntity = ProjectImpl.class)
  @JoinColumn(name = "projectId", nullable = false)
  private Project project;

  @OneToOne(targetEntity = SubprojectImpl.class)
  @JoinColumn(name = "subprojectId")
  private Subproject subproject;

  @Column(nullable = false)
  private Integer groupId;

  @Column(nullable = false)
  private String description;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  @Override
  public Long getId() {
    return sampleGroupId;
  }

  @Override
  public void setId(Long sampleGroupId) {
    this.sampleGroupId = sampleGroupId;
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
  public Subproject getSubproject() {
    return subproject;
  }

  @Override
  public void setSubproject(Subproject subproject) {
    if (subproject != null && subproject.getParentProject().getId() != project.getId()) {
      throw new IllegalArgumentException("Subproject does not match current project");
    }
    this.subproject = subproject;
  }

  @Override
  public Integer getGroupId() {
    return groupId;
  }

  @Override
  public void setGroupId(Integer groupId) {
    this.groupId = groupId;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
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
  public String toString() {
    return "SampleGroupImpl [sampleGroupId=" + sampleGroupId + ", projectId=" + project + ", subprojectId=" + subproject + " groupId="
        + groupId + ", description=" + description + ", createdBy=" + createdBy + ", creationDate=" + creationDate + ", updatedBy="
        + updatedBy + ", lastUpdated=" + lastUpdated + "]";
  }

}
