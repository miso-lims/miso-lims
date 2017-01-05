package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingParametersImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

/**
 * Information about completion (success/failure/pending/requested) lanes. This maps over a view in the database for pool orders and run
 * information.
 */
@Entity
@Table(name = "OrderCompletion")
public class PoolOrderCompletion implements Serializable {
  private static final long serialVersionUID = 1L;

  @Enumerated(EnumType.STRING)
  private HealthType health;
  private Date lastUpdated;
  private int num_partitions;
  @Id
  @ManyToOne(targetEntity = SequencingParametersImpl.class)
  @JoinColumn(name = "parametersId", nullable = false)
  private SequencingParameters parameters;
  @Id
  @ManyToOne
  @JoinColumn(name = "poolId")
  private Pool pool;

  public HealthType getHealth() {
    return health;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public int getNumPartitions() {
    return num_partitions;
  }

  public Pool getPool() {
    return pool;
  }

  public SequencingParameters getSequencingParameters() {
    return parameters;
  }

  public void setHealth(HealthType health) {
    this.health = health;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public void setPool(Pool pool) {
    this.pool = pool;
  }
}
