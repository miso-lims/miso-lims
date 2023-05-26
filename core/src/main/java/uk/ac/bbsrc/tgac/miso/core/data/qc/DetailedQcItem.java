package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.time.LocalDate;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;

public interface DetailedQcItem {

  public DetailedQcStatus getDetailedQcStatus();

  public void setDetailedQcStatus(DetailedQcStatus detailedQcStatus);

  public String getDetailedQcStatusNote();

  public void setDetailedQcStatusNote(String detailedQcStatusNote);

  public User getQcUser();

  public void setQcUser(User qcUser);

  public LocalDate getQcDate();

  public void setQcDate(LocalDate qcDate);

}
