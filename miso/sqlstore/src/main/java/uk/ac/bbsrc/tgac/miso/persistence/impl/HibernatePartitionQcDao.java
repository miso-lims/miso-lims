package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQC;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQC.PartitionQCId;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.store.PartitionQcStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernatePartitionQcDao implements PartitionQcStore {
  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public void create(PartitionQC qc) throws IOException {
    currentSession().save(qc);
  }

  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public PartitionQC get(Run run, Partition partition) throws IOException {
    PartitionQCId id = new PartitionQCId();
    id.setRun(run);
    id.setPartition(partition);
    return (PartitionQC) currentSession().get(PartitionQC.class, id);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public PartitionQCType getType(long qcTypeId) throws IOException {
    return (PartitionQCType) currentSession().get(PartitionQCType.class, qcTypeId);
  }

  @Override
  public Collection<PartitionQCType> listTypes() {
    Criteria criteria = currentSession().createCriteria(PartitionQCType.class);
    @SuppressWarnings("unchecked")
    List<PartitionQCType> records = criteria.list();
    return records;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void update(PartitionQC managedQc) throws IOException {
    currentSession().update(managedQc);
  }

}
