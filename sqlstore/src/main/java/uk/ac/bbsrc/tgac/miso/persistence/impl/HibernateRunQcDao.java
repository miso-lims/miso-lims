package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
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

import uk.ac.bbsrc.tgac.miso.core.data.AbstractRun;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.store.RunQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRunQcDao implements RunQcStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateRunQcDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private SequencerPartitionContainerStore sequencerPartitionContainerDao;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(RunQC runQc) throws IOException {
    long id;
    if (runQc.getId() == AbstractRun.UNSAVED_ID) {
      id = (long) currentSession().save(runQc);
    } else {
      currentSession().update(runQc);
      id = runQc.getId();
    }
    return id;
  }

  @Override
  public RunQC get(long id) throws IOException {
    return (RunQC) currentSession().get(RunQCImpl.class, id);
  }

  @Override
  public RunQC lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public Collection<RunQC> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(RunQCImpl.class);
    @SuppressWarnings("unchecked")
    List<RunQC> records = criteria.list();
    return records;
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(RunQCImpl.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public boolean remove(RunQC runQc) throws IOException {
    if (runQc.isDeletable()) {
      currentSession().delete(runQc);
      RunQC testIfExists = get(runQc.getId());

      return testIfExists == null;
    } else {
      return false;
    }
  }

  @Override
  public Collection<RunQC> listByRunId(long runId) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunQCImpl.class);
    criteria.add(Restrictions.eq("run.id", runId));
    @SuppressWarnings("unchecked")
    Collection<RunQC> records = criteria.list();
    return records;
  }

  @Override
  public QcType getRunQcTypeById(long qcTypeId) throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    criteria.add(Restrictions.eq("qcTarget", "Run"));
    criteria.add(Restrictions.eq("id", qcTypeId));
    return (QcType) criteria.uniqueResult();
  }

  @Override
  public QcType getRunQcTypeByName(String qcName) throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    criteria.add(Restrictions.eq("qcTarget", "Run"));
    criteria.add(Restrictions.eq("name", qcName));
    return (QcType) criteria.uniqueResult();
  }

  @Override
  public Collection<QcType> listAllRunQcTypes() throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    criteria.add(Restrictions.eq("qcTarget", "Run"));
    @SuppressWarnings("unchecked")
    Collection<QcType> records = criteria.list();
    return records;
  }

  public List<Partition> listPartitionSelectionsByRunQcId(long runQcId) {
    Criteria criteria = currentSession().createCriteria(RunQCImpl.class);
    criteria.add(Restrictions.eqOrIsNull("id", runQcId));
    @SuppressWarnings("unchecked")
    List<RunQC> runQcs = criteria.list();
    List<Partition> partitions = new ArrayList<>();
    for (RunQC runQc : runQcs) {
      if (runQc.getPartitionSelections() != null) partitions.addAll(runQc.getPartitionSelections());
    }
    return partitions;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public SequencerPartitionContainerStore getSequencerPartitionContainerDao() {
    return sequencerPartitionContainerDao;
  }

  public void setSequencerPartitionContainerDao(SequencerPartitionContainerStore sequencerPartitionContainerDao) {
    this.sequencerPartitionContainerDao = sequencerPartitionContainerDao;
  }
}
