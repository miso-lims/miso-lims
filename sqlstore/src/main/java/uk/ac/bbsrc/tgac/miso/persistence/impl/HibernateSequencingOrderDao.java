package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingOrderImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingOrderImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingOrderDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencingOrderDao extends HibernateSaveDao<SequencingOrder> implements SequencingOrderDao {

  public HibernateSequencingOrderDao() {
    super(SequencingOrder.class, SequencingOrderImpl.class);
  }

  @Override
  public List<SequencingOrder> listByPool(Pool pool) {
    @SuppressWarnings("unchecked")
    List<SequencingOrder> records = currentSession().createCriteria(SequencingOrderImpl.class)
        .add(Restrictions.eq("pool", pool))
        .list();
    return records;
  }

  @Override
  public List<SequencingOrder> listByAttributes(Pool pool, RunPurpose purpose, SequencingContainerModel containerModel,
      SequencingParameters parameters, Integer partitions) throws IOException {
    @SuppressWarnings("unchecked")
    List<SequencingOrder> records = currentSession().createCriteria(SequencingOrderImpl.class)
        .add(Restrictions.eq("pool", pool))
        .add(Restrictions.eq("purpose", purpose))
        .add(containerModel == null ? Restrictions.isNull("containerModel")
            : Restrictions.eq("containerModel", containerModel))
        .add(Restrictions.eq("parameters", parameters))
        .add(Restrictions.eq("partitions", partitions))
        .list();
    return records;
  }

  @Override
  public List<SequencingOrder> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(SequencingOrderImpl_.SEQUENCING_ORDER_ID, ids);
  }

}
