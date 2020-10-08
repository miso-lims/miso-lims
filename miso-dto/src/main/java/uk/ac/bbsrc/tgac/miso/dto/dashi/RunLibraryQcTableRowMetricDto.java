package uk.ac.bbsrc.tgac.miso.dto.dashi;

public class RunLibraryQcTableRowMetricDto {

  private String title;
  private String thresholdType;
  private double threshold;
  private double value;

  public static RunLibraryQcTableRowMetricDto fromRequestDto(RunLibraryQcTableRequestMetricDto from) {
    RunLibraryQcTableRowMetricDto to = new RunLibraryQcTableRowMetricDto();
    to.setTitle(from.getTitle());
    to.setThresholdType(from.getThresholdType());
    to.setThreshold(from.getThreshold());
    to.setValue(from.getValue());
    return to;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

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

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

}
