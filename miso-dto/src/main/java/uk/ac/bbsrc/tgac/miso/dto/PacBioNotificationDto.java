package uk.ac.bbsrc.tgac.miso.dto;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class PacBioNotificationDto extends NotificationDto {

  private int movieDurationInSec;

  public int getMovieDurationInSec() {
    return movieDurationInSec;
  }

  public void setMovieDurationInSec(int movieDurationInSec) {
    this.movieDurationInSec = movieDurationInSec;
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.PACBIO;
  }

  @Override
  public String toString() {
    return "PacBioNotificationDto [movieDurationInSec=" + movieDurationInSec + ", getRunAlias()=" + getRunAlias() + ", getSequencerName()="
        + getSequencerName() + ", getContainerSerialNumber()=" + getContainerSerialNumber() + ", getLaneCount()=" + getLaneCount()
        + ", getHealthType()=" + getHealthType() + ", getSequencerFolderPath()=" + getSequencerFolderPath() + ", isPairedEndRun()="
        + isPairedEndRun() + ", getSoftware()=" + getSoftware() + ", getStartDate()=" + getStartDate() + ", getCompletionDate()="
        + getCompletionDate() + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + movieDurationInSec;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    PacBioNotificationDto other = (PacBioNotificationDto) obj;
    if (movieDurationInSec != other.movieDurationInSec) return false;
    return true;
  }

}
