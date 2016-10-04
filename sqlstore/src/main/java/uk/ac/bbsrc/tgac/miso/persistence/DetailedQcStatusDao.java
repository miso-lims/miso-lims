package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;

public interface DetailedQcStatusDao {

  List<DetailedQcStatus> getDetailedQcStatus();

  DetailedQcStatus getDetailedQcStatus(Long id);

  Long addDetailedQcStatus(DetailedQcStatus detailedQcStatus);

  void deleteDetailedQcStatus(DetailedQcStatus detailedQcStatus);

  void update(DetailedQcStatus detailedQcStatus);

}