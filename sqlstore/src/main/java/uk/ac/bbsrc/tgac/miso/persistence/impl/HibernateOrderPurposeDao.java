package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OrderPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.persistence.OrderPurposeDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateOrderPurposeDao extends HibernateSaveDao<OrderPurpose> implements OrderPurposeDao {

  public HibernateOrderPurposeDao() {
    super(OrderPurpose.class);
  }

  @Override
  public OrderPurpose getByAlias(String alias) {
    return getBy("alias", alias);
  }

  @Override
  public long getUsageByPoolOrders(OrderPurpose purpose) {
    return getUsageBy(PoolOrder.class, "purpose", purpose);
  }

  @Override
  public long getUsageBySequencingOrders(OrderPurpose purpose) {
    return getUsageBy(SequencingOrder.class, "purpose", purpose);
  }

}
