package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

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
    QueryBuilder<SequencingOrder, SequencingOrderImpl> builder =
        new QueryBuilder<>(currentSession(), SequencingOrderImpl.class, SequencingOrder.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingOrderImpl_.pool), pool));
    return builder.getResultList();
  }

  @Override
  public List<SequencingOrder> listByAttributes(Pool pool, RunPurpose purpose, SequencingContainerModel containerModel,
      SequencingParameters parameters, Integer partitions) throws IOException {
    QueryBuilder<SequencingOrder, SequencingOrderImpl> builder =
        new QueryBuilder<>(currentSession(), SequencingOrderImpl.class, SequencingOrder.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingOrderImpl_.pool), pool));
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingOrderImpl_.purpose), purpose));
    builder.addPredicate(containerModel == null
        ? builder.getCriteriaBuilder().isNull(builder.getRoot().get(SequencingOrderImpl_.containerModel))
        : builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingOrderImpl_.containerModel),
            containerModel));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingOrderImpl_.parameters), parameters));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingOrderImpl_.partitions), partitions));
    return builder.getResultList();
  }

  @Override
  public List<SequencingOrder> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(SequencingOrderImpl_.SEQUENCING_ORDER_ID, ids);
  }

}
