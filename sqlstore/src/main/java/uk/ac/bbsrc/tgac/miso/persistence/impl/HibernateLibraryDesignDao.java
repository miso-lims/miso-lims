package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignDao;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryDesignDao implements LibraryDesignDao {
  protected static final Logger log = LoggerFactory.getLogger(HibernateLibraryDesignDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }
  
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Autowired
  private LibraryStore libraryStore;

  public void setLibraryStore(LibraryStore libraryStore) {
    this.libraryStore = libraryStore;
  }

  @Override
  public List<LibraryDesign> getLibraryDesignByClass(SampleClass sampleClass) throws IOException {
    if (sampleClass == null) return Collections.emptyList();
    Query query = currentSession().createQuery("from LibraryDesign where sampleClass.sampleClassId = :sampleClass");
    query.setLong("sampleClass", sampleClass.getId());
    @SuppressWarnings("unchecked")
    List<LibraryDesign> rules = query.list();
    fetchSqlStore(rules);
    return rules;
  }

  @Override
  public LibraryDesign getLibraryDesign(Long id) throws IOException {
    LibraryDesign libraryDesign = (LibraryDesign) currentSession().get(LibraryDesign.class, id);
    fetchSqlStore(libraryDesign);
    return libraryDesign;
  }

  @Override
  public List<LibraryDesign> getLibraryDesigns() throws IOException {
    Query query = currentSession().createQuery("from LibraryDesign");
    @SuppressWarnings("unchecked")
    List<LibraryDesign> libraryDesigns = query.list();
    fetchSqlStore(libraryDesigns);
    return libraryDesigns;
  }

  private LibraryDesign fetchSqlStore(LibraryDesign libraryDesign) throws IOException {
    if (libraryDesign != null && libraryDesign.getHibernateLibrarySelectionTypeId() != null) {
      libraryDesign.setLibrarySelectionType(libraryStore.getLibrarySelectionTypeById(libraryDesign.getHibernateLibrarySelectionTypeId()));
    }
    if (libraryDesign != null && libraryDesign.getHibernateLibraryStrategyTypeId() != null) {
      libraryDesign.setLibraryStrategyType(libraryStore.getLibraryStrategyTypeById(libraryDesign.getHibernateLibrarySelectionTypeId()));
    }
    return libraryDesign;
  }

  private Collection<LibraryDesign> fetchSqlStore(Collection<LibraryDesign> libraryDesigns) throws IOException {
    for (LibraryDesign libraryDesign : libraryDesigns) {
      fetchSqlStore(libraryDesign);
    }
    return libraryDesigns;
  }

}
