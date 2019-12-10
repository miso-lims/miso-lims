package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
    if (discarded) volume = BigDecimal.ZERO;
    this.discarded = discarded;
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
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    AbstractBoxable other = (AbstractBoxable) obj;
    return new EqualsBuilder()
        .append(discarded, other.discarded)
        .append(volume, other.volume)
        .isEquals();
  }

}
