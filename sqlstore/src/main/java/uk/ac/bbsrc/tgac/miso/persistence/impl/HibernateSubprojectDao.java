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

import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;

@Repository
@Transactional
public class HibernateSubprojectDao implements SubprojectDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSubprojectDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<Subproject> getSubproject() {
    Query query = currentSession().createQuery("from SubprojectImpl");
    @SuppressWarnings("unchecked")
    List<Subproject> records = query.list();
    return records;
  }

  @Override
  public Subproject getSubproject(Long id) {
    return (Subproject) currentSession().get(SubprojectImpl.class, id);
  }

  @Override
  public Long addSubproject(Subproject subproject) {
    Date now = new Date();
    subproject.setCreationDate(now);
    subproject.setLastUpdated(now);
    return (Long) currentSession().save(subproject);
  }

  @Override
  public void deleteSubproject(Subproject subproject) {
    currentSession().delete(subproject);

  }

  @Override
  public void update(Subproject subproject) {
    Date now = new Date();
    subproject.setLastUpdated(now);
    currentSession().update(subproject);
  }

}
