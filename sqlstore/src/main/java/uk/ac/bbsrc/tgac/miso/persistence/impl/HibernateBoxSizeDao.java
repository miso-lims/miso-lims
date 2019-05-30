package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.persistence.BoxSizeDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateBoxSizeDao implements BoxSizeDao {

  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public BoxSize get(long id) {
    return (BoxSize) currentSession().get(BoxSize.class, id);
  }

  @Override
  public List<BoxSize> list() {
    @SuppressWarnings("unchecked")
    List<BoxSize> results = currentSession().createCriteria(BoxSize.class).list();
    return results;
  }

  @Override
  public long create(BoxSize boxSize) {
    return (long) currentSession().save(boxSize);
  }

  @Override
  public long update(BoxSize boxSize) {
    currentSession().update(boxSize);
    return boxSize.getId();
  }

  @Override
  public long getUsage(BoxSize boxSize) {
    return (long) currentSession().createCriteria(BoxImpl.class)
        .add(Restrictions.eq("size", boxSize))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
