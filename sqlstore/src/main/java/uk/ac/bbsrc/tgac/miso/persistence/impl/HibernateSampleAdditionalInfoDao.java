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

import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SampleAdditionalInfoDao;

@Repository
@Transactional
public class HibernateSampleAdditionalInfoDao implements SampleAdditionalInfoDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleAdditionalInfoDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<SampleAdditionalInfo> getSampleAdditionalInfo() {
    Query query = currentSession().createQuery("from SampleAdditionalInfoImpl");
    @SuppressWarnings("unchecked")
    List<SampleAdditionalInfo> records = query.list();
    return records;
  }

  @Override
  public SampleAdditionalInfo getSampleAdditionalInfo(Long id) {
    return (SampleAdditionalInfo) currentSession().get(SampleAdditionalInfoImpl.class, id);
  }

  @Override
  public Long addSampleAdditionalInfo(SampleAdditionalInfo sampleAdditionalInfo) {
    Date now = new Date();
    sampleAdditionalInfo.setCreationDate(now);
    sampleAdditionalInfo.setLastUpdated(now);
    return (Long) currentSession().save(sampleAdditionalInfo);
  }

  @Override
  public void deleteSampleAdditionalInfo(SampleAdditionalInfo sampleAdditionalInfo) {
    currentSession().delete(sampleAdditionalInfo);

  }

  @Override
  public void update(SampleAdditionalInfo sampleAdditionalInfo) {
    Date now = new Date();
    sampleAdditionalInfo.setLastUpdated(now);
    currentSession().update(sampleAdditionalInfo);
  }

}
