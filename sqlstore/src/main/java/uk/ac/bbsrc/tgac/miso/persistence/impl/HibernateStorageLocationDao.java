package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.persistence.StorageLocationStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateStorageLocationDao implements StorageLocationStore {

  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public StorageLocation get(long id) {
    return (StorageLocation) currentSession().get(StorageLocation.class, id);
  }

  @Override
  public StorageLocation getByBarcode(String barcode) {
    return (StorageLocation) currentSession().createCriteria(StorageLocation.class)
        .add(Restrictions.eq("identificationBarcode", barcode))
        .uniqueResult();
  }

  @Override
  public List<StorageLocation> listRooms() {
    Criteria criteria = currentSession().createCriteria(StorageLocation.class)
        .add(Restrictions.eq("locationUnit", LocationUnit.ROOM));
    @SuppressWarnings("unchecked")
    List<StorageLocation> list = criteria.list();
    return list;
  }

  @Override
  public List<StorageLocation> listFreezers() {
    Criteria criteria = currentSession().createCriteria(StorageLocation.class)
        .add(Restrictions.eq("locationUnit", LocationUnit.FREEZER));
    @SuppressWarnings("unchecked")
    List<StorageLocation> list = criteria.list();
    return list;
  }

  @Override
  public long save(StorageLocation location) {
    if (!location.isSaved()) {
      return (long) currentSession().save(location);
    } else {
      currentSession().update(location);
      return location.getId();
    }
  }

  @Override
  public StorageLocation getByServiceRecord(ServiceRecord record) throws IOException {
    return (StorageLocation) currentSession().createCriteria(StorageLocation.class)
        .createAlias("serviceRecords", "serviceRecords")
        .add(Restrictions.eq("serviceRecords.recordId", record.getId()))
        .uniqueResult();
  }

}
