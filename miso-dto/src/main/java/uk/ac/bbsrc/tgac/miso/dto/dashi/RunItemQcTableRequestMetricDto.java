package uk.ac.bbsrc.tgac.miso.dto.dashi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RunItemQcTableRequestMetricDto {

  private String title;
  private String thresholdType;
  private double threshold;
  private double threshold2;
  private Double value;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @JsonProperty("threshold_type")
  public String getThresholdType() {
    return thresholdType;
  }

  public void setThresholdType(String thresholdType) {
    this.thresholdType = thresholdType;
  }

  public double getThreshold() {
    return threshold;
  }

  public void setThreshold(double threshold) {
    this.threshold = threshold;
  }

  @JsonProperty(value = "threshold_2")
  public double getThreshold2() {
    return threshold2;
  }

  public void setThreshold2(double threshold2) {
    this.threshold2 = threshold2;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

}
