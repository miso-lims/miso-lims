package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.RunPartition.RunPartitionId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

@Entity
@Table(name = "Run_Partition")
@IdClass(RunPartitionId.class)
public class RunPartition implements Serializable {

  public static class RunPartitionId implements Serializable {

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
      RunPartitionId other = (RunPartitionId) obj;
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
  private PartitionQCType qcType;

  @ManyToOne
  @JoinColumn(name = "purposeId")
  private RunPurpose purpose;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier")
  private User lastModifier;

  public String getNotes() {
    return notes;
  }

  public Partition getPartition() {
    return partition;
  }

  public Run getRun() {
    return run;
  }

  public PartitionQCType getQcType() {
    return qcType;
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

  public void setQcType(PartitionQCType qcType) {
    this.qcType = qcType;
  }

  public RunPurpose getPurpose() {
    return purpose;
  }

  public void setPurpose(RunPurpose purpose) {
    this.purpose = purpose;
  }

  public User getLastModifier() {
    return lastModifier;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

}
