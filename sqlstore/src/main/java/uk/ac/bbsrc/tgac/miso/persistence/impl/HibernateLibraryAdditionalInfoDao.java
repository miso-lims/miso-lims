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

import uk.ac.bbsrc.tgac.miso.core.data.LibraryAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryAdditionalInfoDao;

@Transactional
public class HibernateLibraryAdditionalInfoDao implements LibraryAdditionalInfoDao {
  
  protected static final Logger log = LoggerFactory.getLogger(HibernateLibraryAdditionalInfoDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Autowired
  private KitStore kitStore;
  
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
  
  public void setKitStore(KitStore kitStore) {
    this.kitStore = kitStore;
  }
  
  private LibraryAdditionalInfo fetchSqlStore(LibraryAdditionalInfo libraryAdditionalInfo) throws IOException {
    if (libraryAdditionalInfo != null && libraryAdditionalInfo.getHibernateKitDescriptorId() != null) {
      libraryAdditionalInfo.setPrepKit(kitStore.getKitDescriptorById(libraryAdditionalInfo.getHibernateKitDescriptorId()));
    }
    return libraryAdditionalInfo;
  }
  
  private Collection<LibraryAdditionalInfo> fetchSqlStore(Collection<LibraryAdditionalInfo> libraryAdditionalInfos) throws IOException {
    for (LibraryAdditionalInfo libraryAdditionalInfo : libraryAdditionalInfos) {
      fetchSqlStore(libraryAdditionalInfo);
    }
    return libraryAdditionalInfos;
  }

  @Override
  public List<LibraryAdditionalInfo> getLibraryAdditionalInfo() throws IOException {
    Query query = currentSession().createQuery("from LibraryAdditionalInfoImpl");
    @SuppressWarnings("unchecked")
    List<LibraryAdditionalInfo> info = query.list();
    fetchSqlStore(info);
    return info;
  }

  @Override
  public LibraryAdditionalInfo getLibraryAdditionalInfo(Long id) throws IOException {
    LibraryAdditionalInfo info = (LibraryAdditionalInfo) currentSession().get(LibraryAdditionalInfoImpl.class, id);
    return fetchSqlStore(info);
  }

  @Override
  public LibraryAdditionalInfo getLibraryAdditionalInfoByLibraryId(Long id) throws IOException {
    Query query = currentSession().createQuery("from LibraryAdditionalInfoImpl where libraryId = :id");
    query.setLong("id", id);
    @SuppressWarnings("unchecked")
    List<LibraryAdditionalInfo> records = query.list();
    return records.isEmpty() ? null : fetchSqlStore(records.get(0));
  }

  @Override
  public Long addLibraryAdditionalInfo(LibraryAdditionalInfo libraryAdditionalInfo) {
      Date now = new Date();
      libraryAdditionalInfo.setCreationDate(now);
      libraryAdditionalInfo.setLastUpdated(now);
      return (Long) currentSession().save(libraryAdditionalInfo);
  }
  
  @Override
  public void deleteLibraryAdditionalInfo(
      LibraryAdditionalInfo libraryAdditionalInfo) {
    currentSession().delete(libraryAdditionalInfo);
    
  }

  @Override
  public void update(LibraryAdditionalInfo libraryAdditionalInfo) {
    Date now = new Date();
    libraryAdditionalInfo.setLastUpdated(now);
    currentSession().update(libraryAdditionalInfo);
  }
  
}
