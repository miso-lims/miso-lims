package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingParametersImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

/**
 * Information about completion (success/failure/pending/requested) lanes. This maps over a view in the database for pool orders and run
 * information.
 */
@Entity
@Table(name = "OrderCompletion")
public class PoolOrderCompletion implements Serializable {
  @Embeddable
  public static class PoolOrderCompletionId implements Serializable {
    private static final long serialVersionUID = -2890725144995338712L;
    @Enumerated(EnumType.STRING)
    HealthType health;
    @ManyToOne(targetEntity = SequencingParametersImpl.class)
    @JoinColumn(name = "parametersId", nullable = false)
    SequencingParameters parameters;
    @ManyToOne(targetEntity = PoolImpl.class)
    @JoinColumn(name = "poolId")
    Pool pool;

    public PoolOrderCompletionId() {
    }

    public HealthType getHealth() {
      return health;
    }

    public SequencingParameters getParameters() {
      return parameters;
    }

    public Pool getPool() {
      return pool;
    }

    public void setHealth(HealthType health) {
      this.health = health;
    }

    public void setParameters(SequencingParameters parameters) {
      this.parameters = parameters;
    }

    public void setPool(Pool pool) {
      this.pool = pool;
    }

  }

  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private final PoolOrderCompletionId id = new PoolOrderCompletionId();
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  private int num_partitions;

  public HealthType getHealth() {
    return id.health;
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
