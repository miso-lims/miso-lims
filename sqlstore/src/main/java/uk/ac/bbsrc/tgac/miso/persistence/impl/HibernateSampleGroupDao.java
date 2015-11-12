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

import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleGroupImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SampleGroupDao;

@Repository
@Transactional
public class HibernateSampleGroupDao implements SampleGroupDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleGroupDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<SampleGroupId> getSampleGroupId() {
    Query query = currentSession().createQuery("from SampleGroupIdImpl");
    @SuppressWarnings("unchecked")
    List<SampleGroupId> records = query.list();
    return records;
  }

  @Override
  public SampleGroupId getSampleGroupId(Long id) {
    return (SampleGroupId) currentSession().get(SampleGroupImpl.class, id);
  }

  @Override
  public Long addSampleGroupId(SampleGroupId sampleGroup) {
    Date now = new Date();
    sampleGroup.setCreationDate(now);
    sampleGroup.setLastUpdated(now);
    return (Long) currentSession().save(sampleGroup);
  }

  @Override
  public void deleteSampleGroupId(SampleGroupId sampleGroup) {
    currentSession().delete(sampleGroup);

  }

  @Override
  public void update(SampleGroupId sampleGroup) {
    Date now = new Date();
    sampleGroup.setLastUpdated(now);
    currentSession().update(sampleGroup);
  }

}
