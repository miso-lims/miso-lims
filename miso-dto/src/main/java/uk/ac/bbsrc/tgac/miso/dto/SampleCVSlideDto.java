package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.SampleCVSlide;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeName(value = SampleCVSlide.SAMPLE_CLASS_NAME)
public class SampleCVSlideDto extends SampleTissueProcessingDto {

  private Integer cuts;
  private Integer cutsRemaining;
  private Integer discards;
  private Integer thickness;

  public Integer getCuts() {
    return cuts;
  }

  public void setCuts(Integer cuts) {
    this.cuts = cuts;
  }

  public Integer getCutsRemaining() {
    return cutsRemaining;
  }

  public void setCutsRemaining(Integer cutsRemaining) {
    this.cutsRemaining = cutsRemaining;
  }

  public void setCutsRemaining() {
    this.cutsRemaining = getCuts() - getDiscards();
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

}
