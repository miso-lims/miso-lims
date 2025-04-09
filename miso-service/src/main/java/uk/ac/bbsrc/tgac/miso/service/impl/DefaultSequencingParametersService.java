package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaChemistry;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingParametersDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSequencingParametersService extends AbstractSaveService<SequencingParameters> implements SequencingParametersService {

  @Autowired
  private SequencingParametersDao sequencingParametersDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private InstrumentModelService instrumentModelService;

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<SequencingParameters> list() throws IOException {
    return sequencingParametersDao.list();
  }

  @Override
  public List<SequencingParameters> listByInstrumentModelId(long instrumentModelId) throws IOException {
    InstrumentModel model = instrumentModelService.get(instrumentModelId);
    return sequencingParametersDao.listByInstrumentModel(model);
  }

  @Override
  public List<SequencingParameters> listByIdList(List<Long> ids) throws IOException {
    return sequencingParametersDao.listByIdList(ids);
  }

  public void setSequencingParametersDao(SequencingParametersDao sequencingParametersDao) {
    this.sequencingParametersDao = sequencingParametersDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
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
  public SaveDao<SequencingParameters> getDao() {
    return sequencingParametersDao;
  }

  @Override
  protected void authorizeUpdate(SequencingParameters object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void loadChildEntities(SequencingParameters object) throws IOException {
    loadChildEntity(object.getInstrumentModel(), object::setInstrumentModel, instrumentModelService);
  }

  @Override
  protected void collectValidationErrors(SequencingParameters params, SequencingParameters beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isSetAndChanged(SequencingParameters::getName, params, beforeChange)
        && sequencingParametersDao.getByNameAndInstrumentModel(params.getName(), params.getInstrumentModel()) != null) {
      errors.add(new ValidationError("name",
          String.format("There are already sequencing parameters for %s with this name.", params.getInstrumentModel().getAlias())));
    }

    validateReadLengths(params, errors);
    validateChemistry(params, errors);
    validateRunType(params, errors);
    validateMovieTime(params, errors);
  }

  private void validateReadLengths(SequencingParameters params, List<ValidationError> errors) {
    if (params.getReadLength() < 0) {
      errors.add(new ValidationError("read1Length", "Read lengths cannot be negative."));
    }
    if (params.getReadLength2() < 0) {
      errors.add(new ValidationError("read2Length", "Read lengths cannot be negative."));
    }
    if (params.getInstrumentModel().getPlatformType() != PlatformType.ILLUMINA) {
      if (params.getReadLength() != 0) {
        errors.add(new ValidationError("read1Length",
            String.format("Read lengths must be 0 for %s instruments.", params.getInstrumentModel().getPlatformType().getKey())));
      }
      if (params.getReadLength2() != 0) {
        errors.add(new ValidationError("read2Length",
            String.format("Read lengths must be 0 for %s instruments.", params.getInstrumentModel().getPlatformType().getKey())));
      }
    }
  }

  private void validateChemistry(SequencingParameters params, List<ValidationError> errors) {
    if (params.getChemistry() == null) {
      errors.add(new ValidationError("chemistry", "Chemistry must be specified"));
    } else if (params.getInstrumentModel().getPlatformType() != PlatformType.ILLUMINA
        && params.getChemistry() != IlluminaChemistry.UNKNOWN) {
      errors.add(new ValidationError("chemistry", "Chemistry must be 'UNKNOWN' for non-Illumina instruments"));
    }
  }

  private void validateRunType(SequencingParameters params, List<ValidationError> errors) {
    if (params.getInstrumentModel().getPlatformType() == PlatformType.OXFORDNANOPORE) {
      if (params.getRunType() == null) {
        errors.add(new ValidationError("runType", "Run type must be specified for ONT instruments"));
      }
    } else if (params.getRunType() != null) {
      errors.add(new ValidationError("runType", "Run type should be omitted for non-ONT instruments"));
    }
  }

  private void validateMovieTime(SequencingParameters params, List<ValidationError> errors) {
    if(params.getInstrumentModel().getPlatformType() == PlatformType.PACBIO) {
      if(params.getMovieTime() == null) {
        errors.add(new ValidationError("movieTime", "Movie time value must be specified for "
            + "PacBio instruments"));
      }
      if(params.getMovieTime() < 0) {
        errors.add(new ValidationError("movieTime", "Movie time must be greater than 0"));
      }
    } else if (params.getMovieTime() != null) {
      errors.add(new ValidationError("movieTime", "Movie time should be omitted for non-PacBio "
          + "instruments"));
    }
  }

  @Override
  protected void applyChanges(SequencingParameters to, SequencingParameters from) throws IOException {
    to.setName(from.getName());
    to.setReadLength(from.getReadLength());
    to.setReadLength2(from.getReadLength2());
    to.setChemistry(from.getChemistry());
    to.setRunType(from.getRunType());
    to.setMovieTime(from.getMovieTime());
  }

  @Override
  protected void beforeSave(SequencingParameters object) throws IOException {
    object.setChangeDetails(authorizationManager.getCurrentUser());
  }

  @Override
  public ValidationResult validateDeletion(SequencingParameters object) throws IOException {
    ValidationResult result = new ValidationResult();
    long runUsage = sequencingParametersDao.getUsageByRuns(object);
    long poolOrderUsage = sequencingParametersDao.getUsageByPoolOrders(object);
    long seqOrderUsage = sequencingParametersDao.getUsageBySequencingOrders(object);
    if (runUsage > 0L || poolOrderUsage > 0L || seqOrderUsage > 0L) {
      String message = String.format("%s %s is used by ", object.getDeleteType(), object.getDeleteDescription());
      message += Stream.of(runUsage == 0L ? null : String.format("%d %s", runUsage, Pluralizer.runs(runUsage)),
          poolOrderUsage == 0L ? null : String.format("%d pool %s", poolOrderUsage, Pluralizer.orders(poolOrderUsage)),
          seqOrderUsage == 0L ? null : String.format("%d sequencing %s", seqOrderUsage, Pluralizer.orders(seqOrderUsage)))
          .filter(Objects::nonNull)
          .collect(Collectors.joining(", "));
      result.addError(new ValidationError(message));
    }
    return result;
  }

}
