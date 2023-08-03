package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.persistence.LabDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLabDao extends HibernateSaveDao<Lab> implements LabDao {

  public HibernateLabDao() {
    super(Lab.class, LabImpl.class);
  }

  @Override
  public Lab getByAlias(String alias) {
    return getBy("alias", alias);
  }

  @Override
  public long getUsageByTissues(Lab lab) {
    return getUsageBy(SampleTissueImpl.class, "lab", lab);
  }

  @Override
  public long getUsageByTransfers(Lab lab) {
    return getUsageBy(Transfer.class, "senderLab", lab);
  }

  @Override
  public List<Lab> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("id", idList);
  }
}
