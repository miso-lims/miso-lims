package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PartitionQCTypeDto {
  private String description;
  private long id;
  private boolean noteRequired;
  private boolean orderFulfilled;
  private boolean analysisSkipped;

  public String getDescription() {
    return description;
  }

  public long getId() {
    return id;
  }

  public boolean isNoteRequired() {
    return noteRequired;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(long id) {
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
    if (!isOrderFulfilled() || isAnalysisSkipped()) sb.append(" (");
    if (!isOrderFulfilled()) sb.append("order not fulfilled");
    if (!isOrderFulfilled() && isAnalysisSkipped()) sb.append(", ");
    if (isAnalysisSkipped()) sb.append("analysis skipped");
    if (!isOrderFulfilled() || isAnalysisSkipped()) sb.append(")");
    return sb.toString();
  }

}
