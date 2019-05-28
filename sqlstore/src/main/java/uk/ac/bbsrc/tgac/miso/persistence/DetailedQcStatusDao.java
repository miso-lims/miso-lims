package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;

public interface DetailedQcStatusDao {

  List<DetailedQcStatus> list();

  DetailedQcStatus get(Long id);

  DetailedQcStatus getByDescription(String description);

  long create(DetailedQcStatus detailedQcStatus);

  long update(DetailedQcStatus detailedQcStatus);

  long getUsage(DetailedQcStatus detailedQcStatus);

}