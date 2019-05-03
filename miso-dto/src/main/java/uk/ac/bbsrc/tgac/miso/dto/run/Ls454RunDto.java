package uk.ac.bbsrc.tgac.miso.dto.run;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "LS454")
public class Ls454RunDto extends RunDto {

  private Integer cycles;
  private Boolean pairedEnd;

  public Integer getCycles() {
    return cycles;
  }

  public void setCycles(Integer cycles) {
    this.cycles = cycles;
  }

  public Boolean getPairedEnd() {
    return pairedEnd;
  }

  public void setPairedEnd(Boolean pairedEnd) {
    this.pairedEnd = pairedEnd;
  }

}
