package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingParametersImpl;

@Entity
@Table(name = "OrderCompletion")
public class PoolOrderCompletion implements Serializable {
  private static final long serialVersionUID = 1L;

  private int completed_partitions;
  private int desired_partitions;
  @Id
  @ManyToOne(targetEntity = SequencingParametersImpl.class)
  @JoinColumn(name = "parametersId", nullable = false)
  private SequencingParameters parameters;
  @Transient
  private Pool<?> pool;
  @Id
  private Long poolId;

  public int getCompletedPartitions() {
    return completed_partitions;
  }

  public int getDesiredPartitions() {
    return desired_partitions;
  }

  public Pool<?> getPool() {
    return pool;
  }

  public Long getPoolId() {
    return poolId;
  }

  public int getRemainingPartitions() {
    if (desired_partitions > completed_partitions) return desired_partitions - completed_partitions;
    return 0;
  }

  public SequencingParameters getSequencingParameters() {
    return parameters;
  }

  public void setPool(Pool<?> pool) {
    this.pool = pool;
  }
}
