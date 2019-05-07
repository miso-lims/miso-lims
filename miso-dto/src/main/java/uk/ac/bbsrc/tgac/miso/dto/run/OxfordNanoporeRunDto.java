package uk.ac.bbsrc.tgac.miso.dto.run;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "OxfordNanopore")
public class OxfordNanoporeRunDto extends RunDto {

  private String minKnowVersion;
  private String protocolVersion;

  public String getMinKnowVersion() {
    return minKnowVersion;
  }

  public void setMinKnowVersion(String minKnowVersion) {
    this.minKnowVersion = minKnowVersion;
  }

  public String getProtocolVersion() {
    return protocolVersion;
  }

  public void setProtocolVersion(String protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

}
