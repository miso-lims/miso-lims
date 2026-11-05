package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;

@JsonTypeName(value = SampleTissueProcessing.CATEGORY_NAME)
public class SampleTissueProcessingDto extends SampleTissueDto {

  private List<SampleDto> relatedSlides;
  private Long indexFamilyId;
  private Long index1Id;
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

  public Long getIndex1Id() {
    return index1Id;
  }
  public Long getIndex2Id() {
    return index2Id;
  }

  public void setIndex1Id(Long indexId) {
    this.index1Id = indexId;
  }
  public void setIndex2Id(Long indexId) {
    this.index2Id = indexId;
  }
}
