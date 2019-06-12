package uk.ac.bbsrc.tgac.miso.dto;

public class LibraryDesignDto {
  private Long id;
  private String name;
  private Long designCodeId;
  private String designCodeLabel;
  private Long sampleClassId;
  private String sampleClassAlias;
  private Long selectionId;
  private String selectionName;
  private Long strategyId;
  private String strategyName;

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

  public Long getDesignCodeId() {
    return designCodeId;
  }

  public void setDesignCodeId(Long designCodeId) {
    this.designCodeId = designCodeId;
  }

  public String getDesignCodeLabel() {
    return designCodeLabel;
  }

  public void setDesignCodeLabel(String designCodeLabel) {
    this.designCodeLabel = designCodeLabel;
  }

  public Long getSampleClassId() {
    return sampleClassId;
  }

  public void setSampleClassId(Long sampleClassId) {
    this.sampleClassId = sampleClassId;
  }

  public String getSampleClassAlias() {
    return sampleClassAlias;
  }

  public void setSampleClassAlias(String sampleClassAlias) {
    this.sampleClassAlias = sampleClassAlias;
  }

  public Long getSelectionId() {
    return selectionId;
  }

  public void setSelectionId(Long selectionId) {
    this.selectionId = selectionId;
  }

  public String getSelectionName() {
    return selectionName;
  }

  public void setSelectionName(String selectionName) {
    this.selectionName = selectionName;
  }

  public Long getStrategyId() {
    return strategyId;
  }

  public void setStrategyId(Long strategyId) {
    this.strategyId = strategyId;
  }

  public String getStrategyName() {
    return strategyName;
  }

  public void setStrategyName(String strategyName) {
    this.strategyName = strategyName;
  }
}
