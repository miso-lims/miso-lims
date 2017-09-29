package uk.ac.bbsrc.tgac.miso.core.data;

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
  private Double volume;
  
  @Column(name = "alias")
  private String alias;

  @Override
  public Double getVolume() {
    return volume;
  }

  @Override
  public void setVolume(Double volume) {
    this.volume = volume;
  }

  @Override
  public boolean isDiscarded() {
    return discarded;
  }

  @Override
  public void setDiscarded(boolean discarded) {
    if (discarded) volume = 0.0;
    this.discarded = discarded;
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
  public int hashCode() {
    return new HashCodeBuilder(1, 31)
        .append(alias)
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
        .append(alias, other.alias)
        .append(discarded, other.discarded)
        .append(volume, other.volume)
        .isEquals();
  }
}
