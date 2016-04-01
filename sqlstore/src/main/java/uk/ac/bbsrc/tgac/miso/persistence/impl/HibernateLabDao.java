package uk.ac.bbsrc.tgac.miso.persistence.impl;

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

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.persistence.LabDao;

@Repository
@Transactional
public class HibernateLabDao implements LabDao {
  
  protected static final Logger log = LoggerFactory.getLogger(HibernateLabDao.class);
  
  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public List<Lab> getLabs() {
    Query query = currentSession().createQuery("from LabImpl");
    @SuppressWarnings("unchecked")
    List<Lab> labs = query.list();
    return labs;
  }

  @Override
  public Lab getLab(Long id) {
    return (Lab) currentSession().get(LabImpl.class, id);
  }

  @Override
  public Long addLab(Lab lab) {
    Date now = new Date();
    lab.setCreationDate(now);
    lab.setLastUpdated(now);
    return (Long) currentSession().save(lab);
  }

  @Override
  public void deleteLab(Lab lab) {
    currentSession().delete(lab);
  }

  @Override
  public void update(Lab lab) {
    Date now = new Date();
    lab.setLastUpdated(now);
    currentSession().update(lab);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
