package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.store.TargetedSequencingStore;
import uk.ac.bbsrc.tgac.miso.service.TargetedSequencingService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTargetedSequencingService implements TargetedSequencingService {

  @Autowired
  private TargetedSequencingStore targetedSequencingDao;

  @Override
  public TargetedSequencing get(long targetedSequencingId) throws IOException {
    return targetedSequencingDao.get(targetedSequencingId);
  }

  @Override
  public Collection<TargetedSequencing> list() throws IOException {
    return targetedSequencingDao.listAll();
  }

  public void setTargetedSequencingDao(TargetedSequencingStore targetedSequencingDao) {
    this.targetedSequencingDao = targetedSequencingDao;
  }
}
