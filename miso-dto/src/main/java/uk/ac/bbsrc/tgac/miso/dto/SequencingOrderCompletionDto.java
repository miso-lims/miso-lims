package uk.ac.bbsrc.tgac.miso.dto;

public class SequencingOrderCompletionDto {
  private int completed;
  private int failed;
  private String id;
  private String lastUpdated;
  private int loaded;
  private SequencingParametersDto parameters;
  private String containerModelAlias;
  private PoolDto pool;
  private int remaining;
  private int requested;
  private int running;
  private int started;
  private int stopped;
  private int unknown;
  private String description;
  private String purpose;

  public int getCompleted() {
    return completed;
  }

  public int getFailed() {
    return failed;
  }

  public String getId() {
    return id;
  }

  public String getLastUpdated() {
    return lastUpdated;
  }

  public int getLoaded() {
    return loaded;
  }

  public SequencingParametersDto getParameters() {
    return parameters;
  }

  public PoolDto getPool() {
    return pool;
  }

  public int getRemaining() {
    return remaining;
  }

  public int getRequested() {
    return requested;
  }

  public int getRunning() {
    return running;
  }

  public int getStarted() {
    return started;
  }

  public int getStopped() {
    return stopped;
  }

  public int getUnknown() {
    return unknown;
  }

  public void setCompleted(int completed) {
    this.completed = completed;
  }

  public void setFailed(int failed) {
    this.failed = failed;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public void setLoaded(int loaded) {
    this.loaded = loaded;
  }

  public void setParameters(SequencingParametersDto parameters) {
    this.parameters = parameters;
  }

  public String getContainerModelAlias() {
    return containerModelAlias;
  }

  public void setContainerModelAlias(String containerModelAlias) {
    this.containerModelAlias = containerModelAlias;
  }

  public void setPool(PoolDto pool) {
    this.pool = pool;
  }

  public void setRemaining(int remaining) {
    this.remaining = remaining;
  }
  public void setRequested(int requested) {
    this.requested = requested;
  }

  public void setRunning(int running) {
    this.running = running;
  }

  public void setStarted(int started) {
    this.started = started;
  }

  public void setStopped(int stopped) {
    this.stopped = stopped;
  }

  public void setUnknown(int unknown) {
    this.unknown = unknown;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

}
