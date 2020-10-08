package uk.ac.bbsrc.tgac.miso.dto.dashi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RunLibraryQcTableRequestLibraryDto {

  private String name;
  private Long runId;
  private Integer partition;
  private List<RunLibraryQcTableRequestMetricDto> metrics;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
