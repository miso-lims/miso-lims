package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.persistence.SampleAdditionalInfoDao;

@Transactional
public class HibernateSampleAdditionalInfoDao implements SampleAdditionalInfoDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleAdditionalInfoDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }
  
  @Autowired
  private KitStore kitStore;
  
  public void setKitStore(KitStore kitStore) {
    this.kitStore = kitStore;
  }
  
  private SampleAdditionalInfo fetchSqlStore(SampleAdditionalInfo sampleAdditionalInfo) throws IOException {
    if (sampleAdditionalInfo != null) {
      sampleAdditionalInfo.setPrepKit(kitStore.getKitDescriptorById(sampleAdditionalInfo.getHibernateKitDescriptorId()));
    }
    return sampleAdditionalInfo;
  }
  
  private Collection<SampleAdditionalInfo> fetchSqlStore(Collection<SampleAdditionalInfo> sampleAdditionalInfos) throws IOException {
    for (SampleAdditionalInfo sampleAdditionalInfo : sampleAdditionalInfos) {
      fetchSqlStore(sampleAdditionalInfo);
    }
    return sampleAdditionalInfos;
  }

  @Override
  public List<SampleAdditionalInfo> getSampleAdditionalInfo() throws IOException {
    Query query = currentSession().createQuery("from SampleAdditionalInfoImpl");
    @SuppressWarnings("unchecked")
    List<SampleAdditionalInfo> records = query.list();
    fetchSqlStore(records);
    return records;
  }

  @Override
  public SampleAdditionalInfo getSampleAdditionalInfoBySampleId(Long id) throws IOException {
    Query query = currentSession().createQuery("from SampleAdditionalInfoImpl where sampleId = :id");
    query.setLong("id", id);
    SampleAdditionalInfo record = (SampleAdditionalInfo) query.uniqueResult();
    return record == null ? null : fetchSqlStore(record);
  }

  @Override
  public SampleAdditionalInfo getSampleAdditionalInfo(Long id) throws IOException {
    SampleAdditionalInfo record = (SampleAdditionalInfo) currentSession().get(SampleAdditionalInfoImpl.class, id);
    return fetchSqlStore(record);
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
