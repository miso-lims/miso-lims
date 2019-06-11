package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.InstrumentStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultInstrumentService implements InstrumentService {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private InstrumentStore instrumentDao;

  @Override
  public List<Instrument> list() throws IOException {
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
  public long create(Instrument instrument) throws IOException {
    authorizationManager.throwIfNonAdmin();
    loadChildEntities(instrument);
    validateChange(instrument, null);
    return save(instrument);
  }

  @Override
  public long update(Instrument instrument) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Instrument managed = get(instrument.getId());
    validateChange(instrument, managed);
    applyChanges(managed, instrument);
    loadChildEntities(managed);
    return save(managed);
  }

  private long save(Instrument instrument) throws IOException {
    return instrumentDao.save(instrument);
  }

  private void validateChange(Instrument instrument, Instrument beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(Instrument::getName, instrument, beforeChange) && getByName(instrument.getName()) != null) {
      errors.add(new ValidationError("name", "There is already an instrument with this name"));
    }
    if (ValidationUtils.isSetAndChanged(Instrument::getUpgradedInstrument, instrument, beforeChange)
        && getByUpgradedInstrumentId(instrument.getUpgradedInstrument().getId()) != null) {
      errors.add(new ValidationError("upgradedInstrumentId", "There is already an instrument that has been upgraded to this one"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(Instrument target, Instrument source) {
    target.setInstrumentModel(source.getInstrumentModel());
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

  public void setInstrumentDao(InstrumentStore instrumentDao) {
    this.instrumentDao = instrumentDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
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
