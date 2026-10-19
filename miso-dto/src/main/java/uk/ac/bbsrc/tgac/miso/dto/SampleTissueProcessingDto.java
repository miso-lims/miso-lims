package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;

@JsonTypeName(value = SampleTissueProcessing.CATEGORY_NAME)
public class SampleTissueProcessingDto extends SampleTissueDto {

  private List<SampleDto> relatedSlides;
  private Long indexFamilyId;
  private Long indexId;
  private Long index2Id;

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
  public Long getIndex2Id() {
    return index2Id;
  }

  public void setIndexId(Long indexId) {
    this.indexId = indexId;
  }
  public void setIndex2Id(Long indexId) {
    this.index2Id = indexId;
  }
}
