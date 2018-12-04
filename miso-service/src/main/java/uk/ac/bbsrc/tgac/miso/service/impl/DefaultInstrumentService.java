package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.InstrumentStore;
import uk.ac.bbsrc.tgac.miso.core.store.ServiceRecordStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.service.RunService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultInstrumentService implements InstrumentService {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private InstrumentStore instrumentDao;
  @Autowired
  private ServiceRecordStore serviceRecordDao;
  @Autowired
  private RunService runService;

  @Override
  public Collection<Instrument> list() throws IOException {
    return instrumentDao.listAll();
  }

  @Override
  public Collection<Instrument> listByPlatformType(PlatformType platformType) throws IOException {
    return instrumentDao.list(0, 0, true, "id", PaginationFilter.platformType(platformType), PaginationFilter.archived(false));
  }

  @Override
  public Instrument get(long instrumentId) throws IOException {
    return instrumentDao.get(instrumentId);
  }

  @Override
  public Instrument getByName(String name) throws IOException {
    return instrumentDao.getByName(name);
  }

  @Override
  public Instrument getByUpgradedInstrumentId(long instrumentId) throws IOException {
    return instrumentDao.getByUpgradedInstrument(instrumentId);
  }

  @Override
  public Long create(Instrument instrument) throws IOException {
    authorizationManager.throwIfNonAdmin();
    loadChildEntities(instrument);
    return save(instrument);
  }

  @Override
  public void update(Instrument instrument) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Instrument managed = get(instrument.getId());
    applyChanges(managed, instrument);
    loadChildEntities(managed);
    save(managed);
  }

  private long save(Instrument instrument) throws IOException {
    return instrumentDao.save(instrument);
  }

  private void applyChanges(Instrument target, Instrument source) {
    target.setPlatform(source.getPlatform());
    target.setName(source.getName());
    target.setSerialNumber(source.getSerialNumber());
    target.setDateCommissioned(source.getDateCommissioned());
    target.setDateDecommissioned(source.getDateDecommissioned());
    target.setUpgradedInstrument(source.getUpgradedInstrument());
  }

  private void loadChildEntities(Instrument instrument) throws IOException {
    if (instrument.getUpgradedInstrument() != null) {
      instrument.setUpgradedInstrument(get(instrument.getUpgradedInstrument().getId()));
    } else {
      instrument.setUpgradedInstrument(null);
    }
  }

  @Override
  public Map<String, Integer> getColumnSizes() throws IOException {
    return instrumentDao.getInstrumentColumnSizes();
  }

  public void setInstrumentDao(InstrumentStore instrumentDao) {
    this.instrumentDao = instrumentDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return instrumentDao.count(errorHandler, filter);
  }

  @Override
  public List<Instrument> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return instrumentDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

}
