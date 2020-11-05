package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleStockRna;

@JsonTypeName(SampleStockRna.SUBCATEGORY_NAME)
public class SampleStockRnaDto extends SampleStockDto implements SampleStockRnaRelative {

  private Boolean dnaseTreated;

  @Override
  public Boolean getDnaseTreated() {
    return dnaseTreated;
  }

  @Override
  public void setDnaseTreated(Boolean dnaseTreated) {
    this.dnaseTreated = dnaseTreated;
  }

}
