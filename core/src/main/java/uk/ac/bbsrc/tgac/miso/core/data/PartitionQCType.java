package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PartitionQCType")
public class PartitionQCType implements Deletable, Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long partitionQcTypeId = UNSAVED_ID;

  @Column(nullable = false)
  private String description;

  private boolean noteRequired;

  private boolean orderFulfilled;

  private boolean analysisSkipped;

  @Override
  public long getId() {
    return partitionQcTypeId;
  }

  @Override
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

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Partition QC Type";
  }

  @Override
  public String getDeleteDescription() {
    return getDescription();
  }

}
