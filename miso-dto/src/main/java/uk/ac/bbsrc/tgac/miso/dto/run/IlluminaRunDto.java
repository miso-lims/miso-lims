package uk.ac.bbsrc.tgac.miso.dto.run;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "Illumina")
public class IlluminaRunDto extends RunDto {

  private String workflowType;
  private Integer numCycles;
  private Integer calledCycles;
  private Integer ImagedCycles;
  private Integer scoredCycles;
  private Boolean pairedEnd;
  private String basesMask;

  public String getWorkflowType() {
    return workflowType;
  }

  public void setWorkflowType(String workflowType) {
    this.workflowType = workflowType;
  }

  public Integer getNumCycles() {
    return numCycles;
  }

  public void setNumCycles(Integer numCycles) {
    this.numCycles = numCycles;
  }

  public Integer getCalledCycles() {
    return calledCycles;
  }

  public void setCalledCycles(Integer calledCycles) {
    this.calledCycles = calledCycles;
  }

  public Integer getImagedCycles() {
    return ImagedCycles;
  }

  public void setImagedCycles(Integer imagedCycles) {
    ImagedCycles = imagedCycles;
  }

  public Integer getScoredCycles() {
    return scoredCycles;
  }

  public void setScoredCycles(Integer scoredCycles) {
    this.scoredCycles = scoredCycles;
  }

  public Boolean getPairedEnd() {
    return pairedEnd;
  }

  public void setPairedEnd(Boolean pairedEnd) {
    this.pairedEnd = pairedEnd;
  }

  public String getBasesMask() {
    return basesMask;
  }

  public void setBasesMask(String basesMask) {
    this.basesMask = basesMask;
  }

}
