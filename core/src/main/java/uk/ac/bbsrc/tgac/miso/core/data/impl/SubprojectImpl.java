package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

@Entity
@Table(name = "Subproject")
public class SubprojectImpl implements Subproject {

  private static final long serialVersionUID = 1L;
  public static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long subprojectId = UNSAVED_ID;

  @ManyToOne(targetEntity = ProjectImpl.class)
  @JoinColumn(name = "projectId", nullable = false)
  private Project parentProject;

  @Column(unique = true, nullable = false)
  private String alias;

  @Column
  private String description;

  @Column(nullable = false)
  private Boolean priority;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  @Column(nullable = true)
  private Long referenceGenomeId;

  @Override
  public long getId() {
    return subprojectId;
  }

  @Override
  public void setId(long subprojectId) {
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

  @Override
  public Long getReferenceGenomeId() {
    return referenceGenomeId;
  }

  @Override
  public void setReferenceGenomeId(Long referenceGenomeId) {
    this.referenceGenomeId = referenceGenomeId;
  }

  @Override
  public String getDeleteType() {
    return "Subproject";
  }

  @Override
  public String getDeleteDescription() {
    Project p = getParentProject();
    return (p.getShortName() == null ? p.getAlias() : p.getShortName())
        + " - "
        + getAlias()
        + (getAlias().equals(getDescription()) ? "" : " (" + getDescription() + ")");
  }

  @Override
  public SecurityProfile getDeletionSecurityProfile() {
    return null;
  }
}
