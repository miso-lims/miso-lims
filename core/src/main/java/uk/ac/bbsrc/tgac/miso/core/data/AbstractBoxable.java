package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

/*
 * Skeleton implementation of a Boxable item
 */
@MappedSuperclass
public abstract class AbstractBoxable implements Boxable {

  private static final long serialVersionUID = 1L;

  @Column(name = "discarded")
  private boolean discarded;
  @Column(nullable = true)
  private BigDecimal volume;

  @Transient
  private Long pendingBoxId;

  @Transient
  private String pendingBoxPosition;

  @Override
  public BigDecimal getVolume() {
    return volume;
  }

  @Override
  public void setVolume(BigDecimal volume) {
    this.volume = volume;
  }

  @Override
  public boolean isDiscarded() {
    return discarded;
  }

  @Override
  public void setDiscarded(boolean discarded) {
    if (discarded)
      volume = BigDecimal.ZERO;
    this.discarded = discarded;
  }

  @Override
  public Long getPendingBoxId() {
    return pendingBoxId;
  }

  @Override
  public String getPendingBoxPosition() {
    return pendingBoxPosition;
  }

  @Override
  public void setPendingBoxId(Long pendingBoxId) {
    this.pendingBoxId = pendingBoxId;
  }

  @Override
  public void setPendingBoxPosition(String pendingBoxPosition) {
    this.pendingBoxPosition = pendingBoxPosition;
  }

  @Override
  public void moveBoxPositionToPending() {
    setPendingBoxId(getBox() == null ? null : getBox().getId());
    setPendingBoxPosition(getBoxPosition());
    removeFromBox();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(1, 31)
        .append(discarded)
        .append(volume)
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
    AbstractBoxable other = (AbstractBoxable) obj;
    return new EqualsBuilder()
        .append(discarded, other.discarded)
        .append(volume, other.volume)
        .isEquals();
  }

}
