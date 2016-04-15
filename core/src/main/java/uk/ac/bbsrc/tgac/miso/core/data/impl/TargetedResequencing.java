package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

public class TargetedResequencing {

  public static final Long UNSAVED_ID = 0L;

  private Long targetedResequencingId;

  /** Name displayed in UI and associated with targeted resequencing bed file. */
  private String alias;

  /** Description used to make purpose of targeted resequencing clearer to user. */
  private String description;

  /** The kit chosen will restrict the targeted resequencing that is available. */
  private KitDescriptor kitDescriptor;

  private User createdBy;

  private Date creationDate;

  private User updatedBy;

  private Date lastUpdated;

  public Long getTargetedResequencingId() {
    return targetedResequencingId;
  }

  public void setTargetedResequencingId(Long targetedResequencingId) {
    this.targetedResequencingId = targetedResequencingId;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public KitDescriptor getKitDescriptor() {
    return kitDescriptor;
  }

  public void setKitDescriptor(KitDescriptor kitDescriptor) {
    this.kitDescriptor = kitDescriptor;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public User getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

}
