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
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

@Entity
@Table(name = "Subproject")
public class SubprojectImpl implements Subproject {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long subprojectId;

  @OneToOne(targetEntity = AbstractProject.class)
  @JoinColumn(name = "projectId", nullable = false)
  private Project parentProject;

  @Column(unique = true, nullable = false)
  private String alias;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private Boolean priority;

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
  public Long getSubprojectId() {
    return subprojectId;
  }

  @Override
  public void setSubprojectId(Long subprojectId) {
    this.subprojectId = subprojectId;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
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
  public Project getParentProject() {
    return parentProject;
  }

  @Override
  public void setParentProject(Project parentProject) {
    this.parentProject = parentProject;
  }

  @Override
  public Boolean getPriority() {
    return priority;
  }

  @Override
  public void setPriority(Boolean priority) {
    this.priority = priority;
  }

}
