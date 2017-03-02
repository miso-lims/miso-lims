package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

@Entity
@Table(name = "TargetedSequencing")
public class TargetedSequencing {

  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long targetedSequencingId;

  @Column(nullable = false)
  private String alias;

  @Column(nullable = false)
  private String description;

  @ManyToMany(targetEntity = KitDescriptor.class, cascade = CascadeType.ALL)
  @JoinTable(name = "TargetedSequencing_KitDescriptor", joinColumns = {
      @JoinColumn(name = "targetedSequencingId") }, inverseJoinColumns = {
          @JoinColumn(name = "kitDescriptorId") })
  private final Collection<KitDescriptor> kitDescriptors = new HashSet<>();

  @Column(nullable = false)
  private boolean archived;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @ManyToOne(targetEntity = UserImpl.class)
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

  public Collection<KitDescriptor> getKitDescriptors() {
    return kitDescriptors;
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

}
