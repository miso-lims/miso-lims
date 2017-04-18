package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion.PoolOrderCompletionId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingParametersImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

/**
 * Information about completion (success/failure/pending/requested) lanes. This maps over a view in the database for pool orders and run
 * information.
 */
@Entity
@Table(name = "OrderCompletion")
@IdClass(PoolOrderCompletionId.class)
public class PoolOrderCompletion implements Serializable {
  @Embeddable
  public static class PoolOrderCompletionId implements Serializable {
    private static final long serialVersionUID = -2890725144995338712L;
    @ManyToOne(targetEntity = SequencingParametersImpl.class)
    @JoinColumn(name = "parametersId", nullable = false)
    SequencingParameters parameters;
    @ManyToOne(targetEntity = PoolImpl.class)
    @JoinColumn(name = "poolId")
    Pool pool;

    public PoolOrderCompletionId() {
    }

    public SequencingParameters getParameters() {
      return parameters;
    }

    public Pool getPool() {
      return pool;
    }

    public void setParameters(SequencingParameters parameters) {
      this.parameters = parameters;
    }

    public void setPool(Pool pool) {
      this.pool = pool;
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  private Pool pool;
  @Id
  private SequencingParameters parameters;

  @ElementCollection
  @CollectionTable(name = "OrderCompletion_Items", joinColumns = { @JoinColumn(name = "poolId"), @JoinColumn(name = "parametersId") })
  @Column(name = "num_partitions")
  @MapKeyClass(HealthType.class)
  @MapKeyColumn(name = "health", unique = true)
  @MapKeyEnumerated(EnumType.STRING)
  private Map<HealthType, Integer> items;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  private int remaining;


  public Map<HealthType, Integer> getItems() {
    return items;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public Pool getPool() {
    return pool;
  }

  public int getRemaining() {
    return remaining;
  }

  public SequencingParameters getSequencingParameters() {
    return parameters;
  }

  public void setItems(Map<HealthType, Integer> items) {
    this.items = items;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public void setPool(Pool pool) {
    this.pool = pool;
  }

  public void setRemaining(int remaining) {
    this.remaining = remaining;
  }
}
