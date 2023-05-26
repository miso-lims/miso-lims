package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot.RunPartitionAliquotId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
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
    private ListLibraryAliquotView aliquot;

    public RunPartitionAliquotId() {
      // default constructor
    }

    public RunPartitionAliquotId(Run run, Partition partition, ListLibraryAliquotView aliquot) {
      this.run = run;
      this.partition = partition;
      this.aliquot = aliquot;
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

    public ListLibraryAliquotView getAliquot() {
      return aliquot;
    }

    public void setAliquot(ListLibraryAliquotView aliquot) {
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
  private ListLibraryAliquotView aliquot;

  @ManyToOne
  @JoinColumn(name = "purposeId")
  private RunPurpose purpose;

  @ManyToOne
  @JoinColumn(name = "statusId")
  private RunLibraryQcStatus qcStatus;

  private String qcNote;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "qcUser")
  private User qcUser;

  private LocalDate qcDate;

  private Boolean dataReview;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "dataReviewerId")
  private User dataReviewer;

  private LocalDate dataReviewDate;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier")
  private User lastModifier;

  public RunPartitionAliquot() {
    // Default constructor
  }

  public RunPartitionAliquot(Run run, Partition partition, ListLibraryAliquotView aliquot) {
    this.run = run;
    this.partition = partition;
    this.aliquot = aliquot;
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

  public ListLibraryAliquotView getAliquot() {
    return aliquot;
  }

  public void setAliquot(ListLibraryAliquotView aliquot) {
    this.aliquot = aliquot;
  }

  public RunPurpose getPurpose() {
    return purpose;
  }

  public void setPurpose(RunPurpose purpose) {
    this.purpose = purpose;
  }

  public RunLibraryQcStatus getQcStatus() {
    return qcStatus;
  }

  public void setQcStatus(RunLibraryQcStatus qcStatus) {
    this.qcStatus = qcStatus;
  }

  public String getQcNote() {
    return qcNote;
  }

  public void setQcNote(String qcNote) {
    this.qcNote = qcNote;
  }

  public User getQcUser() {
    return qcUser;
  }

  public void setQcUser(User qcUser) {
    this.qcUser = qcUser;
  }

  public LocalDate getQcDate() {
    return qcDate;
  }

  public void setQcDate(LocalDate qcDate) {
    this.qcDate = qcDate;
  }

  public Boolean getDataReview() {
    return dataReview;
  }

  public void setDataReview(Boolean dataReview) {
    this.dataReview = dataReview;
  }

  public User getDataReviewer() {
    return dataReviewer;
  }

  public void setDataReviewer(User dataReviewer) {
    this.dataReviewer = dataReviewer;
  }

  public LocalDate getDataReviewDate() {
    return dataReviewDate;
  }

  public void setDataReviewDate(LocalDate dataReviewDate) {
    this.dataReviewDate = dataReviewDate;
  }

  public User getLastModifier() {
    return lastModifier;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

}
