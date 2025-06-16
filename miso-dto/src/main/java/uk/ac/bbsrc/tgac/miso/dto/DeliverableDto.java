package uk.ac.bbsrc.tgac.miso.dto;

public class DeliverableDto {
  private Long id;
  private String name;
  private Long categoryId;
  private String categoryName;
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

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public boolean isAnalysisReviewRequired() {
    return analysisReviewRequired;
  }

  public void setAnalysisReviewRequired(boolean analysisReviewRequired) {
    this.analysisReviewRequired = analysisReviewRequired;
  }
}
