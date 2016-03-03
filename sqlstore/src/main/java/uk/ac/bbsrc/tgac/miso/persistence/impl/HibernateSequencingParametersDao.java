package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingParametersImpl;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingParametersDao;

@Repository
@Transactional
public class HibernateSequencingParametersDao implements SequencingParametersDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSequencingParametersDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private PlatformStore platformStore;

  private SequencingParameters fetchSqlStore(SequencingParameters sp) throws IOException {
    sp.setPlatform(platformStore.get(sp.getPlatformId()));
    return sp;
  }

  private <T extends Iterable<SequencingParameters>> T fetchSqlStore(T items) throws IOException {
    for (SequencingParameters item : items) {
      fetchSqlStore(item);
    }
    return items;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<SequencingParameters> getSequencingParameters() throws IOException {
    Query query = currentSession().createQuery("from SequencingParametersImpl sp");
    @SuppressWarnings("unchecked")
    List<SequencingParameters> records = query.list();
    return fetchSqlStore(records);

  }

  @Override
  public SequencingParameters getSequencingParameters(Long id) throws IOException {
    if (id == null) {
      return null;
    }
    return fetchSqlStore((SequencingParameters) currentSession().get(SequencingParametersImpl.class, id));
  }

  @Override
  public Long addSequencingParameters(SequencingParameters sequencingParameters) {
    Date now = new Date();
    sequencingParameters.setCreationDate(now);
    sequencingParameters.setLastUpdated(now);
    return (Long) currentSession().save(sequencingParameters);
  }

  @Override
  public void deleteSequencingParameters(SequencingParameters sequencingParameters) {
    currentSession().delete(sequencingParameters);
  }

  @Override
  public void update(SequencingParameters sequencingParameters) {
    Date now = new Date();
    sequencingParameters.setLastUpdated(now);
    currentSession().update(sequencingParameters);
  }

}
