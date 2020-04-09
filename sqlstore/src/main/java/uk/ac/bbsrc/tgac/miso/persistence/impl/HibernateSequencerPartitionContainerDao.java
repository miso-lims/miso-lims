package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoreVersion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SequencerPartitionContainerStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencerPartitionContainerDao implements SequencerPartitionContainerStore {

  @Autowired
  private SessionFactory sessionFactory;

  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public SequencerPartitionContainer save(SequencerPartitionContainer spc) throws IOException {
    if (spc.getId() == SequencerPartitionContainerImpl.UNSAVED_ID) {
      currentSession().save(spc);
    } else {
      currentSession().update(spc);
    }
    return spc;
  }

  @Override
  public SequencerPartitionContainer get(long id) throws IOException {
    return (SequencerPartitionContainer) currentSession().get(SequencerPartitionContainerImpl.class, id);
  }

  @Override
  public List<SequencerPartitionContainer> listAllSequencerPartitionContainersByRunId(long runId)
      throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class);
    criteria.createAlias("runPositions", "runPos");
    criteria.createAlias("runPos.run", "run");
    criteria.add(Restrictions.eq("run.id", runId));
    @SuppressWarnings("unchecked")
    List<SequencerPartitionContainer> records = criteria.list();
    return records;
  }

  @Override
  public List<Partition> listAllPartitionsByPoolId(long poolId)
      throws IOException {
    Criteria criteria = currentSession().createCriteria(PartitionImpl.class);
    criteria.createAlias("pool", "pool");
    criteria.add(Restrictions.eq("pool.id", poolId));
    @SuppressWarnings("unchecked")
    List<Partition> records = criteria.list();
    return records;
  }

  @Override
  public List<SequencerPartitionContainer> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException {
    Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    @SuppressWarnings("unchecked")
    List<SequencerPartitionContainer> records = criteria.list();
    return records;
  }

  @Override
  public Partition getPartitionById(long partitionId) {
    return (Partition) currentSession().get(PartitionImpl.class, partitionId);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public PoreVersion getPoreVersion(long id) {
    return (PoreVersion) currentSession().get(PoreVersion.class, id);
  }

  @Override
  public List<PoreVersion> listPoreVersions() {
    Criteria criteria = currentSession().createCriteria(PoreVersion.class);
    @SuppressWarnings("unchecked")
    List<PoreVersion> results = criteria.list();
    return results;
  }
}
