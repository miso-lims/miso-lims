package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetSample;
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
        .createAlias("worksetSamples", "worksetSample")
        .createAlias("worksetSample.item", "sample")
        .add(Restrictions.eq("sample.id", sampleId))
        .list();
    return results;
  }

  @Override
  public List<Workset> listByLibrary(long libraryId) {
    @SuppressWarnings("unchecked")
    List<Workset> results = currentSession().createCriteria(Workset.class)
        .createAlias("worksetLibraries", "worksetLibrary")
        .createAlias("worksetLibrary.item", "library")
        .add(Restrictions.eq("library.id", libraryId))
        .list();
    return results;
  }

  @Override
  public List<Workset> listByLibraryAliquot(long aliquotId) {
    @SuppressWarnings("unchecked")
    List<Workset> results = currentSession().createCriteria(Workset.class)
        .createAlias("worksetLibraryAliquots", "worksetLibraryAliquot")
        .createAlias("worksetLibraryAliquot.item", "aliquot")
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

  @Override
  public Map<Long, Date> getSampleAddedTimes(long worksetId) {
    return getAddedTimes(worksetId, WorksetSample.class);
  }

  @Override
  public Map<Long, Date> getLibraryAddedTimes(long worksetId) {
    return getAddedTimes(worksetId, WorksetLibrary.class);
  }

  @Override
  public Map<Long, Date> getLibraryAliquotAddedTimes(long worksetId) {
    return getAddedTimes(worksetId, WorksetLibraryAliquot.class);
  }

  private Map<Long, Date> getAddedTimes(long worksetId, Class<?> itemClass) {
    @SuppressWarnings("unchecked")
    List<Object[]> results = currentSession().createCriteria(itemClass)
        .createAlias("workset", "workset")
        .createAlias("item", "item")
        .add(Restrictions.eq("workset.id", worksetId))
        .setProjection(Projections.projectionList()
            .add(Projections.property("item.id"))
            .add(Projections.property("addedTime")))
        .list();

    return results.stream()
        .filter(row -> row[1] != null)
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Date) row[1]));
  }

}
