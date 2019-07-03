package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.persistence.ServiceRecordStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateServiceRecordDao implements ServiceRecordStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateServiceRecordDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(ServiceRecord ssr) throws IOException {
    long id;
    if (!ssr.isSaved()) {
      if (ssr.getInstrument().getDateDecommissioned() != null)
        throw new IOException("Cannot add service records to a retired instrument!");

      id = (long) currentSession().save(ssr);
    } else {
      currentSession().update(ssr);
      id = ssr.getId();
    }
    return id;
  }

  @Override
  public ServiceRecord get(long id) throws IOException {
    return (ServiceRecord) currentSession().get(ServiceRecord.class, id);
  }

  @Override
  public List<ServiceRecord> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(ServiceRecord.class);
    @SuppressWarnings("unchecked")
    List<ServiceRecord> records = criteria.list();
    return records;
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(ServiceRecord.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public List<ServiceRecord> listByInstrumentId(long instrumentId) {
    Criteria criteria = currentSession().createCriteria(ServiceRecord.class);
    criteria.add(Restrictions.eq("instrument.id", instrumentId));
    @SuppressWarnings("unchecked")
    List<ServiceRecord> records = criteria.list();
    return records;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
