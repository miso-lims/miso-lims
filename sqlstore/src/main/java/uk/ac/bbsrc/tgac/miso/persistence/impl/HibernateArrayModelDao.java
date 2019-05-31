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

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayModelDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateArrayModelDao implements ArrayModelDao {

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
  public ArrayModel get(long id) throws IOException {
    return (ArrayModel) currentSession().get(ArrayModel.class, id);
  }

  @Override
  public ArrayModel getByAlias(String alias) throws IOException {
    return (ArrayModel) currentSession().createCriteria(ArrayModel.class)
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

  @Override
  public List<ArrayModel> list() throws IOException {
    @SuppressWarnings("unchecked")
    List<ArrayModel> results = currentSession().createCriteria(ArrayModel.class).list();
    return results;
  }

  @Override
  public long create(ArrayModel model) throws IOException {
    return (long) currentSession().save(model);
  }

  @Override
  public long update(ArrayModel model) throws IOException {
    currentSession().update(model);
    return model.getId();
  }

  @Override
  public long getUsage(ArrayModel model) throws IOException {
    return (long) currentSession().createCriteria(Array.class)
        .add(Restrictions.eq("arrayModel", model))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
