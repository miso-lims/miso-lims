package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingParametersDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencingParametersDao implements SequencingParametersDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSequencingParametersDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<SequencingParameters> list() throws IOException {
    Criteria query = currentSession().createCriteria(SequencingParameters.class);
    @SuppressWarnings("unchecked")
    List<SequencingParameters> records = query.list();
    return records;
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
  public SequencingParameters get(long id) throws IOException {
    return (SequencingParameters) currentSession().get(SequencingParameters.class, id);
  }

  @Override
  public long create(SequencingParameters sequencingParameters) {
    Date now = new Date();
    sequencingParameters.setCreationDate(now);
    sequencingParameters.setLastUpdated(now);
    return (long) currentSession().save(sequencingParameters);
  }

  @Override
  public long update(SequencingParameters sequencingParameters) {
    Date now = new Date();
    sequencingParameters.setLastUpdated(now);
    currentSession().update(sequencingParameters);
    return sequencingParameters.getId();
  }

  @Override
  public Iterator<SequencingParameters> iterator() {
    try {
      return list().iterator();
    } catch (IOException e) {
      log.error("Failed to get sequencing parameters", e);
      return Collections.emptyIterator();
    }
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
