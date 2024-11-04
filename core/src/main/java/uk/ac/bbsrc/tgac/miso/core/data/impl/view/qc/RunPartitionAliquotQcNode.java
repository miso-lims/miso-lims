package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionAliquotQcNode.RunPartitionAliquotQcNodeId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Run_Partition_LibraryAliquot")
@Immutable
@IdClass(RunPartitionAliquotQcNodeId.class)
public class RunPartitionAliquotQcNode implements QcNode {

  public static class RunPartitionAliquotQcNodeId implements Serializable {

    private static final long serialVersionUID = 1L;

    private RunQcNode run;

    private RunPartitionQcNodePartition partition;

    private LibraryAliquotQcNode aliquot;

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

    public LibraryAliquotQcNode getAliquot() {
      return aliquot;
    }

    public void setAliquot(LibraryAliquotQcNode aliquot) {
      this.aliquot = aliquot;
    }

    @Override
    public int hashCode() {
      return Objects.hash(run, partition, aliquot);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          RunPartitionAliquotQcNodeId::getRun,
          RunPartitionAliquotQcNodeId::getPartition,
          RunPartitionAliquotQcNodeId::getAliquot);
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

  @Id
  @ManyToOne
  @JoinColumn(name = "aliquotId")
  private LibraryAliquotQcNode aliquot;

  @ManyToOne
  @JoinColumn(name = "statusId")
  private RunLibraryQcStatus qcStatus;

  private String qcNote;

  private Boolean dataReview;

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

  public LibraryAliquotQcNode getAliquot() {
    return aliquot;
  }

  public void setAliquot(LibraryAliquotQcNode aliquot) {
    this.aliquot = aliquot;
  }

  @Override
  public Long getId() {
    return null;
  }

  @Override
  public Long[] getIds() {
    return new Long[] {getRun().getId(), getPartition().getId(), getAliquot().getId()};
  }

  @Override
  public QcNodeType getEntityType() {
    return QcNodeType.RUN_LIBRARY;
  }

  @Override
  public String getTypeLabel() {
    return QcNodeType.RUN_LIBRARY.getLabel();
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String getLabel() {
    return String.format("%s partition %d - %s", getRun().getAlias(), getPartition().getPartitionNumber(),
        getAliquot().getAlias());
  }

  public RunLibraryQcStatus getQcStatus() {
    return qcStatus;
  }

  public void setQcStatus(RunLibraryQcStatus qcStatus) {
    this.qcStatus = qcStatus;
  }

  @Override
  public Boolean getQcPassed() {
    return getQcStatus() == null ? null : getQcStatus().getQcPassed();
  }

  @Override
  public Long getQcStatusId() {
    return getQcStatus() == null ? null : getQcStatus().getId();
  }

  @Override
  public String getQcNote() {
    return qcNote;
  }

  public void setQcNote(String qcNote) {
    this.qcNote = qcNote;
  }

  @Override
  public Boolean getDataReview() {
    return dataReview;
  }

  public void setDataReview(Boolean dataReview) {
    this.dataReview = dataReview;
  }

  @Override
  public List<? extends QcNode> getChildren() {
    return null;
  }

}
