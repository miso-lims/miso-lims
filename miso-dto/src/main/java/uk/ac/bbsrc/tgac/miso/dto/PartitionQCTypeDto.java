package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PartitionQCTypeDto {

  private String description;
  private Long id;
  private boolean noteRequired;
  private boolean orderFulfilled = true;
  private boolean analysisSkipped;

  public String getDescription() {
    return description;
  }

  public Long getId() {
    return id;
  }

  public boolean isNoteRequired() {
    return noteRequired;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setNoteRequired(boolean noteRequired) {
    this.noteRequired = noteRequired;
  }

  public boolean isOrderFulfilled() {
    return orderFulfilled;
  }

  public void setOrderFulfilled(boolean orderFulfilled) {
    this.orderFulfilled = orderFulfilled;
  }

  public boolean isAnalysisSkipped() {
    return analysisSkipped;
  }

  public void setAnalysisSkipped(boolean analysisSkipped) {
    this.analysisSkipped = analysisSkipped;
  }

  @JsonProperty
  public String getDetailedLabel() {
    StringBuilder sb = new StringBuilder();
    sb.append(getDescription());
    sb.append(" (order ");
    if (!isOrderFulfilled()) sb.append("not ");
    sb.append("fulfilled, analysis ");
    if (isAnalysisSkipped()) {
      sb.append("skipped");
    } else {
      sb.append("to proceed");
    }
    sb.append(")");
    return sb.toString();
  }

}
