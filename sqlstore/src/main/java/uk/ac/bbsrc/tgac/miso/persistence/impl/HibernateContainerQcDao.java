package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.store.ContainerQcStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateContainerQcDao implements ContainerQcStore {

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public QC get(long id) throws IOException {
    return (ContainerQC) currentSession().get(ContainerQC.class, id);
  }

  @Override
  public QualityControlEntity getEntity(long id) throws IOException {
    return (SequencerPartitionContainer) currentSession().get(SequencerPartitionContainerImpl.class, id);
  }

  @Override
  public Collection<? extends QC> listForEntity(long id) throws IOException {
    return ((SequencerPartitionContainer) currentSession().get(SequencerPartitionContainerImpl.class, id)).getQCs();
  }

  @Override
  public long save(QC qc) throws IOException {
    ContainerQC containerQC = (ContainerQC) qc;
    if (containerQC.getId() == QC.UNSAVED_ID) {
      return (Long) currentSession().save(containerQC);
    } else {
      currentSession().update(containerQC);
      return containerQC.getId();
    }
  }

}
