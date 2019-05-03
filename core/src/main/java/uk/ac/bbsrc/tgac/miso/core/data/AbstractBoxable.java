package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

  private boolean distributed;
  @Temporal(TemporalType.DATE)
  private Date distributionDate;
  private String distributionRecipient;

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
  public boolean isDistributed() {
    return distributed;
  }

  @Override
  public void setDistributed(boolean distributed) {
    this.distributed = distributed;
  }

  @Override
  public Date getDistributionDate() {
    return distributionDate;
  }

  @Override
  public void setDistributionDate(Date distributionDate) {
    this.distributionDate = distributionDate;
  }

  @Override
  public String getDistributionRecipient() {
    return distributionRecipient;
  }

  @Override
  public void setDistributionRecipient(String distributionRecipient) {
    this.distributionRecipient = distributionRecipient;
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
