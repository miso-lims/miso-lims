package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;
import uk.ac.bbsrc.tgac.miso.persistence.StainCategoryDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateStainCategoryDao implements StainCategoryDao {

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public StainCategory get(long id) throws IOException {
    return (StainCategory) currentSession().get(StainCategory.class, id);
  }

  @Override
  public StainCategory getByName(String name) throws IOException {
    return (StainCategory) currentSession().createCriteria(StainCategory.class)
        .add(Restrictions.eq("name", name))
        .uniqueResult();
  }

  @Override
  public List<StainCategory> list() throws IOException {
    Criteria criteria = currentSession().createCriteria(StainCategory.class);
    @SuppressWarnings("unchecked")
    List<StainCategory> results = criteria.list();
    return results;
  }

  @Override
  public long create(StainCategory stainCategory) throws IOException {
    return (long) currentSession().save(stainCategory);
  }

  @Override
  public long update(StainCategory stainCategory) throws IOException {
    currentSession().update(stainCategory);
    return stainCategory.getId();
  }

  @Override
  public long getUsage(StainCategory stainCategory) throws IOException {
    return (long) currentSession().createCriteria(Stain.class)
        .add(Restrictions.eq("category", stainCategory))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
