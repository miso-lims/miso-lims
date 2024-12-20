package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;
import java.util.Objects;

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
import jakarta.persistence.UniqueConstraint;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "DetailedQcStatus", uniqueConstraints = @UniqueConstraint(columnNames = {"status", "description"}))
public class DetailedQcStatusImpl implements DetailedQcStatus {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long detailedQcStatusId = UNSAVED_ID;

  @Column(nullable = true)
  private Boolean status;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private Boolean noteRequired;

  @Column(nullable = false)
  private Boolean archived;

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

  @Override
  public long getId() {
    return detailedQcStatusId;
  }

  @Override
  public void setId(long detailedQcStatusId) {
    this.detailedQcStatusId = detailedQcStatusId;
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
  public void setCreationTime(Date creationTime) {
    this.creationDate = creationTime;
  }

  @Override
  public User getLastModifier() {
    return updatedBy;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.updatedBy = lastModifier;
  }

  @Override
  public Date getLastModified() {
    return lastUpdated;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastUpdated = lastModified;
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
  public boolean getArchived() {
    return archived;
  }

  @Override
  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  @Override
  public String toString() {
    return "DetailedQcStatusImpl [detailedQcStatusId=" + detailedQcStatusId + ", status=" + status + ", description="
        + description
        + ", noteRequired=" + noteRequired + ", createdBy=" + createdBy + ", creationDate=" + creationDate
        + ", updatedBy=" + updatedBy
        + ", lastUpdated=" + lastUpdated + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, description, noteRequired);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        DetailedQcStatusImpl::getStatus,
        DetailedQcStatusImpl::getDescription,
        DetailedQcStatusImpl::getNoteRequired);
  }

  @Override
  public String getDeleteType() {
    return "Detailed QC Status";
  }

  @Override
  public String getDeleteDescription() {
    return getDescription();
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }
}
