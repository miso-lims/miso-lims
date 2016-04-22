package uk.ac.bbsrc.tgac.miso.persistence.impl;

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

@Repository
@Transactional
public class HibernateLibraryDesignDao implements LibraryDesignDao {
  protected static final Logger log = LoggerFactory.getLogger(HibernateLibraryDesignDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<LibraryDesign> getLibraryDesignByClass(SampleClass sampleClass) {
    if (sampleClass == null) return Collections.emptyList();
    Query query = currentSession().createQuery("from LibraryDesign where sampleClass.sampleClassId = :sampleClass");
    query.setLong("sampleClass", sampleClass.getSampleClassId());
    @SuppressWarnings("unchecked")
    List<LibraryDesign> rules = query.list();
    return rules;
  }

  @Override
  public LibraryDesign getLibraryDesign(Long id) {
    return (LibraryDesign) currentSession().get(LibraryDesign.class, id);
  }

}
