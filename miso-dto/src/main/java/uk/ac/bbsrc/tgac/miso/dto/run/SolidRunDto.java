package uk.ac.bbsrc.tgac.miso.dto.run;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "Solid")
public class SolidRunDto extends RunDto {

  private Boolean pairedEnd;

  public Boolean getPairedEnd() {
    return pairedEnd;
  }

  public void setPairedEnd(Boolean pairedEnd) {
    this.pairedEnd = pairedEnd;
  }

}
