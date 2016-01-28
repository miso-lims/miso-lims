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

import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstituteImpl;
import uk.ac.bbsrc.tgac.miso.persistence.InstituteDao;

@Repository
@Transactional
public class HibernateInstituteDao implements InstituteDao {
  
  protected static final Logger log = LoggerFactory.getLogger(HibernateInstituteDao.class);
  
  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<Institute> getInstitute() {
    Query query = currentSession().createQuery("from InstituteImpl");
    @SuppressWarnings("unchecked")
    List<Institute> institutes = query.list();
    return institutes;
  }

  @Override
  public Institute getInstitute(Long id) {
    return (Institute) currentSession().get(InstituteImpl.class, id);
  }

  @Override
  public Long addInstitute(Institute institute) {
    Date now = new Date();
    institute.setCreationDate(now);
    institute.setLastUpdated(now);
    return (Long) currentSession().save(institute);
  }

  @Override
  public void deleteInstitute(Institute institute) {
    currentSession().delete(institute);
  }

  @Override
  public void update(Institute institute) {
    Date now = new Date();
    institute.setLastUpdated(now);
    currentSession().update(institute);
  }

}
