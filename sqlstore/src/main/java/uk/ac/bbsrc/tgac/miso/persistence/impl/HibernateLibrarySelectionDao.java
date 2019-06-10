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
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.persistence.LibrarySelectionDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateLibrarySelectionDao implements LibrarySelectionDao {

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
  public LibrarySelectionType get(long id) throws IOException {
    return (LibrarySelectionType) currentSession().get(LibrarySelectionType.class, id);
  }

  @Override
  public LibrarySelectionType getByName(String name) throws IOException {
    return (LibrarySelectionType) currentSession().createCriteria(LibrarySelectionType.class)
        .add(Restrictions.eq("name", name))
        .uniqueResult();
  }

  @Override
  public List<LibrarySelectionType> list() throws IOException {
    @SuppressWarnings("unchecked")
    List<LibrarySelectionType> results = currentSession().createCriteria(LibrarySelectionType.class).list();
    return results;
  }

  @Override
  public long create(LibrarySelectionType type) throws IOException {
    return (long) currentSession().save(type);
  }

  @Override
  public long update(LibrarySelectionType type) throws IOException {
    currentSession().update(type);
    return type.getId();
  }

  @Override
  public long getUsage(LibrarySelectionType type) throws IOException {
    return (long) currentSession().createCriteria(LibraryImpl.class)
        .add(Restrictions.eq("librarySelectionType", type))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
