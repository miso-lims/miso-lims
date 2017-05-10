package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;

@Entity
@Table(name = "BoxableView")
public class BoxableView {

  @Embeddable
  public static class BoxableId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private EntityType targetType;
    private long targetId;

    public BoxableId() {

    }

    public BoxableId(EntityType targetType, long targetId) {
      this.targetType = targetType;
      this.targetId = targetId;
    }

    public EntityType getTargetType() {
      return targetType;
    }

    public void setTargetType(EntityType targetType) {
      this.targetType = targetType;
    }

    public long getTargetId() {
      return targetId;
    }

    public void setTargetId(long targetId) {
      this.targetId = targetId;
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder(71, 49)
          .append(targetId)
          .append(targetType)
          .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      BoxableId other = (BoxableId) obj;
      return new EqualsBuilder()
          .append(targetId, other.targetId)
          .append(targetType, other.targetType)
          .isEquals();
    }
  }

  @EmbeddedId
  private BoxableId id;

  private String name;
  private String alias;
  private String identificationBarcode;
  private String locationBarcode;
  private Double volume;
  private boolean discarded;
  private Long boxId;
  private String boxName;
  private String boxAlias;
  private String boxPosition;
  private String boxLocationBarcode;

  public BoxableId getId() {
    return id;
  }

  public void setId(BoxableId id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  public Double getVolume() {
    return volume;
  }

  public void setVolume(Double volume) {
    this.volume = volume;
  }

  public boolean isDiscarded() {
    return discarded;
  }

  public void setDiscarded(boolean discarded) {
    this.discarded = discarded;
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

  public String getBoxPosition() {
    return boxPosition;
  }

  public void setBoxPosition(String position) {
    this.boxPosition = position;
  }

  public String getBoxLocationBarcode() {
    return boxLocationBarcode;
  }

  public void setBoxLocationBarcode(String boxLocationBarcode) {
    this.boxLocationBarcode = boxLocationBarcode;
  }

}
