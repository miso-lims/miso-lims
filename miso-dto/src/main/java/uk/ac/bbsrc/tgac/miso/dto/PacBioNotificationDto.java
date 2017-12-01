package uk.ac.bbsrc.tgac.miso.dto;

import java.util.Map;
import java.util.Optional;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class PacBioNotificationDto extends NotificationDto {

  private Map<String, String> poolNames;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    PacBioNotificationDto other = (PacBioNotificationDto) obj;
    if (poolNames == null) {
      if (other.poolNames != null) return false;
    } else if (!poolNames.equals(other.poolNames)) return false;
    return true;
  }

  @Override
  public Optional<String> getLaneContents(int lane) {
    String wellName = String.format("%c%2d", 'A' + (lane % 8), lane / 8);
    return poolNames.containsKey(wellName) ? Optional.of(poolNames.get(wellName)) : Optional.empty();
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.PACBIO;
  }

  public Map<String, String> getPoolNames() {
    return poolNames;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((poolNames == null) ? 0 : poolNames.hashCode());
    return result;
  }

  public void setPoolNames(Map<String, String> poolNames) {
    this.poolNames = poolNames;
  }

  @Override
  public String toString() {
    return "PacBioNotificationDto [poolNames=" + poolNames + ", getRunAlias()=" + getRunAlias() + ", getSequencerName()="
        + getSequencerName() + ", getContainerSerialNumber()=" + getContainerSerialNumber() + ", getLaneCount()=" + getLaneCount()
        + ", getHealthType()=" + getHealthType() + ", getSequencerFolderPath()=" + getSequencerFolderPath() + ", isPairedEndRun()="
        + isPairedEndRun() + ", getSoftware()=" + getSoftware() + ", getStartDate()=" + getStartDate() + ", getCompletionDate()="
        + getCompletionDate() + "]";
  }

}
