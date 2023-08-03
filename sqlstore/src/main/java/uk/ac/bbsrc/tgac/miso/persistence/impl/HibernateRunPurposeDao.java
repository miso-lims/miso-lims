package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingOrderImpl;
import uk.ac.bbsrc.tgac.miso.persistence.RunPurposeDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateRunPurposeDao extends HibernateSaveDao<RunPurpose> implements RunPurposeDao {

  public HibernateRunPurposeDao() {
    super(RunPurpose.class);
  }

  @Override
  public RunPurpose getByAlias(String alias) {
    return getBy("alias", alias);
  }

  @Override
  public List<RunPurpose> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("purposeId", idList);
  }

  @Override
  public long getUsageByPoolOrders(RunPurpose purpose) {
    return getUsageBy(PoolOrder.class, "purpose", purpose);
  }

  @Override
  public long getUsageBySequencingOrders(RunPurpose purpose) {
    return getUsageBy(SequencingOrderImpl.class, "purpose", purpose);
  }

}
