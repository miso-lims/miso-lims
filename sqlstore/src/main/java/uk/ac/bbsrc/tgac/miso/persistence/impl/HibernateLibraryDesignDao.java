package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignDao;

/**
 * This is the Hibernate DAO for LibraryDesigns and serves as the bridge between Hibernate and the existing SqlStore persistence layers.
 * 
 * The data from the LibraryDesign table is loaded via Hibernate, but Hibernate cannot follow the references to LibrarySelectionType and
 * LibraryStrategyType from a LibraryDesign.
 * Therefore, this implementation loads a LibraryDesign via Hibernate, then calls into the SqlStore persistence layer to gather the
 * remaining data that Hibernate cannot access. Similarly, it then follows any necessary links on save. All the SqlStore-populated fields
 * are marked “transient” in the LibraryDesign class.
 */
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

  @Override
  public List<LibraryDesign> getLibraryDesignByClass(SampleClass sampleClass) throws IOException {
    if (sampleClass == null) return Collections.emptyList();
    Criteria criteria = currentSession().createCriteria(LibraryDesign.class);
    criteria.createAlias("sampleClass", "sampleClass");
    criteria.add(Restrictions.eq("sampleClass.id", sampleClass.getId()));
    @SuppressWarnings("unchecked")
    List<LibraryDesign> rules = criteria.list();
    return rules;
  }

  @Override
  public LibraryDesign getLibraryDesign(Long id) throws IOException {
    LibraryDesign libraryDesign = (LibraryDesign) currentSession().get(LibraryDesign.class, id);
    return libraryDesign;
  }

  @Override
  public List<LibraryDesign> getLibraryDesigns() throws IOException {
    @SuppressWarnings("unchecked")
    List<LibraryDesign> libraryDesigns = currentSession().createCriteria(LibraryDesign.class).list();
    return libraryDesigns;
  }

}
