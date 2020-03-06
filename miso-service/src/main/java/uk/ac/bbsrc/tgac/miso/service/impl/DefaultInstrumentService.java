package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.InstrumentStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultInstrumentService implements InstrumentService {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private InstrumentStore instrumentDao;
  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private RunPurposeService runPurposeService;

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
    loadChildEntities(instrument);
    validateChange(instrument, managed);
    applyChanges(managed, instrument);
    return save(managed);
  }

  private long save(Instrument instrument) throws IOException {
    return instrumentDao.save(instrument);
  }

  private void validateChange(Instrument instrument, Instrument beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (isSetAndChanged(Instrument::getName, instrument, beforeChange) && getByName(instrument.getName()) != null) {
      errors.add(new ValidationError("name", "There is already an instrument with this name"));
    }
    if (isSetAndChanged(Instrument::getUpgradedInstrument, instrument, beforeChange)
        && getByUpgradedInstrumentId(instrument.getUpgradedInstrument().getId()) != null) {
      errors.add(new ValidationError("upgradedInstrumentId", "There is already an instrument that has been upgraded to this one"));
    }
    if (instrument.getInstrumentModel().getInstrumentType() == InstrumentType.SEQUENCER) {
      if (instrument.getDefaultRunPurpose() == null) {
        errors.add(new ValidationError("defaultRunPurposeId", "Required for sequencing instruments"));
      }
    } else {
      if (instrument.getDefaultRunPurpose() != null) {
        errors.add(new ValidationError("defaultRunPurposeId", "Invalid for non-sequencing instruments"));
      }
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
    target.setDefaultRunPurpose(source.getDefaultRunPurpose());
  }

  private void loadChildEntities(Instrument instrument) throws IOException {
    loadChildEntity(instrument::setUpgradedInstrument, instrument.getUpgradedInstrument(), this, "upgradedInstrumentId");
    loadChildEntity(instrument::setInstrumentModel, instrument.getInstrumentModel(), instrumentModelService, "instrumentModelId");
    loadChildEntity(instrument::setDefaultRunPurpose, instrument.getDefaultRunPurpose(), runPurposeService, "defaultRunPurposeId");
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

  @Override
  public List<Instrument> listByType(InstrumentType type) throws IOException {
    return instrumentDao.listByType(type);
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
  public ValidationResult validateDeletion(Instrument object) throws IOException {
    ValidationResult result = new ValidationResult();
    long runUsage = instrumentDao.getUsageByRuns(object);
    if (runUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, runUsage, "sequencer " + Pluralizer.runs(runUsage)));
    }
    long arrayRunUsage = instrumentDao.getUsageByArrayRuns(object);
    if (arrayRunUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, arrayRunUsage, "array " + Pluralizer.runs(arrayRunUsage)));
    }
    long qcUsage = instrumentDao.getUsageByQcs(object);
    if (qcUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, qcUsage, Pluralizer.qcs(qcUsage)));
    }
    return result;
  }

}
