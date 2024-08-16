package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation_;
import uk.ac.bbsrc.tgac.miso.persistence.StorageLocationStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateStorageLocationDao extends HibernateSaveDao<StorageLocation> implements StorageLocationStore {

  public HibernateStorageLocationDao() {
    super(StorageLocation.class);
  }

  @Override
  public StorageLocation getByBarcode(String barcode) {
    return getBy(StorageLocation_.identificationBarcode, barcode);
  }

  @Override
  public StorageLocation getByProbeId(String probeId) throws IOException {
    return getBy(StorageLocation_.probeId, probeId);
  }

  @Override
  public List<StorageLocation> listRooms() {
    QueryBuilder<StorageLocation, StorageLocation> builder =
        new QueryBuilder<>(currentSession(), StorageLocation.class, StorageLocation.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(StorageLocation_.locationUnit), LocationUnit.ROOM));
    return builder.getResultList();
  }

  @Override
  public List<StorageLocation> listFreezers() {
    QueryBuilder<StorageLocation, StorageLocation> builder =
        new QueryBuilder<>(currentSession(), StorageLocation.class, StorageLocation.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(StorageLocation_.locationUnit), LocationUnit.FREEZER));
    return builder.getResultList();
  }

  @Override
  public StorageLocation getByServiceRecord(ServiceRecord record) throws IOException {
    QueryBuilder<StorageLocation, StorageLocation> builder =
        new QueryBuilder<>(currentSession(), StorageLocation.class, StorageLocation.class);
    Join<StorageLocation, ServiceRecord> serviceRecords =
        builder.getJoin(builder.getRoot(), StorageLocation_.serviceRecords);
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(serviceRecords.get(ServiceRecord_.recordId), record.getId()));
    return builder.getSingleResultOrNull();
  }

}
