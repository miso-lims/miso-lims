package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.RunItemQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.persistence.RunItemQcStatusDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateRunItemQcStatusDao extends HibernateSaveDao<RunItemQcStatus> implements RunItemQcStatusDao {

  public HibernateRunItemQcStatusDao() {
    super(RunItemQcStatus.class);
  }

  @Override
  public RunItemQcStatus getByDescription(String description) throws IOException {
    return getBy("description", description);
  }

  @Override
  public List<RunItemQcStatus> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("statusId", ids);
  }

  @Override
  public long getUsage(RunItemQcStatus status) throws IOException {
    return getUsageBy(RunPartitionAliquot.class, "qcStatus", status);
  }

}
