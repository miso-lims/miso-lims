package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryQcStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryQcDao implements LibraryQcStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleQcDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(LibraryQC libraryQc) throws IOException {
    long id;
    if (libraryQc.getId() == LibraryQCImpl.UNSAVED_ID) {
      id = (long) currentSession().save(libraryQc);
    } else {
      currentSession().update(libraryQc);
      id = libraryQc.getId();
    }
    return id;
  }

  @Override
  public LibraryQC get(long id) throws IOException {
    return (LibraryQC) currentSession().get(LibraryQCImpl.class, id);
  }

  @Override
  public Collection<LibraryQC> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryQCImpl.class);
    @SuppressWarnings("unchecked")
    List<LibraryQC> records = criteria.list();
    return records;
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryQCImpl.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public boolean remove(LibraryQC libraryQc) throws IOException {
    if (libraryQc.isDeletable()) {
      get(libraryQc.getId()).getLibrary().getLibraryQCs().remove(libraryQc);
      currentSession().delete(libraryQc);
      LibraryQC testIfExists = get(libraryQc.getId());

      return testIfExists == null;
    } else {
      return false;
    }
  }

  @Override
  public Collection<LibraryQC> listByLibraryId(long libraryId) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryQCImpl.class);
    criteria.add(Restrictions.eq("library.id", libraryId));
    @SuppressWarnings("unchecked")
    Collection<LibraryQC> records = criteria.list();
    return records;
  }

  @Override
  public QcType getLibraryQcTypeById(long qcTypeId) throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    criteria.add(Restrictions.eq("qcTarget", "Library"));
    criteria.add(Restrictions.eq("id", qcTypeId));
    return (QcType) criteria.uniqueResult();
  }

  @Override
  public QcType getLibraryQcTypeByName(String qcName) throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    criteria.add(Restrictions.eq("qcTarget", "Library"));
    criteria.add(Restrictions.eq("name", qcName));
    return (QcType) criteria.uniqueResult();
  }

  @Override
  public Collection<QcType> listAllLibraryQcTypes() throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    criteria.add(Restrictions.eq("qcTarget", "Library"));
    @SuppressWarnings("unchecked")
    Collection<QcType> records = criteria.list();
    return records;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
