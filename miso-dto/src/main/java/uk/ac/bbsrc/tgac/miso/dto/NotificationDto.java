package uk.ac.bbsrc.tgac.miso.dto;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public abstract class NotificationDto {

  private String runName;
  private Path sequencerFolderPath;
  private String sequencerName;
  private String containerId;
  private int laneCount;
  private HealthType healthType;
  private String startDate;
  private String completionDate;
  private boolean pairedEndRun;
  private String software;

  public String getRunName() {
    return runName;
  }

  public void setRunName(String runName) {
    this.runName = runName;
  }

  public String getSequencerName() {
    return sequencerName;
  }

  public void setSequencerName(String sequencerName) {
    this.sequencerName = sequencerName;
  }

  public String getContainerId() {
    return containerId;
  }

  public void setContainerId(String containerId) {
    this.containerId = containerId;
  }

  public int getLaneCount() {
    return laneCount;
  }

  public void setLaneCount(int laneCount) {
    this.laneCount = laneCount;
  }

  public HealthType getHealthType() {
    return healthType;
  }

  public void setHealthType(HealthType healthType) {
    this.healthType = healthType;
  }

  public Path getSequencerFolderPath() {
    return sequencerFolderPath;
  }

  public void setSequencerFolderPath(Path sequencerFolderPath) {
    this.sequencerFolderPath = sequencerFolderPath;
  }

  public boolean isPairedEndRun() {
    return pairedEndRun;
  }

  public void setPairedEndRun(boolean pairedEndRun) {
    this.pairedEndRun = pairedEndRun;
  }

  public String getSoftware() {
    return software;
  }

  public void setSoftware(String software) {
    this.software = software;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getCompletionDate() {
    return completionDate;
  }

  public void setCompletionDate(String completionDate) {
    this.completionDate = completionDate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((completionDate == null) ? 0 : completionDate.hashCode());
    result = prime * result + ((containerId == null) ? 0 : containerId.hashCode());
    result = prime * result + ((healthType == null) ? 0 : healthType.hashCode());
    result = prime * result + laneCount;
    result = prime * result + (pairedEndRun ? 1231 : 1237);
    result = prime * result + ((runName == null) ? 0 : runName.hashCode());
    result = prime * result + ((sequencerFolderPath == null) ? 0 : sequencerFolderPath.hashCode());
    result = prime * result + ((sequencerName == null) ? 0 : sequencerName.hashCode());
    result = prime * result + ((software == null) ? 0 : software.hashCode());
    result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    NotificationDto other = (NotificationDto) obj;
    if (completionDate == null) {
      if (other.completionDate != null) return false;
    } else if (!completionDate.equals(other.completionDate)) return false;
    if (containerId == null) {
      if (other.containerId != null) return false;
    } else if (!containerId.equals(other.containerId)) return false;
    if (healthType != other.healthType) return false;
    if (laneCount != other.laneCount) return false;
    if (pairedEndRun != other.pairedEndRun) return false;
    if (runName == null) {
      if (other.runName != null) return false;
    } else if (!runName.equals(other.runName)) return false;
    if (sequencerFolderPath == null) {
      if (other.sequencerFolderPath != null) return false;
    } else if (!sequencerFolderPath.equals(other.sequencerFolderPath)) return false;
    if (sequencerName == null) {
      if (other.sequencerName != null) return false;
    } else if (!sequencerName.equals(other.sequencerName)) return false;
    if (software == null) {
      if (other.software != null) return false;
    } else if (!software.equals(other.software)) return false;
    if (startDate == null) {
      if (other.startDate != null) return false;
    } else if (!startDate.equals(other.startDate)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "NotificationDto [runName=" + runName + ", sequencerFolderPath=" + sequencerFolderPath + ", sequencerName=" + sequencerName
        + ", containerId=" + containerId + ", laneCount=" + laneCount + ", healthType=" + healthType + ", completionDate=" + completionDate
        + ", startDate=" + startDate + ", pairedEndRun=" + pairedEndRun + ", software=" + software + "]";
  }

}
