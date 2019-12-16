package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;

@JsonTypeName(value = SampleTissuePiece.SUBCATEGORY_NAME)
public class SampleTissuePieceDto extends SampleTissueProcessingDto {

  private Integer slidesConsumed;
  private Long parentSlideClassId;
  private Long tissuePieceTypeId;
  private Long referenceSlideId;
  private List<SampleDto> relatedSlides;

  public Integer getSlidesConsumed() {
    return slidesConsumed;
  }

  public void setSlidesConsumed(Integer slidesConsumed) {
    this.slidesConsumed = slidesConsumed;
  }

  public Long getParentSlideClassId() {
    return parentSlideClassId;
  }

  public void setParentSlideClassId(Long parentSlideClassId) {
    this.parentSlideClassId = parentSlideClassId;
  }

  public Long getTissuePieceTypeId() {
    return tissuePieceTypeId;
  }

  public void setTissuePieceTypeId(Long tissuePieceTypeId) {
    this.tissuePieceTypeId = tissuePieceTypeId;
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

}
