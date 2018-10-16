package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PartitionQCType")
public class PartitionQCType implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  private long partitionQcTypeId;

  @Column(nullable = false)
  private String description;

  private boolean noteRequired;

  private boolean orderFulfilled;

  private boolean analysisSkipped;

  public long getId() {
    return partitionQcTypeId;
  }

  public void setId(long partitionQcTypeId) {
    this.partitionQcTypeId = partitionQcTypeId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isNoteRequired() {
    return noteRequired;
  }

  public void setNoteRequired(boolean noteRequired) {
    this.noteRequired = noteRequired;
  }

  public boolean isOrderFulfilled() {
    return orderFulfilled;
  }

  public void setOrderFulfilled(boolean orderFulfillment) {
    this.orderFulfilled = orderFulfillment;
  }

  public boolean isAnalysisSkipped() {
    return analysisSkipped;
  }

  public void setAnalysisSkipped(boolean analysisSkipped) {
    this.analysisSkipped = analysisSkipped;
  }

}
