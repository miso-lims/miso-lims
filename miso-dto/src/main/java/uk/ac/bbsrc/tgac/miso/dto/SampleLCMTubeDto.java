package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeName(value = SampleLCMTube.SAMPLE_CLASS_NAME)
public class SampleLCMTubeDto extends SampleTissueProcessingDto {

  private Integer cutsConsumed;
  
  public Integer getCutsConsumed() {
    return cutsConsumed;
  }

  public void setCutsConsumed(Integer cutsConsumed) {
    this.cutsConsumed = cutsConsumed;
  }

}
