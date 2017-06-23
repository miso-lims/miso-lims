package uk.ac.bbsrc.tgac.miso.dto;

import java.nio.file.Path;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.node.ArrayNode;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public abstract class NotificationDto {

  private String runAlias;
  private Path sequencerFolderPath;
  private String sequencerName;
  private String containerSerialNumber;
  private int laneCount;
  private HealthType healthType;
  private LocalDate startDate;
  private LocalDate completionDate;
  private boolean pairedEndRun;
  private String software;
  private ArrayNode metrics;

  public String getRunAlias() {
    return runAlias;
  }

  public void setRunAlias(String runAlias) {
    this.runAlias = runAlias;
  }

  public String getSequencerName() {
    return sequencerName;
  }

  public void setSequencerName(String sequencerName) {
    this.sequencerName = sequencerName;
  }

  public String getContainerSerialNumber() {
    return containerSerialNumber;
  }

  public void setContainerSerialNumber(String containerId) {
    this.containerSerialNumber = containerId;
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

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getCompletionDate() {
    return completionDate;
  }

  public void setCompletionDate(LocalDate completionDate) {
    this.completionDate = completionDate;
  }

  public ArrayNode getMetrics() {
    return metrics;
  }

  public void setMetrics(ArrayNode metrics) {
    this.metrics = metrics;
  }

  @JsonIgnore
  public abstract PlatformType getPlatformType();

  @Override
  public String toString() {
    return "NotificationDto [runAlias=" + runAlias + ", sequencerFolderPath=" + sequencerFolderPath + ", sequencerName=" + sequencerName
        + ", containerSerialNumber=" + containerSerialNumber + ", laneCount=" + laneCount + ", healthType=" + healthType + ", startDate="
        + startDate + ", completionDate=" + completionDate + ", pairedEndRun=" + pairedEndRun + ", software=" + software + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((completionDate == null) ? 0 : completionDate.hashCode());
    result = prime * result + ((containerSerialNumber == null) ? 0 : containerSerialNumber.hashCode());
    result = prime * result + ((healthType == null) ? 0 : healthType.hashCode());
    result = prime * result + laneCount;
    result = prime * result + ((metrics == null) ? 0 : metrics.hashCode());
    result = prime * result + (pairedEndRun ? 1231 : 1237);
    result = prime * result + ((runAlias == null) ? 0 : runAlias.hashCode());
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
    if (containerSerialNumber == null) {
      if (other.containerSerialNumber != null) return false;
    } else if (!containerSerialNumber.equals(other.containerSerialNumber)) return false;
    if (healthType != other.healthType) return false;
    if (laneCount != other.laneCount) return false;
    if (metrics == null) {
      if (other.metrics != null) return false;
    } else if (!metrics.equals(other.metrics)) return false;
    if (pairedEndRun != other.pairedEndRun) return false;
    if (runAlias == null) {
      if (other.runAlias != null) return false;
    } else if (!runAlias.equals(other.runAlias)) return false;
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
}
