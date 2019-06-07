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

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap;
import uk.ac.bbsrc.tgac.miso.persistence.StorageLocationMapDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateStorageLocationMapDao implements StorageLocationMapDao {

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
  public StorageLocationMap get(long id) throws IOException {
    return (StorageLocationMap) currentSession().get(StorageLocationMap.class, id);
  }

  @Override
  public StorageLocationMap getByFilename(String filename) throws IOException {
    return (StorageLocationMap) currentSession().createCriteria(StorageLocationMap.class)
        .add(Restrictions.eq("filename", filename))
        .uniqueResult();
  }

  @Override
  public List<StorageLocationMap> list() throws IOException {
    @SuppressWarnings("unchecked")
    List<StorageLocationMap> results = currentSession().createCriteria(StorageLocationMap.class).list();
    return results;
  }

  @Override
  public long create(StorageLocationMap map) throws IOException {
    return (long) currentSession().save(map);
  }

  @Override
  public long update(StorageLocationMap map) throws IOException {
    currentSession().update(map);
    return map.getId();
  }

  @Override
  public long getUsage(StorageLocationMap map) throws IOException {
    return (long) currentSession().createCriteria(StorageLocation.class)
        .add(Restrictions.eq("map", map))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
