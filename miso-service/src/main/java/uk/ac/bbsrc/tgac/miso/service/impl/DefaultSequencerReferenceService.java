package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerReferenceStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerServiceRecordStore;
import uk.ac.bbsrc.tgac.miso.service.SequencerReferenceService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSequencerReferenceService implements SequencerReferenceService {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private SequencerReferenceStore sequencerReferenceDao;
  @Autowired
  private SequencerServiceRecordStore serviceRecordDao;
  @Autowired
  private RunService runService;

  @Override
  public Collection<SequencerReference> list() throws IOException {
    return sequencerReferenceDao.listAll();
  }

  @Override
  public Collection<SequencerReference> listByPlatformType(PlatformType platformType) throws IOException {
    return sequencerReferenceDao.listByPlatformType(platformType);
  }

  @Override
  public Collection<SequencerServiceRecord> listServiceRecordsByInstrument(long instrumentId) throws IOException {
    return serviceRecordDao.listBySequencerId(instrumentId);
  }

  @Override
  public SequencerReference get(long instrumentId) throws IOException {
    return sequencerReferenceDao.get(instrumentId);
  }

  @Override
  public SequencerReference getByName(String name) throws IOException {
    return sequencerReferenceDao.getByName(name);
  }

  @Override
  public SequencerReference getByUpgradedReferenceId(long instrumentId) throws IOException {
    return sequencerReferenceDao.getByUpgradedReference(instrumentId);
  }

  @Override
  public SequencerServiceRecord getServiceRecord(long recordId) throws IOException {
    return serviceRecordDao.get(recordId);
  }

  @Override
  public Long create(SequencerReference instrument) throws IOException {
    authorizationManager.throwIfNonAdmin();
    loadChildEntities(instrument);
    return save(instrument);
  }

  @Override
  public void update(SequencerReference instrument) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SequencerReference managed = get(instrument.getId());
    applyChanges(managed, instrument);
    loadChildEntities(managed);
    save(managed);
  }

  private long save(SequencerReference instrument) throws IOException {
    return sequencerReferenceDao.save(instrument);
  }

  private void applyChanges(SequencerReference target, SequencerReference source) throws IOException {
    target.setPlatform(source.getPlatform());
    target.setName(source.getName());
    target.setIpAddress(source.getIpAddress());
    target.setSerialNumber(source.getSerialNumber());
    target.setDateCommissioned(source.getDateCommissioned());
    target.setDateDecommissioned(source.getDateDecommissioned());
    target.setUpgradedSequencerReference(source.getUpgradedSequencerReference());
  }

  private void loadChildEntities(SequencerReference instrument) throws IOException {
    if (instrument.getUpgradedSequencerReference() != null) {
      instrument.setUpgradedSequencerReference(get(instrument.getUpgradedSequencerReference().getId()));
    } else {
      instrument.setUpgradedSequencerReference(null);
    }
  }

  @Override
  public long createServiceRecord(SequencerServiceRecord record) throws IOException {
    record.setSequencerReference(get(record.getSequencerReference().getId()));
    return serviceRecordDao.save(record);
  }

  @Override
  public void updateServiceRecord(SequencerServiceRecord record) throws IOException {
    SequencerServiceRecord managed = getServiceRecord(record.getId());
    applyRecordChanges(managed, record);
    record.setSequencerReference(get(managed.getSequencerReference().getId()));
    serviceRecordDao.save(managed);
  }

  private void applyRecordChanges(SequencerServiceRecord target, SequencerServiceRecord source) {
    target.setTitle(source.getTitle());
    target.setDetails(source.getDetails());
    target.setServicedByName(source.getServicedByName());
    target.setReferenceNumber(source.getReferenceNumber());
    target.setServiceDate(source.getServiceDate());
    target.setShutdownTime(source.getShutdownTime());
    target.setRestoredTime(source.getRestoredTime());
  }

  @Override
  public Map<String, Integer> getSequencerReferenceColumnSizes() throws IOException {
    return sequencerReferenceDao.getSequencerReferenceColumnSizes();
  }

  @Override
  public Map<String, Integer> getServiceRecordColumnSizes() throws IOException {
    return serviceRecordDao.getServiceRecordColumnSizes();
  }

  @Override
  public void deleteServiceRecord(long recordId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    serviceRecordDao.remove(getServiceRecord(recordId));
  }

  @Override
  public void delete(long instrumentId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Collection<Run> attachedRuns = runService.listBySequencerId(instrumentId);
    if (attachedRuns.isEmpty()) {
      sequencerReferenceDao.remove(get(instrumentId));
    } else {
      throw new ConstraintViolationException("Cannot delete sequencer reference since runs are still associated.", null,
          "sequencerReference");
    }
  }

  public void setSequencerReferenceDao(SequencerReferenceStore sequencerReferenceDao) {
    this.sequencerReferenceDao = sequencerReferenceDao;
  }

  public void setServiceRecordDao(SequencerServiceRecordStore serviceRecordDao) {
    this.serviceRecordDao = serviceRecordDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

}
