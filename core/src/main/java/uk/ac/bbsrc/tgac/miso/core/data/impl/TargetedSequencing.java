package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

@Entity
@Table(name = "TargetedSequencing")
public class TargetedSequencing implements Serializable {

  public static final TargetedSequencing NULL = new TargetedSequencing();

  static {
    NULL.setId(null);
    NULL.setAlias("None");
    NULL.setArchived(false);
  }

  private static final long serialVersionUID = 1L;
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long targetedSequencingId;

  @Column(nullable = false)
  private String alias;

  @Column(nullable = false)
  private String description;

  @ManyToMany(mappedBy = "targetedSequencing")
  @Fetch(FetchMode.SUBSELECT)
  private final Collection<KitDescriptor> kitDescriptors = new HashSet<>();

  @Column(nullable = false)
  private boolean archived;

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

  public Long getId() {
    return targetedSequencingId;
  }

  public void setId(Long targetedSequencingId) {
    this.targetedSequencingId = targetedSequencingId;
  }

  /**
   * Returns name displayed in UI and associated with targeted sequencing bed file.
   * 
   * @return alias of type String
   */
  public String getAlias() {
    return alias;
  }

  /**
   * Sets name displayed in UI and associated with targeted sequencing bed file.
   * 
   * @param alias of type String
   */
  public void setAlias(String alias) {
    this.alias = alias;
  }

  /**
   * Returns description used to make purpose of targeted sequencing clearer to user.
   * 
   * @return description of type String
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets description used to make purpose of targeted sequencing clearer to user.
   * 
   * @param description of type String
   */
  public void setDescription(String description) {
    this.description = description;
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

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  /**
   * Returns all Library Prep Kits that are associated with this TargetedSequencing.
   * 
   * @return Collection of KitDescriptor
   */
  public Collection<KitDescriptor> getKitDescriptors() {
    return kitDescriptors;
  }

  /**
   * Sets the Collection of Library Prep Kits associated with this TargetedSequeincing.
   * 
   * @param kitDescriptors Collection of KitDescriptor
   */
  public void setKitDescriptors(Collection<KitDescriptor> kitDescriptors) {
    this.kitDescriptors.clear();
    this.kitDescriptors.addAll(kitDescriptors);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((alias == null) ? 0 : alias.hashCode());
    result = prime * result + (archived ? 1231 : 1237);
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((targetedSequencingId == null) ? 0 : targetedSequencingId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TargetedSequencing other = (TargetedSequencing) obj;
    if (alias == null) {
      if (other.alias != null) return false;
    } else if (!alias.equals(other.alias)) return false;
    if (archived != other.archived) return false;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (targetedSequencingId == null) {
      if (other.targetedSequencingId != null) return false;
    } else if (!targetedSequencingId.equals(other.targetedSequencingId)) return false;
    return true;
  }

}
