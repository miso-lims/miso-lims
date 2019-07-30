package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.impl.OrderPurpose;

public interface OrderPurposeDao extends SaveDao<OrderPurpose> {

  public OrderPurpose getByAlias(String alias);

  public long getUsageByPoolOrders(OrderPurpose purpose);

  public long getUsageBySequencingOrders(OrderPurpose purpose);

}
