package uk.ac.bbsrc.tgac.miso.dto;

public class PoolOrderCompletionDto {
  private int completed;
  private int failed;
  private String lastUpdated;
  private SequencingParametersDto parameters;
  private PoolDto pool;
  private int remaining;
  private int requested;
  private int running;
  private int started;
  private int stopped;
  private int unknown;

  public int getCompleted() {
    return completed;
  }

  public int getFailed() {
    return failed;
  }

  public String getLastUpdated() {
    return lastUpdated;
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

  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public void setParameters(SequencingParametersDto parameters) {
    this.parameters = parameters;
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

}
