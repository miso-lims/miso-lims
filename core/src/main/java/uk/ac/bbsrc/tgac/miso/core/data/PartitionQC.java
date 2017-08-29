package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.PartitionQC.PartitionQCId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;

@Entity
@Table(name = "Run_Partition_QC")
@IdClass(PartitionQCId.class)
public class PartitionQC implements Serializable {
  public static class PartitionQCId implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(targetEntity = PartitionImpl.class)
    @JoinColumn(name = "partitionId")
    private Partition partition;

    @ManyToOne
    @JoinColumn(name = "runId")
    private Run run;

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      PartitionQCId other = (PartitionQCId) obj;
      if (partition == null) {
        if (other.partition != null) return false;
      } else if (!partition.equals(other.partition)) return false;
      if (run == null) {
        if (other.run != null) return false;
      } else if (!run.equals(other.run)) return false;
      return true;
    }

    public Partition getPartition() {
      return partition;
    }

    public Run getRun() {
      return run;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((partition == null) ? 0 : partition.hashCode());
      result = prime * result + ((run == null) ? 0 : run.hashCode());
      return result;
    }

    public void setPartition(Partition partition) {
      this.partition = partition;
    }

    public void setRun(Run run) {
      this.run = run;
    }

  }

  private static final long serialVersionUID = 1L;

  @Column(length = 1024)
  private String notes;

  @Id
  private Partition partition;

  @Id
  private Run run;

  @ManyToOne
  @JoinColumn(name = "partitionQcTypeId")
  private PartitionQCType type;

  public String getNotes() {
    return notes;
  }

  public Partition getPartition() {
    return partition;
  }

  public Run getRun() {
    return run;
  }

  public PartitionQCType getType() {
    return type;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public void setPartition(Partition partition) {
    this.partition = partition;
  }

  public void setRun(Run run) {
    this.run = run;
  }

  public void setType(PartitionQCType type) {
    this.type = type;
  }

}
