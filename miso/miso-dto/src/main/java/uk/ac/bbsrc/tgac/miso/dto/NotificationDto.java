package uk.ac.bbsrc.tgac.miso.dto;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import uk.ac.bbsrc.tgac.miso.core.data.GetLaneContents;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * A "run" as seen by Run Scanner
 *
 * Run Scanner collects information about runs and reports them to MISO for incorporation. This class is distinct from {@link RunDto}
 * because there is not a direct mapping between runs in MISO and runs as they can be detected on disk ("notifications"). First, runs have
 * added information such as change logs, security profiles, and notes that notifications should never include. Second, the notification is
 * actually a composite of run, container, and partition information and this has to be expanded when written to MISO in a non-trivial way.
 * Finally, notifications contain best-effort information gleaned from the sequencer output, but it is not always possible to correctly
 * detect information; if a human changes certain properties of a run, it should not be overwritten by automation.
 */
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class NotificationDto implements Predicate<SequencingParameters>, GetLaneContents {

  private String runAlias;
  private String sequencerFolderPath;
  private String sequencerName;
  private String containerSerialNumber;
  private int laneCount;
  private HealthType healthType;
  private LocalDateTime startDate;
  private LocalDateTime completionDate;
  private boolean pairedEndRun;
  private String software;
  private String metrics;

  /**
   * Get the alias of the run.
   * 
   * This is usually a derivative of the directory name the run output is stored in. Alias must be unique in MISO and notifications will be
   * matched to runs by alias.
   */
  public String getRunAlias() {
    return runAlias;
  }

  public void setRunAlias(String runAlias) {
    this.runAlias = runAlias;
  }

  /**
   * Get the name of the sequencer/instrument
   * 
   * This must match to the names of sequencer as input in MISO. If a notification has no matching sequencer, it will be discarded during
   * processing. How the name of the sequencer is detected varies by the instrument/platform used in Run Scanner.
   */
  public String getSequencerName() {
    return sequencerName;
  }

  public void setSequencerName(String sequencerName) {
    this.sequencerName = sequencerName;
  }

  /**
   * Get the unique name of the container.
   * 
   * This is usually the unique barcode of the container. Although MISO can handle multiple containers per run, Run Scanner currently does
   * not. If there is no logical definition of container for the platform, this should be the same as the run's alias.
   */
  public String getContainerSerialNumber() {
    return containerSerialNumber;
  }

  public void setContainerSerialNumber(String containerId) {
    this.containerSerialNumber = containerId;
  }

  /**
   * Get the number of partitions in this run.
   * 
   * If MISO has to create a new container for this run, it will be pre-sized to this number of lanes. If, at a later time, the run scanner
   * reports a different container size, any update from Run Scanner will be ignored by MISO.
   */
  public int getLaneCount() {
    return laneCount;
  }

  public void setLaneCount(int laneCount) {
    this.laneCount = laneCount;
  }

  /**
   * Get the status of the run.
   * 
   * This the current status of the run. This may be impossible to correctly detect depending on the platform. MISO will not record the
   * completion date if the health is not a type that is "done". If the status has been changed by a human, any updates provided in
   * notifications are
   * ignored.
   * 
   * @see HealthType#isDone()
   */
  public HealthType getHealthType() {
    return healthType;
  }

  public void setHealthType(HealthType healthType) {
    this.healthType = healthType;
  }

  /**
   * Get the file path to the sequencing output
   * 
   * MISO only presents this data to the user and possibly to a downstream analysis pipeline. It will never attempt to read from this
   * directory.
   */
  public String getSequencerFolderPath() {
    return sequencerFolderPath;
  }

  public void setSequencerFolderPath(String sequencerFolderPath) {
    this.sequencerFolderPath = sequencerFolderPath;
  }

  /**
   * Check if the run is paired end
   * 
   * This is recorded for some platform types. If the target platform type does not support paired end runs, this field will be ignored.
   */
  public boolean isPairedEndRun() {
    return pairedEndRun;
  }

  public void setPairedEndRun(boolean pairedEndRun) {
    this.pairedEndRun = pairedEndRun;
  }

  /**
   * Get the platform-specific software name/version of the sequencer or the library used to read the on-disk output.
   */
  public String getSoftware() {
    return software;
  }

  public void setSoftware(String software) {
    this.software = software;
  }

  /**
   * Get the time when the sequencer run started, if known.
   */
  public LocalDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  /**
   * Get the time when the sequencer run completed/failed/stopped.
   * 
   * This is only read if {@link HealthType#isDone()}. If the run is not done, the completion date of the run is set to null regardless of
   * the contents of this field.
   */
  public LocalDateTime getCompletionDate() {
    return completionDate;
  }

  public void setCompletionDate(LocalDateTime completionDate) {
    this.completionDate = completionDate;
  }

  /**
   * Gets a JSON-encoded array of objects containing information for display purposes.
   * 
   * This provides the data used to generate metrics on the front end. Each metric object has a "type" property that determines how the
   * front end will display the information, if at all. The rest of the object's properties are determined on a "type" by "type" basis and
   * the format expected is determined by the JavaScript front-end. Other than checking for valid JSON, MISO only proxies this data.
   * 
   * The metrics do not overwrite the existing metrics in the MISO database. They are merged where new metrics of the same "type" as an
   * existing metrics will overwrite it, but an existing metric will not be deleted.
   */
  public String getMetrics() {
    return metrics;
  }

  public void setMetrics(String metrics) {
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

  /**
   * Determine the identification barcode of a pool in a lane
   * 
   * For some instruments, a sample sheet is provided. This data can be provided to MISO to automatically populate the pools in the
   * partitions.
   * If multiple pools can be provided for a single partition, the correct behaviour is to return empty.
   * 
   * @param lane, the lane of interest, [0, {{@link #getLaneCount()}
   */
  @Override
  public Optional<String> getLaneContents(int lane) {
    return Optional.empty();
  }

  /**
   * Check if the sequencing parameters provided match this run's output
   * 
   * Some platforms have complicated programmable sequencing conditions, as defined in {@link SequencingParameters}. This function
   * determines if candidate sequencing parameters match the configuration of this notification.
   * 
   * This is a best-effort attempt and the safe behaviour is to return true if unsure. The parameters tested will be constrained by the
   * sequencing platform. If there is exactly one match, then MISO will assign that as the correct parameters. If there are multiple
   * candidates, it will not assign parameters automatically. If a human has changed the parameters, they will not be changed again.
   */
  @Override
  public boolean test(SequencingParameters params) {
    return params.getPlatform().getPlatformType() == getPlatformType();
  }
}
