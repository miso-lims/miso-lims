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

import com.eaglegenomics.simlims.core.User;

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
    String label = getAlias() + " (" + getDescription() + ")";
    return (label.length() < 51 ? label : label.substring(0, 49) + "\u2026");
  }

  @Override
  public String toString() {
    return "TissueTypeImpl [tissueTypeId=" + tissueTypeId + ", alias=" + alias + ", description=" + description + ", createdBy=" + createdBy
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
