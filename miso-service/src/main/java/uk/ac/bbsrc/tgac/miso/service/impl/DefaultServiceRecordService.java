package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.ServiceRecordStore;
import uk.ac.bbsrc.tgac.miso.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.service.ServiceRecordService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultServiceRecordService implements ServiceRecordService {

  private static final Logger log = LoggerFactory.getLogger(DefaultServiceRecordService.class);

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private MisoFilesManager misoFilesManager;

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
    applyRecordChanges(managed, record);
    record.setInstrument(instrumentService.get(managed.getInstrument().getId()));
    serviceRecordDao.save(managed);
  }

  private void applyRecordChanges(ServiceRecord target, ServiceRecord source) {
    target.setTitle(source.getTitle());
    target.setDetails(source.getDetails());
    target.setServicedByName(source.getServicedByName());
    target.setReferenceNumber(source.getReferenceNumber());
    target.setServiceDate(source.getServiceDate());
    target.setShutdownTime(source.getShutdownTime());
    target.setRestoredTime(source.getRestoredTime());
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
    // Attempts to delete all attachments associated with a service record. Does not throw an exception upon failure to delete a file,
    // but will log any failures
    long recordId = object.getId();
    Collection<String> filenames = null;

    try {
      filenames = misoFilesManager.getFileNames(ServiceRecord.class, String.valueOf(recordId));
    } catch (IOException e) {
      log.error("Deleted service record " + recordId + ", but failed to delete attachments", e);
      return;
    }

    for (String filename : filenames) {
      try {
        misoFilesManager.deleteFile(ServiceRecord.class, String.valueOf(recordId), filename);
      } catch (IOException e) {
        log.error("Deleted service record " + recordId + ", but failed to delete attachment: " + filename, e);
      }
    }
  }

}
