package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeName(value = SampleStock.CATEGORY_NAME)
public class SampleStockDto extends SampleTissueDto {

  private Double concentration;
  private String strStatus;

  public Double getConcentration() {
    return concentration;
  }

  public String getStrStatus() {
    return strStatus;
  }

  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }

  public void setStrStatus(String strStatus) {
    this.strStatus = strStatus;
  }

}
