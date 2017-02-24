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

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;

@Entity
@Table(name = "DetailedQcStatus", uniqueConstraints = @UniqueConstraint(columnNames = { "status", "description" }))
public class DetailedQcStatusImpl implements DetailedQcStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long detailedQcStatusId;

  @Column
  private Boolean status;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private Boolean noteRequired;

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
    return detailedQcStatusId;
  }

  @Override
  public void setId(Long detailedQcStatusId) {
    this.detailedQcStatusId = detailedQcStatusId;
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
  public Boolean getStatus() {
    return status;
  }

  @Override
  public void setStatus(Boolean status) {
    this.status = status;
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
  public Boolean getNoteRequired() {
    return noteRequired;
  }

  @Override
  public void setNoteRequired(Boolean noteRequired) {
    this.noteRequired = noteRequired;
  }

  @Override
  public String toString() {
    return "DetailedQcStatusImpl [detailedQcStatusId=" + detailedQcStatusId + ", status=" + status + ", description=" + description
        + ", noteRequired=" + noteRequired + ", createdBy=" + createdBy + ", creationDate=" + creationDate + ", updatedBy=" + updatedBy 
        + ", lastUpdated=" + lastUpdated + "]"; 
  }
}