package uk.ac.bbsrc.tgac.miso.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "RunOxfordNanopore")
public class OxfordNanoporeRun extends Run {

  private static final long serialVersionUID = 1L;

  private String minKnowVersion;
  private String protocolVersion;

  public OxfordNanoporeRun() {
    super();
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.OXFORDNANOPORE;
  }

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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((minKnowVersion == null) ? 0 : minKnowVersion.hashCode());
    result = prime * result + ((protocolVersion == null) ? 0 : protocolVersion.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    OxfordNanoporeRun other = (OxfordNanoporeRun) obj;
    if (minKnowVersion == null) {
      if (other.minKnowVersion != null)
        return false;
    } else if (!minKnowVersion.equals(other.minKnowVersion))
      return false;
    if (protocolVersion == null) {
      if (other.protocolVersion != null)
        return false;
    } else if (!protocolVersion.equals(other.protocolVersion))
      return false;
    return true;
  }

  @Override
  public String getDeleteType() {
    return "Oxford Nanopore Run";
  }

}
