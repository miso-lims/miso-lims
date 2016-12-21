package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.store.TargetedSequencingStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTargetedSequencingDao implements TargetedSequencingStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateTargetedSequencingDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(TargetedSequencing ts) throws IOException {
    long id;
    if (ts.getId() == TargetedSequencing.UNSAVED_ID) {
      id = (long) currentSession().save(ts);
    } else {
      currentSession().update(ts);
      id = ts.getId();
    }
    return id;
  }

  @Override
  public TargetedSequencing get(long id) throws IOException {
    return (TargetedSequencing) currentSession().get(TargetedSequencing.class, id);
  }

  @Override
  public TargetedSequencing lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public Collection<TargetedSequencing> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(TargetedSequencing.class);
    @SuppressWarnings("unchecked")
    List<TargetedSequencing> records = criteria.list();
    return records;
  }

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(TargetedSequencing.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
