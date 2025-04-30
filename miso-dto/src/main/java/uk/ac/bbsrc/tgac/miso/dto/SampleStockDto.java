package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;

@JsonTypeName(value = SampleStock.CATEGORY_NAME)
public class SampleStockDto extends SampleTissueDto {

  private Long tissueProcessingClassId;
  private String strStatus;
  private Integer slidesConsumed;
  private Long referenceSlideId;
  private List<SampleDto> relatedSlides;
  private Long indexFamilyId;
  private Long indexId;

  public Long getTissueProcessingClassId() {
    return tissueProcessingClassId;
  }

  public void setTissueProcessingClassId(Long tissueProcessingClassId) {
    this.tissueProcessingClassId = tissueProcessingClassId;
  }

  public String getStrStatus() {
    return strStatus;
  }

  public void setStrStatus(String strStatus) {
    this.strStatus = strStatus;
  }

  public Integer getSlidesConsumed() {
    return slidesConsumed;
  }

  public void setSlidesConsumed(Integer slidesConsumed) {
    this.slidesConsumed = slidesConsumed;
  }

  public Long getReferenceSlideId() {
    return referenceSlideId;
  }

  public void setReferenceSlideId(Long referenceSlideId) {
    this.referenceSlideId = referenceSlideId;
  }

  public List<SampleDto> getRelatedSlides() {
    return relatedSlides;
  }

  public void setRelatedSlides(List<SampleDto> relatedSlides) {
    this.relatedSlides = relatedSlides;
  }

  public Long getIndexFamilyId() {
    return indexFamilyId;
  }

  public void setIndexFamilyId(Long indexFamilyId) {
    this.indexFamilyId = indexFamilyId;
  }

  public Long getIndexId() {
    return indexId;
  }

  public void setIndexId(Long indexId) {
    this.indexId = indexId;
  }

}
