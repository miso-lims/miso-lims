package uk.ac.bbsrc.tgac.miso.dto;

public class PacBioNotificationDto extends NotificationDto {

  private int movieDurationInSec;

  public int getMovieDurationInSec() {
    return movieDurationInSec;
  }

  public void setMovieDurationInSec(int movieDurationInSec) {
    this.movieDurationInSec = movieDurationInSec;
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

  @Override
  public String toString() {
    return "PacBioNotificationDto [movieDurationInSec=" + movieDurationInSec + ", getRunName()=" + getRunName() + ", getSequencerName()="
        + getSequencerName() + ", getCompletionDate()=" + getCompletionDate() + ", getStartDate()=" + getStartDate() + ", getContainerId()="
        + getContainerId() + ", getLaneCount()=" + getLaneCount() + ", getHealthType()=" + getHealthType() + ", getSequencerFolderPath()="
        + getSequencerFolderPath() + ", isPairedEndRun()=" + isPairedEndRun() + ", getSoftware()=" + getSoftware() + "]";
  }

}
