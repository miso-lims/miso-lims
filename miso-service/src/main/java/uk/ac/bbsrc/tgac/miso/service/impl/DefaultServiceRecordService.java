package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.PlatformPosition;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.ServiceRecordStore;
import uk.ac.bbsrc.tgac.miso.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.service.ServiceRecordService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

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

  @Override
  public Collection<ServiceRecord> listByInstrument(long instrumentId) throws IOException {
    return serviceRecordDao.listByInstrumentId(instrumentId);
  }

  @Override
  public ServiceRecord get(long recordId) throws IOException {
    return serviceRecordDao.get(recordId);
  }

  @Override
  public long create(ServiceRecord record) throws IOException {
    record.setInstrument(instrumentService.get(record.getInstrument().getId()));
    return serviceRecordDao.save(record);
  }

  @Override
  public void update(ServiceRecord record) throws IOException {
    ServiceRecord managed = get(record.getId());
    record.setInstrument(instrumentService.get(managed.getInstrument().getId()));
    validateChange(record, managed);
    applyRecordChanges(managed, record);
    serviceRecordDao.save(managed);
  }

  private void applyRecordChanges(ServiceRecord target, ServiceRecord source) {
    target.setTitle(source.getTitle());
    target.setDetails(source.getDetails());
    if (source.getPosition() == null) {
      target.setPosition(null);
    } else {
      target.setPosition(findPosition(source.getPosition().getId(), target.getInstrument()));
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

    if (record.getPosition() != null && findPosition(record.getPosition().getId(), record.getInstrument()) == null) {
      errors.add(new ValidationError("position", "Position must belong to the same instrument as this record"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private PlatformPosition findPosition(long id, Instrument instrument) {
    return instrument.getPlatform().getPositions().stream()
        .filter(p -> p.getId() == id)
        .findFirst().orElse(null);
  }

  @Override
  public Map<String, Integer> getColumnSizes() throws IOException {
    return serviceRecordDao.getServiceRecordColumnSizes();
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
  public void afterDelete(ServiceRecord object) {
    fileAttachmentService.afterDelete(object);
  }

}
