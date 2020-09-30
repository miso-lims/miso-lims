package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot.RunPartitionAliquotId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Run_Partition_LibraryAliquot")
@IdClass(RunPartitionAliquotId.class)
public class RunPartitionAliquot implements Serializable {

  public static class RunPartitionAliquotId implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "runId")
    private Run run;

    @ManyToOne(targetEntity = PartitionImpl.class)
    @JoinColumn(name = "partitionId")
    private Partition partition;

    @ManyToOne
    @JoinColumn(name = "aliquotId")
    private LibraryAliquot aliquot;

    public Run getRun() {
      return run;
    }

    public void setRun(Run run) {
      this.run = run;
    }

    public Partition getPartition() {
      return partition;
    }

    public void setPartition(Partition partition) {
      this.partition = partition;
    }

    public LibraryAliquot getAliquot() {
      return aliquot;
    }

    public void setAliquot(LibraryAliquot aliquot) {
      this.aliquot = aliquot;
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          RunPartitionAliquotId::getRun,
          RunPartitionAliquotId::getPartition,
          RunPartitionAliquotId::getAliquot);
    }

    @Override
    public int hashCode() {
      return Objects.hash(getRun(), getPartition(), getAliquot());
    }

  }
  
  private static final long serialVersionUID = 1L;

  @Id
  private Run run;

  @Id
  private Partition partition;

  @Id
  private LibraryAliquot aliquot;

  @ManyToOne
  @JoinColumn(name = "purposeId")
  private RunPurpose purpose;

  private Boolean qcPassed;

  private String qcNote;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier")
  private User lastModifier;

  public RunPartitionAliquot() {
    // Default constructor
  }

  public RunPartitionAliquot(Run run, Partition partition, PoolableElementView aliquot) {
    this.run = run;
    this.partition = partition;
    this.aliquot = new LibraryAliquot();
    this.aliquot.setId(aliquot.getAliquotId());
    this.aliquot.setName(aliquot.getAliquotName());
    this.aliquot.setAlias(aliquot.getAliquotAlias());
  }

  public Run getRun() {
    return run;
  }

  public void setRun(Run run) {
    this.run = run;
  }

  public Partition getPartition() {
    return partition;
  }

  public void setPartition(Partition partition) {
    this.partition = partition;
  }

  public LibraryAliquot getAliquot() {
    return aliquot;
  }

  public void setAliquot(LibraryAliquot aliquot) {
    this.aliquot = aliquot;
  }

  public RunPurpose getPurpose() {
    return purpose;
  }

  public void setPurpose(RunPurpose purpose) {
    this.purpose = purpose;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  public String getQcNote() {
    return qcNote;
  }

  public void setQcNote(String qcNote) {
    this.qcNote = qcNote;
  }

  public User getLastModifier() {
    return lastModifier;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

}
