package uk.ac.bbsrc.tgac.miso.dto.dashi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RunLibraryQcTableRequestLibraryDto {

  private long id;
  private Long runId;
  private Integer partition;
  private List<RunLibraryQcTableRequestMetricDto> metrics;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @JsonProperty("run_id")
  public Long getRunId() {
    return runId;
  }

  public void setRunId(Long runId) {
    this.runId = runId;
  }

  public Integer getPartition() {
    return partition;
  }

  public void setPartition(Integer partition) {
    this.partition = partition;
  }

  public List<RunLibraryQcTableRequestMetricDto> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<RunLibraryQcTableRequestMetricDto> metrics) {
    this.metrics = metrics;
  }

}
