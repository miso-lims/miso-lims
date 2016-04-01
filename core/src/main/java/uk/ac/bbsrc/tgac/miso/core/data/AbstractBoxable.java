package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

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
  @Transient
  private long positionId;
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
  public long getBoxPositionId() {
    return positionId;
  }

  @Override
  public void setBoxPositionId(long id) {
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
}
