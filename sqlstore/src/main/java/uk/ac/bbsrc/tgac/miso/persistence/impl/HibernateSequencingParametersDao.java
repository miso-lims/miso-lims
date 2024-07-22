package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Run_;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingOrderImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingOrderImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingParametersDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencingParametersDao extends HibernateSaveDao<SequencingParameters>
    implements SequencingParametersDao {

  public HibernateSequencingParametersDao() {
    super(SequencingParameters.class);
  }

  @Override
  public List<SequencingParameters> listByInstrumentModel(InstrumentModel instrumentModel) throws IOException {
    QueryBuilder<SequencingParameters, SequencingParameters> builder =
        new QueryBuilder<>(currentSession(), SequencingParameters.class, SequencingParameters.class);
    builder.addPredicate(builder.getCriteriaBuilder()
        .equal(builder.getRoot().get(SequencingParameters_.instrumentModel), instrumentModel));
    return builder.getResultList();
  }

  @Override
  public List<SequencingParameters> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(SequencingParameters_.PARAMETERS_ID, idList);
  }

  @Override
  public SequencingParameters getByNameAndInstrumentModel(String name, InstrumentModel instrumentModel)
      throws IOException {
    QueryBuilder<SequencingParameters, SequencingParameters> builder =
        new QueryBuilder<>(currentSession(), SequencingParameters.class, SequencingParameters.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingParameters_.name), name));
    builder.addPredicate(builder.getCriteriaBuilder()
        .equal(builder.getRoot().get(SequencingParameters_.instrumentModel), instrumentModel));
    return builder.getSingleResultOrNull();
  }

  @Override
  public long getUsageByRuns(SequencingParameters sequencingParameters) throws IOException {
    return getUsageBy(Run.class, Run_.sequencingParameters, sequencingParameters);
  }

  @Override
  public long getUsageByPoolOrders(SequencingParameters sequencingParameters) throws IOException {
    return getUsageBy(PoolOrder.class, PoolOrder_.parameters, sequencingParameters);
  }

  @Override
  public long getUsageBySequencingOrders(SequencingParameters sequencingParameters) throws IOException {
    return getUsageBy(SequencingOrderImpl.class, SequencingOrderImpl_.parameters, sequencingParameters);
  }

}
