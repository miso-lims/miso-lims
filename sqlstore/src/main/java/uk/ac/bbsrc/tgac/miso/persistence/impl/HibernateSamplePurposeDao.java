package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;

@Repository
@Transactional
public class HibernateSamplePurposeDao implements SamplePurposeDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSamplePurposeDao.class);

  @Autowired
  private SessionFactory sessionFactory;

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
  public void deleteSamplePurpose(SamplePurpose samplePurpose) {
    currentSession().delete(samplePurpose);

  }

  @Override
  public void update(SamplePurpose samplePurpose) {
    Date now = new Date();
    samplePurpose.setLastUpdated(now);
    currentSession().update(samplePurpose);
  }

}
