package uk.ac.bbsrc.tgac.miso.core.data;

/*
 * Skeleton implementation of a Boxable item
 */
public abstract class AbstractBoxable implements Boxable {
  private boolean empty;
  private double volume;
  private Long boxId;
  private String boxAlias;
  private long positionId;
  private String alias;
  private String position;

  @Override
  public void setBoxId(Long boxId) {
    this.boxId = boxId;
  }

  @Override
  public Long getBoxId() {
    return boxId;
  }

  @Override
  public void setVolume(double volume) {
    this.volume = volume;
  }

  @Override
  public double getVolume() {
    return volume;
  }

  @Override
  public boolean isEmpty() {
    return empty;
  }

  @Override
  public void setEmpty(boolean empty) {
    if (empty) volume = 0;
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
}
