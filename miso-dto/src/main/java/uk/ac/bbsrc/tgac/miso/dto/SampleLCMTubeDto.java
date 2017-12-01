package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(value = SampleLCMTube.SAMPLE_CLASS_NAME)
public class SampleLCMTubeDto extends SampleTissueProcessingDto {

  private Integer slidesConsumed;
  private Long parentSlideClassId;

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

}
