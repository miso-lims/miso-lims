package uk.ac.bbsrc.tgac.miso.dto;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class SolidNotificationDto extends NotificationDto {
  @Override
  public PlatformType getPlatformType() {
    return PlatformType.SOLID;
  }

  @Override
  public String toString() {
    return "SolidNotificationDto [getRunAlias()=" + getRunAlias() + ", getSequencerName()=" + getSequencerName()
        + ", getContainerSerialNumber()=" + getContainerSerialNumber() + ", getLaneCount()=" + getLaneCount() + ", getHealthType()="
        + getHealthType() + ", getSequencerFolderPath()=" + getSequencerFolderPath() + ", isPairedEndRun()=" + isPairedEndRun()
        + ", getSoftware()=" + getSoftware() + ", getStartDate()=" + getStartDate() + ", getCompletionDate()=" + getCompletionDate()
        + ", getMetrics()=" + getMetrics() + "]";
  }

}
