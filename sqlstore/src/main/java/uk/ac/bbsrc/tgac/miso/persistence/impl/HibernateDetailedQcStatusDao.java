package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedQcStatusDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateDetailedQcStatusDao implements DetailedQcStatusDao {

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<DetailedQcStatus> list() {
    Query query = currentSession().createQuery("from DetailedQcStatusImpl");
    @SuppressWarnings("unchecked")
    List<DetailedQcStatus> records = query.list();
    return records;
  }

  @Override
  public DetailedQcStatus get(Long id) {
    return (DetailedQcStatus) currentSession().get(DetailedQcStatusImpl.class, id);
  }

  @Override
  public DetailedQcStatus getByDescription(String description) {
    return (DetailedQcStatus) currentSession().createCriteria(DetailedQcStatusImpl.class)
        .add(Restrictions.eq("description", description))
        .uniqueResult();
  }

  @Override
  public long create(DetailedQcStatus detailedQcStatus) {
    return (long) currentSession().save(detailedQcStatus);
  }

  @Override
  public long update(DetailedQcStatus detailedQcStatus) {
    currentSession().update(detailedQcStatus);
    return detailedQcStatus.getId();
  }

  @Override
  public long getUsage(DetailedQcStatus detailedQcStatus) {
    return (long) currentSession().createCriteria(DetailedSampleImpl.class)
        .add(Restrictions.eq("detailedQcStatus", detailedQcStatus))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
