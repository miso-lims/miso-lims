package uk.ac.bbsrc.tgac.miso.core.data.qc;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;

public interface DetailedQcItem {

  public DetailedQcStatus getDetailedQcStatus();

  public void setDetailedQcStatus(DetailedQcStatus detailedQcStatus);

  public String getDetailedQcStatusNote();

  public void setDetailedQcStatusNote(String detailedQcStatusNote);

}
