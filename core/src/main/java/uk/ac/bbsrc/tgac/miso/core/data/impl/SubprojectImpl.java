package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

@Entity
@Table(name = "Subproject")
public class SubprojectImpl implements Subproject {

  private static final long serialVersionUID = 1L;
  private static final long UNSAVED_ID = 0L;

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

  @ManyToOne(targetEntity = ReferenceGenomeImpl.class)
  @JoinColumn(name = "referenceGenomeId")
  private ReferenceGenome referenceGenome;

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
  public User getCreator() {
    return createdBy;
  }

  @Override
  public void setCreator(User createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public Date getCreationTime() {
    return creationDate;
  }

  @Override
  public void setCreationTime(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public User getLastModifier() {
    return updatedBy;
  }

  @Override
  public void setLastModifier(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Override
  public Date getLastModified() {
    return lastUpdated;
  }

  @Override
  public void setLastModified(Date lastUpdated) {
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
  public ReferenceGenome getReferenceGenome() {
    return referenceGenome;
  }

  @Override
  public void setReferenceGenome(ReferenceGenome referenceGenome) {
    this.referenceGenome = referenceGenome;
  }

  @Override
  public String getDeleteType() {
    return "Subproject";
  }

  @Override
  public String getDeleteDescription() {
    Project p = getParentProject();
    return (p.getCode() == null ? p.getTitle() : p.getCode())
        + " - "
        + getAlias()
        + (getAlias().equals(getDescription()) ? "" : " (" + getDescription() + ")");
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }
}
