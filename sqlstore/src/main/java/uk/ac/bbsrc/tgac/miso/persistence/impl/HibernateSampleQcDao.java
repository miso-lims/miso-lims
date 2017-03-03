package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
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

import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
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
  public long save(SampleQC sampleQc) throws IOException {
    long id;
    if (sampleQc.getId() == SampleQCImpl.UNSAVED_ID) {
      id = (long) currentSession().save(sampleQc);
    } else {
      currentSession().update(sampleQc);
      id = sampleQc.getId();
    }
    return id;
  }

  @Override
  public SampleQC get(long id) throws IOException {
    return (SampleQC) currentSession().get(SampleQCImpl.class, id);
  }

  @Override
  public Collection<SampleQC> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleQCImpl.class);
    @SuppressWarnings("unchecked")
    List<SampleQC> records = criteria.list();
    return records;
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleQCImpl.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public boolean remove(SampleQC sampleQc) throws IOException {
    if (sampleQc.isDeletable()) {
      currentSession().delete(sampleQc);
      SampleQC testIfExists = get(sampleQc.getId());

      return testIfExists == null;
    } else {
      return false;
    }
  }

  @Override
  public Collection<SampleQC> listBySampleId(long sampleId) throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleQCImpl.class);
    criteria.add(Restrictions.eq("sample.id", sampleId));
    @SuppressWarnings("unchecked")
    Collection<SampleQC> records = criteria.list();
    return records;
  }

  @Override
  public QcType getSampleQcTypeById(long qcTypeId) throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    criteria.add(Restrictions.eq("qcTarget", "Sample"));
    criteria.add(Restrictions.eq("id", qcTypeId));
    return (QcType) criteria.uniqueResult();
  }

  @Override
  public QcType getSampleQcTypeByName(String qcName) throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    criteria.add(Restrictions.eq("qcTarget", "Sample"));
    criteria.add(Restrictions.eq("name", qcName));
    return (QcType) criteria.uniqueResult();
  }

  @Override
  public Collection<QcType> listAllSampleQcTypes() throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    criteria.add(Restrictions.eq("qcTarget", "Sample"));
    @SuppressWarnings("unchecked")
    Collection<QcType> records = criteria.list();
    return records;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
