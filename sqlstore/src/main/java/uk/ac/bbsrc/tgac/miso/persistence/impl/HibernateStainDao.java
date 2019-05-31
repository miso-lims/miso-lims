package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSlideImpl;
import uk.ac.bbsrc.tgac.miso.persistence.StainDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateStainDao implements StainDao {

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
  public Stain get(long id) {
    return (Stain) currentSession().get(Stain.class, id);
  }

  @Override
  public List<Stain> list() {
    @SuppressWarnings("unchecked")
    List<Stain> results = currentSession().createCriteria(Stain.class).list();
    return results;
  }

  @Override
  public Stain getByName(String name) throws IOException {
    return (Stain) currentSession().createCriteria(Stain.class)
        .add(Restrictions.eq("name", name))
        .uniqueResult();
  }

  @Override
  public long create(Stain stain) throws IOException {
    return (long) currentSession().save(stain);
  }

  @Override
  public long update(Stain stain) throws IOException {
    currentSession().update(stain);
    return stain.getId();
  }

  @Override
  public long getUsage(Stain stain) throws IOException {
    return (long) currentSession().createCriteria(SampleSlideImpl.class)
        .add(Restrictions.eq("stain", stain))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
