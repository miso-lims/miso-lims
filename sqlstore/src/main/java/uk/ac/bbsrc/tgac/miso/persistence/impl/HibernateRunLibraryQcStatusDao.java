package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.persistence.RunLibraryQcStatusDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateRunLibraryQcStatusDao extends HibernateSaveDao<RunLibraryQcStatus> implements RunLibraryQcStatusDao {

  public HibernateRunLibraryQcStatusDao() {
    super(RunLibraryQcStatus.class);
  }

  @Override
  public RunLibraryQcStatus getByDescription(String description) throws IOException {
    return getBy("description", description);
  }

  @Override
  public List<RunLibraryQcStatus> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("statusId", ids);
  }

  @Override
  public long getUsage(RunLibraryQcStatus status) throws IOException {
    return getUsageBy(RunPartitionAliquot.class, "qcStatus", status);
  }

}
