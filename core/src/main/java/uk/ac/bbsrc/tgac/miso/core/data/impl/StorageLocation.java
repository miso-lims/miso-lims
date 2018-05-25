package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Box;

@Entity
public class StorageLocation {

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
    LOOSE_STORAGE("Loose Storage", Sets.newHashSet(SHELF), BoxStorageAmount.MULTIPLE);

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
  private long id;

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

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public StorageLocation getParentLocation() {
    return parentLocation;
  }

  public void setParentLocation(StorageLocation parentLocation) {
    this.parentLocation = parentLocation;
    if (parentLocation.childLocations == null) {
      parentLocation.childLocations = new HashSet<>();
    }
    parentLocation.childLocations.add(this);
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

}
