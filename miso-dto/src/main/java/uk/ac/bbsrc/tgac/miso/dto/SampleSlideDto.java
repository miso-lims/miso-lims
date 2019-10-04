package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;

@JsonTypeName(value = SampleSlide.SUBCATEGORY_NAME)
public class SampleSlideDto extends SampleTissueProcessingDto {

  private Integer initialSlides;
  private Integer slides;
  private Integer discards;
  private Integer thickness;
  private Long stainId;

  public Integer getInitialSlides() {
    return initialSlides;
  }

  public void setInitialSlides(Integer initialSlides) {
    this.initialSlides = initialSlides;
  }

  public Integer getSlides() {
    return slides;
  }

  public void setSlides(Integer slides) {
    this.slides = slides;
  }

  public Integer getDiscards() {
    return discards;
  }

  public void setDiscards(Integer discards) {
    this.discards = discards;
  }

  public Integer getThickness() {
    return thickness;
  }

  public void setThickness(Integer thickness) {
    this.thickness = thickness;
  }

  public Long getStainId() {
    return stainId;
  }

  public void setStainId(Long stainId) {
    this.stainId = stainId;
  }

}
