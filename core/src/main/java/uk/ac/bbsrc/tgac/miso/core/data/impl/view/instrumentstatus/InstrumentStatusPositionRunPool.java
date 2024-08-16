package uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPositionRunPool.InstrumentStatusPositionRunPoolId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "InstrumentStatusPositionRunPoolView")
@Immutable
@IdClass(InstrumentStatusPositionRunPoolId.class)
public class InstrumentStatusPositionRunPool implements Serializable {

  public static class InstrumentStatusPositionRunPoolId implements Serializable {

    private static final long serialVersionUID = 1L;

    private long runId;
    private long positionId;
    private long partitionId;
    private long poolId;

    public long getRunId() {
      return runId;
    }

    public void setRunId(long runId) {
      this.runId = runId;
    }

    public long getPositionId() {
      return positionId;
    }

    public void setPositionId(long positionId) {
      this.positionId = positionId;
    }

    public long getPartitionId() {
      return partitionId;
    }

    public void setPartitionId(long partitionId) {
      this.partitionId = partitionId;
    }

    public long getPoolId() {
      return poolId;
    }

    public void setPoolId(long poolId) {
      this.poolId = poolId;
    }

    @Override
    public int hashCode() {
      return Objects.hash(partitionId, poolId, positionId, runId);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          InstrumentStatusPositionRunPoolId::getPartitionId,
          InstrumentStatusPositionRunPoolId::getPoolId,
          InstrumentStatusPositionRunPoolId::getPositionId,
          InstrumentStatusPositionRunPoolId::getRunId);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  private long runId;

  @Id
  private long positionId = -1;

  @Id
  private long partitionId;

  @Id
  private long poolId;

  private String name;
  private String alias;

  public long getRunId() {
    return runId;
  }

  public void setRunId(long runId) {
    this.runId = runId;
  }

  public long getPositionId() {
    return positionId;
  }

  public void setPositionId(long positionId) {
    this.positionId = positionId;
  }

  public long getPartitionId() {
    return partitionId;
  }

  public void setPartitionId(long partitionId) {
    this.partitionId = partitionId;
  }

  public long getPoolId() {
    return poolId;
  }

  public void setPoolId(long id) {
    this.poolId = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

}
