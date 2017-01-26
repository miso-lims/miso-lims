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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.store.StatusStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateStatusDao implements StatusStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateStatusDao.class);

  @Autowired
  private JdbcTemplate template;
  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(Status status) throws IOException {
    long id;
    if (status.getId() == StatusImpl.UNSAVED_ID) {
      id = (Long) currentSession().save(status);
    } else {
      currentSession().update(status);
      id = status.getId();
    }
    return id;
  }

  @Override
  public Status get(long id) throws IOException {
    return (Status) currentSession().get(StatusImpl.class, id);
  }

  @Override
  public List<Status> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(StatusImpl.class);
    @SuppressWarnings("unchecked")
    List<Status> records = criteria.list();
    return records;
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(StatusImpl.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public List<Status> listByHealth(String health) {
    Criteria criteria = currentSession().createCriteria(StatusImpl.class);
    criteria.add(Restrictions.eq("health", HealthType.get(health)));
    @SuppressWarnings("unchecked")
    List<Status> records = criteria.list();
    return records;
  }

  @Override
  public Status getByRunName(String runName) throws IOException {
    runName = DbUtils.convertStringToSearchQuery(runName);
    Criteria criteria = currentSession().createCriteria(StatusImpl.class);
    criteria.add(Restrictions.ilike("runName", runName));
    criteria.setMaxResults(1);
    return (Status) criteria.uniqueResult();
  }

  @Override
  public List<Status> listAllBySequencerName(String sequencerName) {
    sequencerName = DbUtils.convertStringToSearchQuery(sequencerName);
    Criteria criteria = currentSession().createCriteria(StatusImpl.class);
    criteria.add(Restrictions.ilike("instrumentName", sequencerName));
    @SuppressWarnings("unchecked")
    List<Status> records = criteria.list();
    return records;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
