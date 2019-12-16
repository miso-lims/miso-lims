package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;

@JsonTypeName(value = SampleStock.CATEGORY_NAME)
public class SampleStockDto extends SampleTissueDto {

  private Long tissueProcessingClassId;
  private String concentration;
  private String strStatus;
  private Boolean dnaseTreated;
  private Long referenceSlideId;
  private List<SampleDto> relatedSlides;

  public Long getTissueProcessingClassId() {
    return tissueProcessingClassId;
  }

  public void setTissueProcessingClassId(Long tissueProcessingClassId) {
    this.tissueProcessingClassId = tissueProcessingClassId;
  }

  @Override
  public String getConcentration() {
    return concentration;
  }

  public String getStrStatus() {
    return strStatus;
  }

  @Override
  public void setConcentration(String concentration) {
    this.concentration = concentration;
  }

  public void setStrStatus(String strStatus) {
    this.strStatus = strStatus;
  }

  public Boolean getDnaseTreated() {
    return dnaseTreated;
  }

  public void setDnaseTreated(Boolean dnaseTreated) {
    this.dnaseTreated = dnaseTreated;
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
