package uk.ac.bbsrc.tgac.miso.notification.service;

import java.util.Date;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import nki.core.MetrixContainer;
import nki.objects.Summary;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

public class IlluminaRunMessage {
  private boolean baseCallComplete;
  private Date completionDate;
  private String containerId;
  private String fullPath;
  private HealthType health = HealthType.Unknown;
  private Set<String> kits;
  private Integer laneCount;
  @JsonIgnore
  private MetrixContainer metrixContainer;
  @JsonIgnore
  private String metrixJson;
  private boolean numberedBaseCallsComplete;
  private Integer numCycles;
  private Integer numReads;
  private String runinfo;
  private String runName;
  private String runparams;
  private Boolean seenLastCycle;
  private String sequencerName;
  private Date startDate;
  private String status;

  @JsonIgnore
  private Summary summary = new Summary();

  public Date getCompletionDate() {
    return completionDate;
  }

  public String getContainerId() {
    return containerId;
  }

  public String getFullPath() {
    return fullPath;
  }

  public HealthType getHealth() {
    return health;
  }

  public Set<String> getKits() {
    return kits;
  }

  public Integer getLaneCount() {
    return laneCount;
  }

  public MetrixContainer getMetrixContainer() {
    return metrixContainer;
  }

  public String getMetrixJson() {
    return metrixJson;
  }

  public Integer getNumCycles() {
    return numCycles;
  }

  public Integer getNumReads() {
    return numReads;
  }

  public String getRuninfo() {
    return runinfo;
  }

  public String getRunName() {
    return runName;
  }

  public String getRunparams() {
    return runparams;
  }

  public String getSequencerName() {
    return sequencerName;
  }

  public Date getStartDate() {
    return startDate;
  }

  public String getStatus() {
    return status;
  }

  public Summary getSummary() {
    return summary;
  }

  public Boolean hasSeenLastCycle() {
    return seenLastCycle;
  }

  public boolean isBaseCallComplete() {
    return baseCallComplete;
  }

  public boolean isNumberedBaseCallsComplete() {
    return numberedBaseCallsComplete;
  }

  public void setBaseCallComplete(boolean baseCallComplete) {
    this.baseCallComplete = baseCallComplete;
  }

  public void setCompletionDate(Date completionDate) {
    this.completionDate = completionDate;
  }

  public void setContainerId(String containerId) {
    this.containerId = containerId;
  }

  public void setFullPath(String fullPath) {
    this.fullPath = fullPath;
  }

  public void setHealth(HealthType health) {
    this.health = health;
  }

  public void setKits(Set<String> kits) {
    this.kits = kits;
  }

  public void setLaneCount(Integer laneCount) {
    this.laneCount = laneCount;
  }

  public void setMetrixContainer(MetrixContainer metrix) {
    this.metrixContainer = metrix;
  }

  public void setMetrixJson(String metrixJson) {
    this.metrixJson = metrixJson;
  }

  public void setNumberedBaseCallsComplete(boolean numberedBaseCallsComplete) {
    this.numberedBaseCallsComplete = numberedBaseCallsComplete;
  }

  public void setNumCycles(Integer numCycles) {
    this.numCycles = numCycles;
  }

  public void setNumReads(Integer numReads) {
    this.numReads = numReads;
  }

  public void setRuninfo(String runinfo) {
    this.runinfo = runinfo;
  }

  public void setRunName(String runName) {
    this.runName = runName;
  }

  public void setRunparams(String runparams) {
    this.runparams = runparams;
  }

  public void setSeenLastCycle(Boolean seenLastCycle) {
    this.seenLastCycle = seenLastCycle;
  }

  public void setSequencerName(String sequencerName) {
    this.sequencerName = sequencerName;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setSummary(Summary summary) {
    this.summary = summary;
  }
}
