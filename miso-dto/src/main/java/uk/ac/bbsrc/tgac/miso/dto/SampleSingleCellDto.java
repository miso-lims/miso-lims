package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;

@JsonTypeName(value = SampleSingleCell.SUBCATEGORY_NAME)
public class SampleSingleCellDto extends SampleTissueProcessingDto implements SampleSingleCellRelative {

  private String initialCellConcentration;
  private String digestion;

  @Override
  public String getInitialCellConcentration() {
    return initialCellConcentration;
  }

  @Override
  public void setInitialCellConcentration(String initialCellConcentration) {
    this.initialCellConcentration = initialCellConcentration;
  }

  @Override
  public String getDigestion() {
    return digestion;
  }

  @Override
  public void setDigestion(String digestion) {
    this.digestion = digestion;
  }

}
