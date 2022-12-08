package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingOrderImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingParametersDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencingParametersDao extends HibernateSaveDao<SequencingParameters> implements SequencingParametersDao {

  public HibernateSequencingParametersDao() {
    super(SequencingParameters.class);
  }

  @Override
  public List<SequencingParameters> listByInstrumentModel(InstrumentModel instrumentModel) throws IOException {
    @SuppressWarnings("unchecked")
    List<SequencingParameters> results = currentSession().createCriteria(SequencingParameters.class)
        .add(Restrictions.eq("instrumentModel", instrumentModel))
        .list();
    return results;
  }

  @Override
  public List<SequencingParameters> listByIdList(List<Long> idList) throws IOException {
    return listByIdList("parametersId", idList);
  }

  @Override
  public SequencingParameters getByNameAndInstrumentModel(String name, InstrumentModel instrumentModel) throws IOException {
    return (SequencingParameters) currentSession().createCriteria(SequencingParameters.class)
        .add(Restrictions.eq("name", name))
        .add(Restrictions.eq("instrumentModel", instrumentModel))
        .uniqueResult();
  }

  @Override
  public long getUsageByRuns(SequencingParameters sequencingParameters) throws IOException {
    return getUsageBy(Run.class, "sequencingParameters", sequencingParameters);
  }

  @Override
  public long getUsageByPoolOrders(SequencingParameters sequencingParameters) throws IOException {
    return getUsageBy(PoolOrder.class, "parameters", sequencingParameters);
  }

  @Override
  public long getUsageBySequencingOrders(SequencingParameters sequencingParameters) throws IOException {
    return getUsageBy(SequencingOrderImpl.class, "parameters", sequencingParameters);
  }

}
