package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.IndexStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateIndexDao implements IndexStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSubprojectDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public Index getIndexById(long id) {
    Query query = currentSession().createQuery("from Index where id = :id");
    query.setLong("id", id);
    return (Index) query.uniqueResult();
  }

  @Override
  public List<IndexFamily> getIndexFamilies() {
    Query query = currentSession().createQuery("from IndexFamily");
    @SuppressWarnings("unchecked")
    List<IndexFamily> list = query.list();
    return list;
  }

  @Override
  public List<IndexFamily> getIndexFamiliesByPlatform(PlatformType platformType) {
    Query query = currentSession().createQuery("from IndexFamily where platformType = :platform");
    query.setParameter("platform", platformType);
    @SuppressWarnings("unchecked")
    List<IndexFamily> list = query.list();
    return list;
  }

  @Override
  public IndexFamily getIndexFamilyByName(String name) {
    Query query = currentSession().createQuery("from IndexFamily where name = :name");
    query.setString("name", name);
    return (IndexFamily) query.uniqueResult();
  }

  @Override
  public List<Index> listAllIndices(PlatformType platformType) {
    Query query = currentSession().createQuery("from Index where family.platformType = :platform");
    query.setParameter("platform", platformType);
    @SuppressWarnings("unchecked")
    List<Index> list = query.list();
    return list;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<Index> listAllIndices() {
    Query query = currentSession().createQuery("from Index");
    @SuppressWarnings("unchecked")
    List<Index> list = query.list();
    return list;
  }

}
