package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SampleQcDto extends QcDto {

  private Double results;
  private Long sampleId;

  public Double getResults() {
    return results;
  }

  public void setResults(Double results) {
    this.results = results;
  }

  public Long getSampleId() {
    return sampleId;
  }

  public void setSampleId(Long sampleId) {
    this.sampleId = sampleId;
  }
}
