package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;

public interface QcPassedDetailDao {

  List<QcPassedDetail> getQcPassedDetails();

  QcPassedDetail getQcPassedDetails(Long id);

  Long addQcPassedDetails(QcPassedDetail qcPassedDetail);

  void deleteQcPassedDetails(QcPassedDetail qcPassedDetail);

  void update(QcPassedDetail qcPassedDetail);

}