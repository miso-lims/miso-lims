package uk.ac.bbsrc.tgac.miso.dto;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class IlluminaNotificationDto extends NotificationDto {

  private int callCycle;
  private int imgCycle;
  private int numCycles;
  private int scoreCycle;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    IlluminaNotificationDto other = (IlluminaNotificationDto) obj;
    if (callCycle != other.callCycle) return false;
    if (imgCycle != other.imgCycle) return false;
    if (scoreCycle != other.scoreCycle) return false;
    if (numCycles != other.numCycles) return false;
    return true;
  }

  public int getCallCycle() {
    return callCycle;
  }

  public int getImgCycle() {
    return imgCycle;
  }

  public int getNumCycles() {
    return numCycles;
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.ILLUMINA;
  }

  public int getScoreCycle() {
    return scoreCycle;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + callCycle;
    result = prime * result + imgCycle;
    result = prime * result + scoreCycle;
    result = prime * result + numCycles;
    return result;
  }

  public void setCallCycle(int callCycle) {
    this.callCycle = callCycle;
  }

  public void setImgCycle(int imgCycle) {
    this.imgCycle = imgCycle;
  }

  public void setNumCycles(int numCycles) {
    this.numCycles = numCycles;
  }

  public void setScoreCycle(int scoreCycle) {
    this.scoreCycle = scoreCycle;
  }

  @Override
  public String toString() {
    return "IlluminaNotificationDto [numCycles=" + numCycles + ", imgCycle=" + imgCycle + ", scoreCycle=" + scoreCycle + ", callCycle="
        + callCycle + ", getRunAlias()=" + getRunAlias() + ", getSequencerName()=" + getSequencerName() + ", getContainerSerialNumber()="
        + getContainerSerialNumber() + ", getLaneCount()=" + getLaneCount() + ", getHealthType()=" + getHealthType()
        + ", getSequencerFolderPath()=" + getSequencerFolderPath() + ", isPairedEndRun()=" + isPairedEndRun() + ", getSoftware()="
        + getSoftware() + ", getStartDate()=" + getStartDate() + ", getCompletionDate()=" + getCompletionDate() + "]";
  }

}
