package uk.ac.bbsrc.tgac.miso.dto;

public class DeliverableDto {
  private Long id;
  private String name;
  private boolean analysisReviewRequired = true;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isAnalysisReviewRequired() {
    return analysisReviewRequired;
  }

  public void setAnalysisReviewRequired(boolean analysisReviewRequired) {
    this.analysisReviewRequired = analysisReviewRequired;
  }
}
