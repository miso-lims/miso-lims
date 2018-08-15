package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.StorageLocationChangeLog;

@Entity
public class StorageLocation implements Serializable, Aliasable, ChangeLoggable {

  private static final long serialVersionUID = 1L;

  public static final long UNSAVED_ID = 0L;

  public enum BoxStorageAmount {
    NONE, SINGLE, MULTIPLE;
  }

  public enum LocationUnit {
    ROOM("Room", Collections.emptySet(), BoxStorageAmount.NONE),
    FREEZER("Freezer", Sets.newHashSet(ROOM), BoxStorageAmount.NONE),
    SHELF("Shelf", Sets.newHashSet(FREEZER), BoxStorageAmount.NONE),
    RACK("Rack", Sets.newHashSet(SHELF), BoxStorageAmount.NONE),
    STACK("Stack", Sets.newHashSet(FREEZER, SHELF, RACK), BoxStorageAmount.NONE),
    STACK_POSITION("Slot", Sets.newHashSet(STACK), BoxStorageAmount.SINGLE),
    LOOSE_STORAGE("Loose Storage", Sets.newHashSet(SHELF), BoxStorageAmount.MULTIPLE),
    TRAY_RACK("Tray Rack", Sets.newHashSet(SHELF), BoxStorageAmount.NONE),
    TRAY("Tray", Sets.newHashSet(TRAY_RACK), BoxStorageAmount.MULTIPLE);

    private final String displayName;
    private final Set<LocationUnit> parents;
    private final BoxStorageAmount boxStorageAmount;

    private LocationUnit(String displayName, Set<LocationUnit> parents, BoxStorageAmount boxStorageAmount) {
      this.displayName = displayName;
      this.parents = Collections.unmodifiableSet(parents);
      this.boxStorageAmount = boxStorageAmount;
    }

    public String getDisplayName() {
      return displayName;
    }

    public Set<LocationUnit> getAcceptableParents() {
      return parents;
    }

    public BoxStorageAmount getBoxStorageAmount() {
      return boxStorageAmount;
    }
  }

  @Id
  @Column(name = "locationId")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id = UNSAVED_ID;

  @ManyToOne
  @JoinColumn(name = "parentLocationId", nullable = true)
  private StorageLocation parentLocation;

  @OneToMany(mappedBy = "parentLocation")
  private Set<StorageLocation> childLocations;

  @Enumerated(EnumType.STRING)
  private LocationUnit locationUnit;

  private String alias;

  private String identificationBarcode;

  @OneToMany(targetEntity = BoxImpl.class, mappedBy = "storageLocation")
  private Set<Box> boxes;

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

  @OneToMany(targetEntity = StorageLocationChangeLog.class, mappedBy = "storageLocation", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @Override
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public StorageLocation getParentLocation() {
    return parentLocation;
  }

  public StorageLocation getFreezerLocation() {
    if (locationUnit == LocationUnit.FREEZER) {
      return this;
    } else if (getParentLocation() == null || locationUnit == LocationUnit.ROOM) {
      return null;
    }
    return getParentLocation().getFreezerLocation();
  }

  public void setParentLocation(StorageLocation parentLocation) {
    if (this.getLocationUnit() != LocationUnit.ROOM) {
      // rooms are at the top of the hierarchy so parentLocation should be null
      this.parentLocation = parentLocation;
      if (parentLocation.childLocations == null) {
        parentLocation.childLocations = new HashSet<>();
      }
      parentLocation.childLocations.add(this);
    }
  }

  public Set<StorageLocation> getChildLocations() {
    return childLocations == null ? Collections.emptySet() : childLocations;
  }

  public LocationUnit getLocationUnit() {
    return locationUnit;
  }

  public void setLocationUnit(LocationUnit locationUnit) {
    this.locationUnit = locationUnit;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public Set<Box> getBoxes() {
    return boxes == null ? Collections.emptySet() : boxes;
  }

  public void setBoxes(Set<Box> boxes) {
    this.boxes = boxes;
  }

  public String getDisplayLocation() {
    return getLocationUnit().getDisplayName() + " " + getAlias();
  }

  public String getFullDisplayLocation() {
    return (getParentLocation() == null ? "" : getParentLocation().getFullDisplayLocation() + ", ") + getDisplayLocation();
  }
  
  public String getFreezerDisplayLocation() {
    return ((getParentLocation() == null || getParentLocation().getLocationUnit() == LocationUnit.FREEZER
        || getParentLocation().getLocationUnit() == LocationUnit.ROOM) ? ""
        : getParentLocation().getFreezerDisplayLocation() + ", ") + getDisplayLocation();
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User user) {
    this.lastModifier = user;
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
  public void setCreator(User user) {
    this.creator = user;
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
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    StorageLocationChangeLog change = new StorageLocationChangeLog();
    change.setStorageLocation(this);
    change.setSummary(summary);
    change.setColumnsChanged(columnsChanged);
    change.setUser(user);
    return change;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((alias == null) ? 0 : alias.hashCode());
    result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
    result = prime * result + ((creator == null) ? 0 : creator.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((identificationBarcode == null) ? 0 : identificationBarcode.hashCode());
    result = prime * result + ((locationUnit == null) ? 0 : locationUnit.hashCode());
    result = prime * result + ((parentLocation == null) ? 0 : parentLocation.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    StorageLocation other = (StorageLocation) obj;
    if (alias == null) {
      if (other.alias != null) return false;
    } else if (!alias.equals(other.alias)) return false;
    if (creationTime == null) {
      if (other.creationTime != null) return false;
    } else if (!creationTime.equals(other.creationTime)) return false;
    if (creator == null) {
      if (other.creator != null) return false;
    } else if (!creator.equals(other.creator)) return false;
    if (id != other.id) return false;
    if (identificationBarcode == null) {
      if (other.identificationBarcode != null) return false;
    } else if (!identificationBarcode.equals(other.identificationBarcode)) return false;
    if (locationUnit != other.locationUnit) return false;
    if (parentLocation == null) {
      if (other.parentLocation != null) return false;
    } else if (!parentLocation.equals(other.parentLocation)) return false;
    return true;
  }

}
