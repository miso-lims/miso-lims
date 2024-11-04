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
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;

@Entity
@Table(name = "TissueType")
public class TissueTypeImpl implements TissueType {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long tissueTypeId = UNSAVED_ID;

  @Column(unique = true, nullable = false)
  private String alias;

  @Column(nullable = false)
  private String description;

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
    return tissueTypeId;
  }

  @Override
  public void setId(long tissueTypeId) {
    this.tissueTypeId = tissueTypeId;
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
  public void setCreator(User creator) {
    this.createdBy = creator;
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

  /**
   * Get custom label for dropdown options
   */
  @Override
  public String getItemLabel() {
    return getAlias() + " (" + getDescription() + ")";
  }

  @Override
  public String toString() {
    return "TissueTypeImpl [tissueTypeId=" + tissueTypeId + ", alias=" + alias + ", description=" + description
        + ", createdBy=" + createdBy
        + ", creationDate=" + creationDate + ", updatedBy=" + updatedBy + ", lastUpdated=" + lastUpdated + "]";
  }

  @Override
  public String getDeleteType() {
    return "Tissue Type";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias() + " (" + getDescription() + ")";
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

}
