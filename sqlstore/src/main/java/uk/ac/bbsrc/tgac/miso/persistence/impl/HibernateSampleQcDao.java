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

import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleQcDao implements SampleQcStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleQcDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public SampleQC get(long id) throws IOException {
    return (SampleQC) currentSession().get(SampleQC.class, id);
  }

  @Override
  public QualityControlEntity getEntity(long id) throws IOException {
    return (Sample) currentSession().get(SampleImpl.class, id);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public Collection<? extends QC> listForEntity(long id) throws IOException {
    return ((Sample) currentSession().get(SampleImpl.class, id)).getQCs();
  }

  @Override
  public long save(QC qc) throws IOException {
    SampleQC sampleQc = (SampleQC) qc;
    long id;
    if (sampleQc.getId() == SampleQC.UNSAVED_ID) {
      id = (long) currentSession().save(sampleQc);
    } else {
      currentSession().update(sampleQc);
      id = sampleQc.getId();
    }
    return id;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
