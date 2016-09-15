package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/*
 * Skeleton implementation of a Boxable item
 */
@MappedSuperclass
public abstract class AbstractBoxable implements Boxable {

  @Column(name = "emptied")
  private boolean empty;
  @Column(nullable = true)
  private Double volume;
  @Transient
  private Long boxId;
  @Transient
  private String boxAlias;
  @Transient
  private String boxLocation;
  
  // Hibernate will read only. BoxPositionId is generated by DB trigger
  @Column(name = "boxPositionId", insertable = false, updatable = false)
  private Long positionId;
  @Transient
  private String alias;
  @Transient
  private String position;

  @Override
  public Double getVolume() {
    return volume;
  }

  @Override
  public void setVolume(Double volume) {
    this.volume = volume;
  }

  @Override
  public void setBoxId(Long boxId) {
    this.boxId = boxId;
  }

  @Override
  public Long getBoxId() {
    return boxId;
  }

  @Override
  public boolean isEmpty() {
    return empty;
  }

  @Override
  public void setEmpty(boolean empty) {
    if (empty) volume = 0.0;
    this.empty = empty;
  }

  @Override
  public String getBoxAlias() {
    return boxAlias;
  }

  @Override
  public void setBoxAlias(String boxAlias) {
    this.boxAlias = boxAlias;
  }

  @Override
  public Long getBoxPositionId() {
    return positionId;
  }

  @Override
  public void setBoxPositionId(Long id) {
    positionId = id;
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
  public String getBoxPosition() {
    return position;
  }

  @Override
  public void setBoxPosition(String position) {
    this.position = position;
  }
  
  @Override
  public String getBoxLocation() {
    return boxLocation;
  }
  
  @Override
  public void setBoxLocation(String boxLocation) {
    this.boxLocation = boxLocation;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(1, 31)
        .append(alias)
        .append(boxAlias)
        .append(boxLocation)
        .append(empty)
        .append(position)
        .append(volume)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    AbstractBoxable other = (AbstractBoxable) obj;
    return new EqualsBuilder()
        .append(alias, other.alias)
        .append(boxAlias, other.boxAlias)
        .append(boxLocation, other.boxLocation)
        .append(empty, other.empty)
        .append(position, other.position)
        .append(volume, other.volume)
        .isEquals();
  }
}
