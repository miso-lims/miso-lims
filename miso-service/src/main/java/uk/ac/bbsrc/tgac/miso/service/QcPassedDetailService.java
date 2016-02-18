package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;

public interface QcPassedDetailService {

  QcPassedDetail get(Long qcPassedDetailId) throws IOException;

  Long create(QcPassedDetail qcPassedDetail) throws IOException;

  void update(QcPassedDetail qcPassedDetail) throws IOException;

  Set<QcPassedDetail> getAll() throws IOException;

  void delete(Long qcPassedDetailId) throws IOException;

}