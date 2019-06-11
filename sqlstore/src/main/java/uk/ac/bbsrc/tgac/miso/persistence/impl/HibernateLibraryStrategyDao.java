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

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryStrategyDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateLibraryStrategyDao implements LibraryStrategyDao {

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
  public LibraryStrategyType get(long id) throws IOException {
    return (LibraryStrategyType) currentSession().get(LibraryStrategyType.class, id);
  }

  @Override
  public LibraryStrategyType getByName(String name) throws IOException {
    return (LibraryStrategyType) currentSession().createCriteria(LibraryStrategyType.class)
        .add(Restrictions.eq("name", name))
        .uniqueResult();
  }

  @Override
  public List<LibraryStrategyType> list() throws IOException {
    @SuppressWarnings("unchecked")
    List<LibraryStrategyType> results = currentSession().createCriteria(LibraryStrategyType.class).list();
    return results;
  }

  @Override
  public long create(LibraryStrategyType type) throws IOException {
    return (long) currentSession().save(type);
  }

  @Override
  public long update(LibraryStrategyType type) throws IOException {
    currentSession().update(type);
    return type.getId();
  }

  @Override
  public long getUsage(LibraryStrategyType type) throws IOException {
    return (long) currentSession().createCriteria(LibraryImpl.class)
        .add(Restrictions.eq("libraryStrategyType", type))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
