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

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedQcStatusDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateDetailedQcStatusDao implements DetailedQcStatusDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateDetailedQcStatusDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<DetailedQcStatus> getDetailedQcStatus() {
    Query query = currentSession().createQuery("from DetailedQcStatusImpl");
    @SuppressWarnings("unchecked")
    List<DetailedQcStatus> records = query.list();
    return records;
  }

  @Override
  public DetailedQcStatus getDetailedQcStatus(Long id) {
    return (DetailedQcStatus) currentSession().get(DetailedQcStatusImpl.class, id);
  }

  @Override
  public Long addDetailedQcStatus(DetailedQcStatus detailedQcStatus) {
    Date now = new Date();
    detailedQcStatus.setCreationDate(now);
    detailedQcStatus.setLastUpdated(now);
    return (Long) currentSession().save(detailedQcStatus);
  }

  @Override
  public void deleteDetailedQcStatus(DetailedQcStatus detailedQcStatus) {
    currentSession().delete(detailedQcStatus);

  }

  @Override
  public void update(DetailedQcStatus detailedQcStatus) {
    Date now = new Date();
    detailedQcStatus.setLastUpdated(now);
    currentSession().update(detailedQcStatus);
  }

}
