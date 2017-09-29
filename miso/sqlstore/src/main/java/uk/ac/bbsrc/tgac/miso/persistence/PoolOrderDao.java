package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;

public interface PoolOrderDao {

  List<PoolOrder> getPoolOrder();

  PoolOrder getPoolOrder(Long id);

  Long addPoolOrder(PoolOrder poolOrder);

  void deletePoolOrder(PoolOrder poolOrder);

  void update(PoolOrder poolOrder);

  List<PoolOrder> getByPool(Long id);
}
