package uk.ac.bbsrc.tgac.miso.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.IlluminaWorkflowType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "RunIllumina")
public class IlluminaRun extends Run {
  private static final long serialVersionUID = 1L;

  public IlluminaRun() {
    super();
  }

  private String runBasesMask;
  private Integer callCycle;
  private Integer imgCycle;
  private Integer numCycles;
  private Integer scoreCycle;
  @Column(nullable = false)
  private boolean pairedEnd = true;
  @Enumerated(EnumType.STRING)
  private IlluminaWorkflowType workflowType;

  public String getRunBasesMask() {
    return runBasesMask;
  }

  public void setRunBasesMask(String runBasesMask) {
    this.runBasesMask = runBasesMask;
  }

  public Integer getCallCycle() {
    return callCycle;
  }

  public void setCallCycle(Integer callCycle) {
    this.callCycle = callCycle;
  }

  public Integer getImgCycle() {
    return imgCycle;
  }

  public void setImgCycle(Integer imgCycle) {
    this.imgCycle = imgCycle;
  }

  public Integer getNumCycles() {
    return numCycles;
  }

  public void setNumCycles(Integer numCycles) {
    this.numCycles = numCycles;
  }

  public Integer getScoreCycle() {
    return scoreCycle;
  }

  public void setScoreCycle(Integer scoreCycle) {
    this.scoreCycle = scoreCycle;
  }

  @Override
  public Boolean getPairedEnd() {
    return pairedEnd;
  }

  @Override
  public void setPairedEnd(boolean pairedEnd) {
    this.pairedEnd = pairedEnd;
  }

  public IlluminaWorkflowType getWorkflowType() {
    return workflowType;
  }

  public void setWorkflowType(IlluminaWorkflowType workflowType) {
    this.workflowType = workflowType;
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.ILLUMINA;
  }

  @Override
  public String getProgress() {
    if (getHealth() == HealthType.Running) {
      if (numCycles != null && callCycle != null) {
        if (numCycles != 0) {
          return String.format("Cycle %d of %d", callCycle, numCycles);
        }
      }
      return "Running";
    }
    return "";
  }

  @Override
  public String getDeleteType() {
    return "Illumina Run";
  }

}
