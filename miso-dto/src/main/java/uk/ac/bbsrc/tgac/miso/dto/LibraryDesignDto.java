package uk.ac.bbsrc.tgac.miso.dto;

public class LibraryDesignDto {
  private long designCodeId;
  private long id;
  private String name;
  private long sampleClassId;
  private long selectionId;
  private long strategyId;

  public long getDesignCodeId() {
    return designCodeId;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public long getSampleClassId() {
    return sampleClassId;
  }

  public long getSelectionId() {
    return selectionId;
  }

  public long getStrategyId() {
    return strategyId;
  }

  public void setDesignCodeId(long designCodeId) {
    this.designCodeId = designCodeId;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSampleClassId(long sampleClassId) {
    this.sampleClassId = sampleClassId;
  }

  public void setSelectionId(long selectionId) {
    this.selectionId = selectionId;
  }

  public void setStrategyId(long strategyId) {
    this.strategyId = strategyId;
  }
}
