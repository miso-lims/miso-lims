package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentStatus;
import uk.ac.bbsrc.tgac.miso.core.store.InstrumentStatusStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateInstrumentStatusDao implements InstrumentStatusStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateQcTypeDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public List<InstrumentStatus> list() throws IOException {
    Criteria criteria = currentSession().createCriteria(InstrumentStatus.class);
    @SuppressWarnings("unchecked")
    List<InstrumentStatus> records = criteria.list();
    return records;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
