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
  private String percentTumour;
  private String percentNecrosis;
  private String markedArea;
  private String markedAreaPercentTumour;

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

  public String getPercentTumour() {
    return percentTumour;
  }

  public void setPercentTumour(String percentTumour) {
    this.percentTumour = percentTumour;
  }

  public String getPercentNecrosis() {
    return percentNecrosis;
  }

  public void setPercentNecrosis(String percentNecrosis) {
    this.percentNecrosis = percentNecrosis;
  }

  public String getMarkedArea() {
    return markedArea;
  }

  public void setMarkedArea(String markedArea) {
    this.markedArea = markedArea;
  }

  public String getMarkedAreaPercentTumour() {
    return markedAreaPercentTumour;
  }

  public void setMarkedAreaPercentTumour(String markedAreaPercentTumour) {
    this.markedAreaPercentTumour = markedAreaPercentTumour;
  }

}
