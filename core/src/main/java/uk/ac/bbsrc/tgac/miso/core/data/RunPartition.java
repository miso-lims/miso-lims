package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition.RunPartitionId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

@Entity
@Table(name = "Run_Partition")
@IdClass(RunPartitionId.class)
public class RunPartition implements Serializable {

  public static class RunPartitionId implements Serializable {

    private static final long serialVersionUID = 1L;

    private long runId;

    private long partitionId;

    public long getRunId() {
      return runId;
    }

    public void setRunId(long runId) {
      this.runId = runId;
    }

    public long getPartitionId() {
      return partitionId;
    }

    public void setPartitionId(long partitionId) {
      this.partitionId = partitionId;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (partitionId ^ (partitionId >>> 32));
      result = prime * result + (int) (runId ^ (runId >>> 32));
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      RunPartitionId other = (RunPartitionId) obj;
      if (partitionId != other.partitionId)
        return false;
      if (runId != other.runId)
        return false;
      return true;
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  private long runId;

  @Id
  private long partitionId;

  @ManyToOne
  @JoinColumn(name = "partitionQcTypeId")
  private PartitionQCType qcType;

  @Column(length = 1024)
  private String notes;

  @ManyToOne
  @JoinColumn(name = "purposeId")
  private RunPurpose purpose;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier")
  private User lastModifier;

  public long getRunId() {
    return runId;
  }

  public void setRunId(long runId) {
    this.runId = runId;
  }

  public long getPartitionId() {
    return partitionId;
  }

  public void setPartitionId(long partitionId) {
    this.partitionId = partitionId;
  }

  public PartitionQCType getQcType() {
    return qcType;
  }

  public void setQcType(PartitionQCType qcType) {
    this.qcType = qcType;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
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
