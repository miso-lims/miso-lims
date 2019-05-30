package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.persistence.BoxUseDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateBoxUseDao implements BoxUseDao {

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
  public BoxUse get(long id) {
    return (BoxUse) currentSession().get(BoxUse.class, id);
  }

  @Override
  public BoxUse getByAlias(String alias) {
    return (BoxUse) currentSession().createCriteria(BoxUse.class)
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

  @Override
  public List<BoxUse> list() {
    @SuppressWarnings("unchecked")
    List<BoxUse> results = currentSession().createCriteria(BoxUse.class).list();
    return results;
  }

  @Override
  public long create(BoxUse boxUse) {
    return (long) currentSession().save(boxUse);
  }

  @Override
  public long update(BoxUse boxUse) {
    currentSession().update(boxUse);
    return boxUse.getId();
  }

  @Override
  public long getUsage(BoxUse boxUse) {
    return (long) currentSession().createCriteria(BoxImpl.class)
        .add(Restrictions.eq("use", boxUse))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
