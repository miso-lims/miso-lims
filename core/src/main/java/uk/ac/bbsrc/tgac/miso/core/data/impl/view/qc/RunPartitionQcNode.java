package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionQcNode.RunPartitionQcNodeId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Run_Partition")
@Immutable
@IdClass(RunPartitionQcNodeId.class)
public class RunPartitionQcNode implements QcNode {

  public static class RunPartitionQcNodeId implements Serializable {

    private static final long serialVersionUID = 1L;

    private RunQcNode run;

    private RunPartitionQcNodePartition partition;

    public RunQcNode getRun() {
      return run;
    }

    public void setRun(RunQcNode run) {
      this.run = run;
    }

    public RunPartitionQcNodePartition getPartition() {
      return partition;
    }

    public void setPartition(RunPartitionQcNodePartition partition) {
      this.partition = partition;
    }

    @Override
    public int hashCode() {
      return Objects.hash(run, partition);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          RunPartitionQcNodeId::getRun,
          RunPartitionQcNodeId::getPartition);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "runId")
  private RunQcNode run;

  @Id
  @ManyToOne
  @JoinColumn(name = "partitionId")
  private RunPartitionQcNodePartition partition;

  private Long partitionQcTypeId;

  private String notes;

  @Transient
  private List<RunPartitionAliquotQcNode> runLibraries;

  public RunQcNode getRun() {
    return run;
  }

  public void setRun(RunQcNode run) {
    this.run = run;
  }

  public RunPartitionQcNodePartition getPartition() {
    return partition;
  }

  public void setPartition(RunPartitionQcNodePartition partition) {
    this.partition = partition;
  }

  public Long getPartitionQcTypeId() {
    return partitionQcTypeId;
  }

  public void setPartitionQcTypeId(Long partitionQcTypeId) {
    this.partitionQcTypeId = partitionQcTypeId;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public List<RunPartitionAliquotQcNode> getRunLibraries() {
    if (runLibraries == null) {
      runLibraries = new ArrayList<>();
    }
    return runLibraries;
  }

  public void setRunLibraries(List<RunPartitionAliquotQcNode> runLibraries) {
    this.runLibraries = runLibraries;
  }

  @Override
  public Long getId() {
    return null;
  }

  @Override
  public Long[] getIds() {
    return new Long[] {getRun().getId(), getPartition().getId()};
  }

  @Override
  public QcNodeType getEntityType() {
    return QcNodeType.RUN_PARTITION;
  }

  @Override
  public String getTypeLabel() {
    return QcNodeType.RUN_PARTITION.getLabel();
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String getLabel() {
    return String.format("%s partition %d", getRun().getAlias(), getPartition().getPartitionNumber());
  }

  @Override
  public Boolean getQcPassed() {
    return null;
  }

  @Override
  public Long getQcStatusId() {
    return partitionQcTypeId;
  }

  @Override
  public String getQcNote() {
    return notes;
  }

  @Override
  public List<? extends QcNode> getChildren() {
    return getRunLibraries();
  }

}
