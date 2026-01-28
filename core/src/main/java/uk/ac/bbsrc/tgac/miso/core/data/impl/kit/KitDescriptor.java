package uk.ac.bbsrc.tgac.miso.core.data.impl.kit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.KitDescriptorChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * A KitDescriptor handles information about a consumable element that is generally typed by a name,
 * manufacturer and part number. Kits use KitDescriptors, coupled with a lot number, to represent a
 * real-world manifestation of a consumable kit.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "KitDescriptor")
public class KitDescriptor implements Serializable, ChangeLoggable, Deletable {

  public static int sortByName(KitDescriptor a, KitDescriptor b) {
    return a.getName().compareTo(b.getName());
  }

  private static final long serialVersionUID = 1L;

  /** Field UNSAVED_ID */
  private static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long kitDescriptorId = KitDescriptor.UNSAVED_ID;
  private String name;
  private Integer version;
  private String manufacturer;
  private String partNumber;
  private Integer stockLevel;
  private String description;
  private boolean archived = false;

  @OneToMany(targetEntity = KitDescriptorChangeLog.class, mappedBy = "kitDescriptor", cascade = CascadeType.REMOVE)
  private Collection<ChangeLog> changelog = new ArrayList<>();

  @ManyToMany(targetEntity = TargetedSequencing.class)
  @JoinTable(name = "TargetedSequencing_KitDescriptor", inverseJoinColumns = {
      @JoinColumn(name = "targetedSequencingId")},
      joinColumns = {
          @JoinColumn(name = "kitDescriptorId")})
  private final Set<TargetedSequencing> targetedSequencing = new HashSet<>();

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  @Column(name = "created", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationTime;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @Enumerated(EnumType.STRING)
  private KitType kitType;

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  /**
   * Returns the kitDescriptorId of this KitDescriptor object.
   * 
   * @return Long kitDescriptorId.
   */
  @Override
  public long getId() {
    return kitDescriptorId;
  }

  /**
   * Sets the kitDescriptorId of this KitDescriptor object.
   * 
   * @param kitDescriptorId kitDescriptorId.
   */
  @Override
  public void setId(long kitDescriptorId) {
    this.kitDescriptorId = kitDescriptorId;
  }

  /**
   * Returns the name of this KitDescriptor object.
   * 
   * @return String name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this KitDescriptor object.
   * 
   * @param name name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the version of this KitDescriptor object.
   *
   * @return Integer version.
   */
  public Integer getVersion() {
    return version;
  }

  /**
   * Sets the version of this KitDescriptor object.
   * 
   * @param version version.
   */
  public void setVersion(Integer version) {
    this.version = version;
  }

  /**
   * Returns the manufacturer of this KitDescriptor object.
   * 
   * @return String manufacturer.
   */
  public String getManufacturer() {
    return manufacturer;
  }

  /**
   * Sets the manufacturer of this KitDescriptor object.
   * 
   * @param manufacturer manufacturer.
   */
  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  /**
   * Returns the partNumber of this KitDescriptor object.
   * 
   * @return String partNumber.
   */
  public String getPartNumber() {
    return partNumber;
  }

  /**
   * Sets the partNumber of this KitDescriptor object.
   * 
   * @param partNumber partNumber.
   */
  public void setPartNumber(String partNumber) {
    this.partNumber = partNumber;
  }

  /**
   * Returns the stockLevel of this KitDescriptor object.
   * 
   * @return Integer stockLevel.
   */
  public Integer getStockLevel() {
    return stockLevel;
  }

  /**
   * Sets the stockLevel of this KitDescriptor object.
   * 
   * @param stockLevel stockLevel.
   */
  public void setStockLevel(Integer stockLevel) {
    this.stockLevel = stockLevel;
  }

  /**
   * Sets the description of this KitDescriptor object.
   *
   * @param description description.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the description of this KitDescriptor object.
   *
   * @return String description.
   */
  public String getDescription() {
    return description;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  /**
   * Returns the kitType of this KitDescriptor object.
   * 
   * @return KitType kitType.
   */
  public KitType getKitType() {
    return kitType;
  }

  /**
   * Sets the kitType of this KitDescriptor object.
   * 
   * @param kitType kitType.
   * 
   */
  public void setKitType(KitType kitType) {
    this.kitType = kitType;
  }

  /**
   * Returns the platformType of this KitDescriptor object.
   * 
   * @return PlatformType platformType.
   */
  public PlatformType getPlatformType() {
    return platformType;
  }

  /**
   * Sets the platformType of this KitDescriptor object.
   * 
   * @param platformType platformType.
   */
  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changelog;
  }

  public void setChangeLog(Collection<ChangeLog> changelog) {
    this.changelog = changelog;
  }

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreator(User creator) {
    this.creator = creator;
  }

  @Override
  public Date getCreationTime() {
    return creationTime;
  }

  @Override
  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  public Date getLastModified() {
    return lastModified;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Set<TargetedSequencing> getTargetedSequencing() {
    return targetedSequencing;
  }

  public void addTargetedSequencing(TargetedSequencing targetedSequencing) {
    this.targetedSequencing.add(targetedSequencing);
    targetedSequencing.getKitDescriptors().add(this);
  }

  public void removeTargetedSequencing(TargetedSequencing targetedSequencing) {
    this.targetedSequencing.remove(targetedSequencing);
    targetedSequencing.getKitDescriptors().remove(this);
  }

  public void clearTargetedSequencing() {
    for (TargetedSequencing ts : this.targetedSequencing) {
      ts.getKitDescriptors().remove(this);
    }
    this.targetedSequencing.clear();
  }

  /**
   * Method toString ...
   * 
   * @return String
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getPartNumber());
    sb.append(" : ");
    sb.append(getKitType());
    sb.append(" : ");
    sb.append(getPlatformType());
    sb.append(" : ");
    sb.append(getStockLevel());
    sb.append(" : ");
    sb.append(getDescription());
    return sb.toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(21, 51)
        .append(description)
        .append(kitType)
        .append(manufacturer)
        .append(name)
        .append(partNumber)
        .append(platformType)
        .append(version)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    KitDescriptor other = (KitDescriptor) obj;
    return new EqualsBuilder()
        .append(description, other.description)
        .append(kitType, other.kitType)
        .append(manufacturer, other.manufacturer)
        .append(name, other.name)
        .append(partNumber, other.partNumber)
        .append(platformType, other.platformType)
        .append(version, other.version)
        .isEquals();
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    KitDescriptorChangeLog changeLog = new KitDescriptorChangeLog();
    changeLog.setKitDescriptor(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Kit Descriptor (" + getKitType().getKey() + ")";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }

}
