package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.SampleQcNode;
import uk.ac.bbsrc.tgac.miso.core.service.QcNodeService;
import uk.ac.bbsrc.tgac.miso.persistence.QcNodeDao;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultQcNodeService implements QcNodeService {

  @Autowired
  private QcNodeDao qcNodeDao;

  @Override
  public SampleQcNode getForSample(long id) throws IOException {
    return qcNodeDao.getForSample(id);
  }

  @Override
  public SampleQcNode getForLibrary(long id) throws IOException {
    return qcNodeDao.getForLibrary(id);
  }

  @Override
  public SampleQcNode getForLibraryAliquot(long id) throws IOException {
    return qcNodeDao.getForLibraryAliquot(id);
  }

}
