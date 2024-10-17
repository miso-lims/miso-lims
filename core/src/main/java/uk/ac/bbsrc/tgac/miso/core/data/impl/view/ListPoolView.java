package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.Immutable;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Timestamped;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Immutable
public class ListPoolView implements Aliasable, Nameable, Serializable, Timestamped {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  private long poolId = UNSAVED_ID;
  private String name;
  private String alias;
  private String identificationBarcode;
  private String description;

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  private LocalDate creationDate;

  @Column(name = "created", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationTime;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  private Double concentration;

  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;

  private Integer dnaSize;
  private boolean discarded;
  private boolean distributed;

  @OneToOne(targetEntity = BoxImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "boxId", insertable = false, updatable = false)
  private Box box;

  private Long boxId;
  private String boxName;
  private String boxAlias;
  private String boxLocationBarcode;
  private String boxPosition;

  @ManyToMany
  @JoinTable(name = "Pool_LibraryAliquot", joinColumns = @JoinColumn(name = "poolId"),
      inverseJoinColumns = @JoinColumn(name = "aliquotId"))
  private List<ListPoolViewElement> elements;

  @Immutable
  @ManyToMany
  @JoinTable(name = "Transfer_Pool", joinColumns = {@JoinColumn(name = "poolId")}, inverseJoinColumns = {
      @JoinColumn(name = "transferId")})
  private Set<ListTransferView> listTransferViews;

  @Override
  public long getId() {
    return poolId;
  }

  @Override
  public void setId(long poolId) {
    this.poolId = poolId;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public PlatformType getPlatformType() {
    return platformType;
  }

  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreator(User creator) {
    this.creator = creator;
  }

  /**
   * @return the user-specified creation date of the pool
   */
  public LocalDate getCreationDate() {
    return creationDate;
  }

  /**
   * Sets the user-specified creation date of the pool
   * 
   * @param creationDate
   */
  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
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

  public Double getConcentration() {
    return concentration;
  }

  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }

  public ConcentrationUnit getConcentrationUnits() {
    return concentrationUnits;
  }

  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
  }

  public Integer getDnaSize() {
    return dnaSize;
  }

  public void setDnaSize(Integer dnaSize) {
    this.dnaSize = dnaSize;
  }

  public boolean isDiscarded() {
    return discarded;
  }

  public void setDiscarded(boolean discarded) {
    this.discarded = discarded;
  }

  public boolean isDistributed() {
    return distributed;
  }

  public void setDistributed(boolean distributed) {
    this.distributed = distributed;
  }

  public Box getBox() {
    return box;
  }

  public void setBox(Box box) {
    this.box = box;
  }

  public Long getBoxId() {
    return boxId;
  }

  public void setBoxId(Long boxId) {
    this.boxId = boxId;
  }

  public String getBoxName() {
    return boxName;
  }

  public void setBoxName(String boxName) {
    this.boxName = boxName;
  }

  public String getBoxAlias() {
    return boxAlias;
  }

  public void setBoxAlias(String boxAlias) {
    this.boxAlias = boxAlias;
  }

  public String getBoxLocationBarcode() {
    return boxLocationBarcode;
  }

  public void setBoxLocationBarcode(String boxLocationBarcode) {
    this.boxLocationBarcode = boxLocationBarcode;
  }

  public String getBoxPosition() {
    return boxPosition;
  }

  public void setBoxPosition(String boxPosition) {
    this.boxPosition = boxPosition;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public Set<String> getPrioritySubprojectAliases() {
    return getElements().stream()
        .filter(element -> element.isSubprojectPriority() != null && element.isSubprojectPriority())
        .map(ListPoolViewElement::getSubprojectAlias)
        .collect(Collectors.toSet());
  }

  public boolean hasLowQualityMembers() {
    return getElements().stream().anyMatch(element -> element.isLowQuality());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((alias == null) ? 0 : alias.hashCode());
    result = prime * result + ((boxAlias == null) ? 0 : boxAlias.hashCode());
    result = prime * result + ((boxId == null) ? 0 : boxId.hashCode());
    result = prime * result + ((boxLocationBarcode == null) ? 0 : boxLocationBarcode.hashCode());
    result = prime * result + ((boxName == null) ? 0 : boxName.hashCode());
    result = prime * result + ((boxPosition == null) ? 0 : boxPosition.hashCode());
    result = prime * result + ((concentration == null) ? 0 : concentration.hashCode());
    result = prime * result + ((concentrationUnits == null) ? 0 : concentrationUnits.hashCode());
    result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
    result = prime * result + ((creator == null) ? 0 : creator.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
    result = prime * result + ((lastModifier == null) ? 0 : lastModifier.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + (int) (poolId ^ (poolId >>> 32));
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
    ListPoolView other = (ListPoolView) obj;
    if (alias == null) {
      if (other.alias != null)
        return false;
    } else if (!alias.equals(other.alias))
      return false;
    if (boxAlias == null) {
      if (other.boxAlias != null)
        return false;
    } else if (!boxAlias.equals(other.boxAlias))
      return false;
    if (boxId == null) {
      if (other.boxId != null)
        return false;
    } else if (!boxId.equals(other.boxId))
      return false;
    if (boxLocationBarcode == null) {
      if (other.boxLocationBarcode != null)
        return false;
    } else if (!boxLocationBarcode.equals(other.boxLocationBarcode))
      return false;
    if (boxName == null) {
      if (other.boxName != null)
        return false;
    } else if (!boxName.equals(other.boxName))
      return false;
    if (boxPosition == null) {
      if (other.boxPosition != null)
        return false;
    } else if (!boxPosition.equals(other.boxPosition))
      return false;
    if (concentration == null) {
      if (other.concentration != null)
        return false;
    } else if (!concentration.equals(other.concentration))
      return false;
    if (concentrationUnits != other.concentrationUnits)
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
    if (lastModified == null) {
      if (other.lastModified != null)
        return false;
    } else if (!lastModified.equals(other.lastModified))
      return false;
    if (lastModifier == null) {
      if (other.lastModifier != null)
        return false;
    } else if (!lastModifier.equals(other.lastModifier))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (poolId != other.poolId)
      return false;
    return true;
  }

  public List<ListPoolViewElement> getElements() {
    if (elements == null) {
      elements = new ArrayList<>();
    }
    return elements;
  }

  public void setElements(List<ListPoolViewElement> elements) {
    this.elements = elements;
  }

  public Set<ListTransferView> getTransferViews() {
    if (listTransferViews == null) {
      listTransferViews = new HashSet<>();
    }
    return listTransferViews;
  }

  public String getLongestIndex() {
    return LimsUtils.getLongestIndex(getElements());
  }

}
