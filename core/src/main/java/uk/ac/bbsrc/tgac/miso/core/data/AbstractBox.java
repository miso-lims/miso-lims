package uk.ac.bbsrc.tgac.miso.core.data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.BoxChangeLog;

@MappedSuperclass
public abstract class AbstractBox implements Box {

  private static final long serialVersionUID = 1L;

  private static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long boxId = UNSAVED_ID;
  private String name;
  private String alias;
  private String description;
  private String identificationBarcode;
  private String locationBarcode;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  @Column(name = "created", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationTime;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @ManyToOne
  @JoinColumn(name = "boxSizeId")
  private BoxSize size;

  @ManyToOne
  @JoinColumn(name = "boxUseId")
  private BoxUse use;

  @OneToMany(targetEntity = BoxChangeLog.class, mappedBy = "box", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "locationId", nullable = true)
  private StorageLocation storageLocation;

  @Override
  public long getId() {
    return boxId;
  }

  @Override
  public void setId(long id) {
    this.boxId = id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
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
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  @Override
  public String getLocationBarcode() {
    return locationBarcode;
  }

  @Override
  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  @Override
  public BoxUse getUse() {
    return use;
  }

  @Override
  public void setUse(BoxUse use) {
    this.use = use;
  }

  @Override
  public BoxSize getSize() {
    return size;
  }

  @Override
  public void setSize(BoxSize size) {
    this.size = size;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
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
  public void setCreationTime(Date created) {
    this.creationTime = created;
  }

  @Override
  public String getLabelText() {
    return getAlias();
  }

  @Override
  public LocalDate getBarcodeDate() {
    return LocalDate.ofInstant(getCreationTime().toInstant(), ZoneId.systemDefault());
  }

  @Override
  public String getDeleteType() {
    return "Box";
  }

  @Override
  public String getDeleteDescription() {
    return getName() + " (" + getAlias() + ")";
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public StorageLocation getStorageLocation() {
    return storageLocation;
  }

  @Override
  public void setStorageLocation(StorageLocation storageLocation) {
    this.storageLocation = storageLocation;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((alias == null) ? 0 : alias.hashCode());
    result = prime * result + (int) (boxId ^ (boxId >>> 32));
    result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
    result = prime * result + ((creator == null) ? 0 : creator.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((identificationBarcode == null) ? 0 : identificationBarcode.hashCode());
    result = prime * result + ((locationBarcode == null) ? 0 : locationBarcode.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((size == null) ? 0 : size.hashCode());
    result = prime * result + ((storageLocation == null) ? 0 : storageLocation.hashCode());
    result = prime * result + ((use == null) ? 0 : use.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractBox other = (AbstractBox) obj;
    if (alias == null) {
      if (other.alias != null)
        return false;
    } else if (!alias.equals(other.alias))
      return false;
    if (boxId != other.boxId)
      return false;
    if (creationTime == null) {
      if (other.creationTime != null)
        return false;
    } else if (!creationTime.equals(other.creationTime))
      return false;
    if (creator == null) {
      if (other.creator != null)
        return false;
    } else if (!creator.equals(other.creator))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (identificationBarcode == null) {
      if (other.identificationBarcode != null)
        return false;
    } else if (!identificationBarcode.equals(other.identificationBarcode))
      return false;
    if (locationBarcode == null) {
      if (other.locationBarcode != null)
        return false;
    } else if (!locationBarcode.equals(other.locationBarcode))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (size == null) {
      if (other.size != null)
        return false;
    } else if (!size.equals(other.size))
      return false;
    if (storageLocation == null) {
      if (other.storageLocation != null)
        return false;
    } else if (!storageLocation.equals(other.storageLocation))
      return false;
    if (use == null) {
      if (other.use != null)
        return false;
    } else if (!use.equals(other.use))
      return false;
    return true;
  }

}
