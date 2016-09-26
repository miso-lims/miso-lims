package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedSampleDao;

@Transactional(rollbackFor = Exception.class)
public class HibernateDetailedSampleDao implements DetailedSampleDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateDetailedSampleDao.class);

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
  
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
  
  private DetailedSample fetchSqlStore(DetailedSample detailedSample) throws IOException {
    if (detailedSample != null) {
      if (detailedSample.getHibernateKitDescriptorId() != null) {
        KitDescriptor kit = kitStore.getKitDescriptorById(detailedSample.getHibernateKitDescriptorId());
        detailedSample.setPrepKit(kit);
      }
    }
    return detailedSample;
  }
  
  private Collection<DetailedSample> fetchSqlStore(Collection<DetailedSample> detailedSamples) throws IOException {
    for (DetailedSample detailedSample : detailedSamples) {
      fetchSqlStore(detailedSample);
    }
    return detailedSamples;
  }

  @Override
  public List<DetailedSample> getDetailedSample() throws IOException {
    Query query = currentSession().createQuery("from DetailedSampleImpl");
    @SuppressWarnings("unchecked")
    List<DetailedSample> records = query.list();
    fetchSqlStore(records);
    return records;
  }

  @Override
  public DetailedSample getDetailedSampleBySampleId(Long id) throws IOException {
    Query query = currentSession().createQuery("from DetailedSampleImpl where sampleId = :id");
    query.setLong("id", id);
    DetailedSample record = (DetailedSample) query.uniqueResult();
    return record == null ? null : fetchSqlStore(record);
  }

  @Override
  public DetailedSample getDetailedSample(Long id) throws IOException {
    DetailedSample record = (DetailedSample) currentSession().get(DetailedSampleImpl.class, id);
    return fetchSqlStore(record);
  }

  @Override
  public void deleteDetailedSample(DetailedSample detailedSample) {
    currentSession().delete(detailedSample);
  }

}
