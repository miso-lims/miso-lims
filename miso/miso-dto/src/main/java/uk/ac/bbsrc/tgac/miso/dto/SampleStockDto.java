package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(value = SampleStock.CATEGORY_NAME)
public class SampleStockDto extends SampleTissueDto {

  private String concentration;
  private String strStatus;
  private Boolean dnaseTreated;

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

}
