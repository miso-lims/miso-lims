package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotRna;

@JsonTypeName(SampleAliquotRna.SUBCATEGORY_NAME)
public class SampleAliquotRnaDto extends SampleAliquotDto implements SampleStockRnaRelative {

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
