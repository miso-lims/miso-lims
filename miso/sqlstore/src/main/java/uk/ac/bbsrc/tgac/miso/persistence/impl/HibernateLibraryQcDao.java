package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
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
  public QC get(long id) throws IOException {
    return (LibraryQC) currentSession().get(LibraryQC.class, id);
  }

  @Override
  public QualityControlEntity getEntity(long id) throws IOException {
    return (Library) currentSession().get(LibraryImpl.class, id);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public Collection<? extends QC> listForEntity(long id) throws IOException {
    return ((Library) currentSession().get(LibraryImpl.class, id)).getQCs();
  }

  @Override
  public long save(QC qc) {
    LibraryQC libraryQc = (LibraryQC) qc;
    long id;
    if (libraryQc.getId() == LibraryQC.UNSAVED_ID) {
      id = (long) currentSession().save(libraryQc);
    } else {
      currentSession().update(libraryQc);
      id = libraryQc.getId();
    }
    return id;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
