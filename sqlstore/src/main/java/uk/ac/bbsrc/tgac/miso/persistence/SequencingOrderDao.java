package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.store.SaveDao;

public interface SequencingOrderDao extends SaveDao<SequencingOrder> {

  List<SequencingOrder> listByPool(Pool pool);

}
