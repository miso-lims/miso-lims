package uk.ac.bbsrc.tgac.miso.notification.service;

import java.util.Date;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

public class IlluminaRunStatus {
  private String runName;
  private String fullPath;
  private String runinfo;
  private String runparams;
  private HealthType status;
  private String sequencerName;
  private String containerId;
  private Integer laneCount;
  private Integer numCycles;
  private Date startDate;
  private Set<String> kits;

  public Set<String> getKits() {
    return kits;
  }

  public void setKits(Set<String> kits) {
    this.kits = kits;
  }

  public String getRunName() {
    return runName;
  }

  public void setRunName(String runName) {
    this.runName = runName;
  }

  public String getFullPath() {
    return fullPath;
  }

  public void setFullPath(String fullPath) {
    this.fullPath = fullPath;
  }

  public String getRuninfo() {
    return runinfo;
  }

  public void setRuninfo(String runinfo) {
    this.runinfo = runinfo;
  }

  public String getRunparams() {
    return runparams;
  }

  public void setRunparams(String runparams) {
    this.runparams = runparams;
  }

  public HealthType getStatus() {
    return status;
  }

  public void setStatus(HealthType status) {
    this.status = status;
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

  public Integer getLaneCount() {
    return laneCount;
  }

  public void setLaneCount(Integer laneCount) {
    this.laneCount = laneCount;
  }

  public Integer getNumCycles() {
    return numCycles;
  }

  public void setNumCycles(Integer numCycles) {
    this.numCycles = numCycles;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getCompletionDate() {
    return completionDate;
  }

  public void setCompletionDate(Date completionDate) {
    this.completionDate = completionDate;
  }

  private Date completionDate;
}
