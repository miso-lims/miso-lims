package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.TargetedSequencingStore;

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
  public List<TargetedSequencing> list() throws IOException {
    return (List<TargetedSequencing>) targetedSequencingDao.listAll();
  }

  @Override
  public List<TargetedSequencing> list(List<Long> targetedSequencingIds) throws IOException {
    return targetedSequencingDao.list(targetedSequencingIds);
  }

  public void setTargetedSequencingDao(TargetedSequencingStore targetedSequencingDao) {
    this.targetedSequencingDao = targetedSequencingDao;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return targetedSequencingDao.count(errorHandler, filter);
  }

  @Override
  public List<TargetedSequencing> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return targetedSequencingDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }
}
