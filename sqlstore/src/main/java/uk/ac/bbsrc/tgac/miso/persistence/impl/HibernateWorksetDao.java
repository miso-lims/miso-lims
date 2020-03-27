package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.persistence.WorksetStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateWorksetDao implements WorksetStore {

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public Workset get(long id) {
    return (Workset) currentSession().get(Workset.class, id);
  }

  @Override
  public Workset getByAlias(String alias) {
    return (Workset) currentSession().createCriteria(Workset.class)
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

  @Override
  public List<Workset> listBySample(long sampleId) {
    @SuppressWarnings("unchecked")
    List<Workset> results = currentSession().createCriteria(Workset.class)
        .createAlias("samples", "sample")
        .add(Restrictions.eq("sample.id", sampleId))
        .list();
    return results;
  }

  @Override
  public List<Workset> listByLibrary(long libraryId) {
    @SuppressWarnings("unchecked")
    List<Workset> results = currentSession().createCriteria(Workset.class)
        .createAlias("libraries", "library")
        .add(Restrictions.eq("library.id", libraryId))
        .list();
    return results;
  }

  @Override
  public List<Workset> listByLibraryAliquot(long aliquotId) {
    @SuppressWarnings("unchecked")
    List<Workset> results = currentSession().createCriteria(Workset.class)
        .createAlias("libraryAliquots", "aliquot")
        .add(Restrictions.eq("aliquot.id", aliquotId))
        .list();
    return results;
  }

  @Override
  public long save(Workset workset) {
    if (!workset.isSaved()) {
      return (long) currentSession().save(workset);
    } else {
      currentSession().update(workset);
      return workset.getId();
    }
  }

}
