package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSamplePurposeDao implements SamplePurposeDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSamplePurposeDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<SamplePurpose> getSamplePurpose() {
    Query query = currentSession().createQuery("from SamplePurposeImpl");
    @SuppressWarnings("unchecked")
    List<SamplePurpose> records = query.list();
    return records;
  }

  @Override
  public SamplePurpose getSamplePurpose(Long id) {
    return (SamplePurpose) currentSession().get(SamplePurposeImpl.class, id);
  }

  @Override
  public Long addSamplePurpose(SamplePurpose samplePurpose) {
    Date now = new Date();
    samplePurpose.setCreationDate(now);
    samplePurpose.setLastUpdated(now);
    return (Long) currentSession().save(samplePurpose);
  }

  @Override
  public void update(SamplePurpose samplePurpose) {
    Date now = new Date();
    samplePurpose.setLastUpdated(now);
    currentSession().update(samplePurpose);
  }

  @Override
  public long getUsage(SamplePurpose samplePurpose) {
    return (long) currentSession().createCriteria(SampleAliquotImpl.class)
        .add(Restrictions.eq("samplePurpose", samplePurpose))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

}
