package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

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
  private int num_partitions;
  @Id
  @ManyToOne(targetEntity = SequencingParametersImpl.class)
  @JoinColumn(name = "parametersId", nullable = false)
  private SequencingParameters parameters;
  @Transient
  private Pool<? extends Poolable<?, ?>> pool;
  @Id
  private Long poolId;

  public HealthType getHealth() {
    return health;
  }

  public int getNumPartitions() {
    return num_partitions;
  }

  public Pool<? extends Poolable<?, ?>> getPool() {
    return pool;
  }

  public Long getPoolId() {
    return poolId;
  }

  public SequencingParameters getSequencingParameters() {
    return parameters;
  }

  public void setHealth(HealthType health) {
    this.health = health;
  }

  public void setPool(Pool<? extends Poolable<?, ?>> pool) {
    this.pool = pool;
  }
}
