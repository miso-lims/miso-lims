package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
  public List<SequencingParameters> getSequencingParameters() throws IOException {
    Criteria query = currentSession().createCriteria(SequencingParameters.class);
    @SuppressWarnings("unchecked")
    List<SequencingParameters> records = query.list();
    return records;

  }

  @Override
  public SequencingParameters getSequencingParameters(Long id) throws IOException {
    if (id == null) {
      return null;
    }
    return (SequencingParameters) currentSession().get(SequencingParameters.class, id);
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

  @Override
  public Iterator<SequencingParameters> iterator() {
    try {
      return getSequencingParameters().iterator();
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
