package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;

@JsonTypeName(value = SampleAliquot.CATEGORY_NAME)
public class SampleAliquotDto extends SampleStockDto {

  private Long samplePurposeId;
  @JsonIgnore
  private Long stockClassId;
  private Long parentAliquotClassId;

  public Long getSamplePurposeId() {
    return samplePurposeId;
  }

  public void setSamplePurposeId(Long samplePurposeId) {
    this.samplePurposeId = samplePurposeId;
  }

  public Long getStockClassId() {
    return stockClassId;
  }

  public void setStockClassId(Long stockClassId) {
    this.stockClassId = stockClassId;
  }

  public Long getParentAliquotClassId() {
    return parentAliquotClassId;
  }

  public void setParentAliquotClassId(Long parentAliquotClassId) {
    this.parentAliquotClassId = parentAliquotClassId;
  }

}
