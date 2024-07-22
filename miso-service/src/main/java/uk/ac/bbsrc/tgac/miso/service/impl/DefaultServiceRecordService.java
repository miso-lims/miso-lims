package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.ServiceRecordService;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.persistence.ServiceRecordStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultServiceRecordService implements ServiceRecordService {

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private FileAttachmentService fileAttachmentService;

  @Autowired
  private ServiceRecordStore serviceRecordDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private InstrumentService instrumentService;

  @Autowired
  private StorageLocationService storageLocationService;

  @Override
  public ServiceRecord get(long recordId) throws IOException {
    return serviceRecordDao.get(recordId);
  }

  @Override
  public long create(ServiceRecord record) throws IOException {
    return serviceRecordDao.create(record);
  }

  @Override
  public long update(ServiceRecord record) throws IOException {
    ServiceRecord managed = get(record.getId());
    validateChange(record, managed);
    applyRecordChanges(managed, record);
    return serviceRecordDao.update(managed);
  }

  private void applyRecordChanges(ServiceRecord target, ServiceRecord source) throws IOException {

    target.setTitle(source.getTitle());
    target.setDetails(source.getDetails());
    if (source.getPosition() == null) {
      target.setPosition(null);
    } else {
      Instrument instrument = instrumentService.getByServiceRecord(target);
      target
          .setPosition(instrument.findPosition(source.getPosition().getId()));
    }
    target.setServicedByName(source.getServicedByName());
    target.setReferenceNumber(source.getReferenceNumber());
    target.setServiceDate(source.getServiceDate());
    target.setStartTime(source.getStartTime());
    target.setOutOfService(source.isOutOfService());
    target.setEndTime(source.getEndTime());
  }

  private void validateChange(ServiceRecord record, ServiceRecord beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    Instrument instrument = instrumentService.getByServiceRecord(record);

    if (instrument != null && instrumentService.getByServiceRecord(beforeChange).getDateDecommissioned() != null) {
      throw new IOException("Cannot add service records to a retired instrument!");
    }

    if (record.getPosition() != null) {
      if (instrument == null) {
        errors.add(new ValidationError("Position cannot be set if the record is not for an instrument"));
      } else if (instrument.findPosition(record.getPosition().getId()) == null) {
        errors.add(new ValidationError("position", "Position must belong to the same instrument as this record"));
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  public void setServiceRecordDao(ServiceRecordStore serviceRecordDao) {
    this.serviceRecordDao = serviceRecordDao;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public void beforeDelete(ServiceRecord object) throws IOException {
    ServiceRecord managedRecord = get(object.getId());

    StorageLocation freezer = storageLocationService.getByServiceRecord(object);

    if (freezer != null) {
      freezer.getServiceRecords().remove(managedRecord);
      storageLocationService.saveFreezer(freezer);
    } else {
      Instrument instrument = instrumentService.getByServiceRecord(object);
      if (instrument != null) {
        instrument.getServiceRecords().remove(managedRecord);
        instrumentService.update(instrument);
      }
    }
    fileAttachmentService.beforeDelete(object);
  }

  @Override
  public void afterDelete(ServiceRecord object) throws IOException {
    fileAttachmentService.afterDelete(object);
  }

}
