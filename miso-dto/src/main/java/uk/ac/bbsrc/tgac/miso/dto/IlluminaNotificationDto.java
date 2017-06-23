package uk.ac.bbsrc.tgac.miso.dto;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class IlluminaNotificationDto extends NotificationDto {

  private int numCycles;
  private int ImgCycle;
  private int ScoreCycle;
  private int CallCycle;

  public int getNumCycles() {
    return numCycles;
  }

  public void setNumCycles(int numCycles) {
    this.numCycles = numCycles;
  }

  public int getImgCycle() {
    return ImgCycle;
  }

  public void setImgCycle(int imgCycle) {
    ImgCycle = imgCycle;
  }

  public int getScoreCycle() {
    return ScoreCycle;
  }

  public void setScoreCycle(int scoreCycle) {
    ScoreCycle = scoreCycle;
  }

  public int getCallCycle() {
    return CallCycle;
  }

  public void setCallCycle(int callCycle) {
    CallCycle = callCycle;
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.ILLUMINA;
  }

  @Override
  public String toString() {
    return "IlluminaNotificationDto [numCycles=" + numCycles + ", ImgCycle=" + ImgCycle + ", ScoreCycle=" + ScoreCycle + ", CallCycle="
        + CallCycle + ", getRunAlias()=" + getRunAlias() + ", getSequencerName()=" + getSequencerName() + ", getContainerSerialNumber()="
        + getContainerSerialNumber() + ", getLaneCount()=" + getLaneCount() + ", getHealthType()=" + getHealthType()
        + ", getSequencerFolderPath()=" + getSequencerFolderPath() + ", isPairedEndRun()=" + isPairedEndRun() + ", getSoftware()="
        + getSoftware() + ", getStartDate()=" + getStartDate() + ", getCompletionDate()=" + getCompletionDate() + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + CallCycle;
    result = prime * result + ImgCycle;
    result = prime * result + ScoreCycle;
    result = prime * result + numCycles;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    IlluminaNotificationDto other = (IlluminaNotificationDto) obj;
    if (CallCycle != other.CallCycle) return false;
    if (ImgCycle != other.ImgCycle) return false;
    if (ScoreCycle != other.ScoreCycle) return false;
    if (numCycles != other.numCycles) return false;
    return true;
  }

}
