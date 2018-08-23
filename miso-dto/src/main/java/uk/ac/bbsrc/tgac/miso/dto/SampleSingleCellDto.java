package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(value = SampleSingleCell.SAMPLE_CLASS_NAME)
public class SampleSingleCellDto extends SampleTissueProcessingDto {

  private String initialCellConcentration;
  private String digestion;

  public String getInitialCellConcentration() {
    return initialCellConcentration;
  }

  public void setInitialCellConcentration(String initialCellConcentration) {
    this.initialCellConcentration = initialCellConcentration;
  }

  public String getDigestion() {
    return digestion;
  }

  public void setDigestion(String digestion) {
    this.digestion = digestion;
  }

}
