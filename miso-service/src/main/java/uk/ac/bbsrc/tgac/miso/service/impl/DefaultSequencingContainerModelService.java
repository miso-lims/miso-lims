package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BarcodableReferenceService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingContainerModelService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingContainerModelStore;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSequencingContainerModelService extends AbstractSaveService<SequencingContainerModel>
    implements SequencingContainerModelService {
  
  @Autowired
  private SequencingContainerModelStore containerModelDao;
  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private BarcodableReferenceService barcodableReferenceService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private DeletionStore deletionStore;

  @Override
  public SequencingContainerModel find(InstrumentModel platform, String search, int partitionCount) throws IOException {
    return containerModelDao.find(platform, search, partitionCount);
  }

  @Override
  public SaveDao<SequencingContainerModel> getDao() {
    return containerModelDao;
  }

  @Override
  protected void authorizeUpdate(SequencingContainerModel model) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void loadChildEntities(SequencingContainerModel model) throws IOException {
    model.setInstrumentModels(model.getInstrumentModels().stream()
        .map(WhineyFunction.rethrow(instrumentModel -> instrumentModelService.get(instrumentModel.getId())))
        .collect(Collectors.toList()));
  }

  @Override
  protected void collectValidationErrors(SequencingContainerModel model, SequencingContainerModel beforeChange,
      List<ValidationError> errors) throws IOException {
    validateBarcodeUniqueness(model, beforeChange, barcodableReferenceService, errors);
    long usage = beforeChange == null ? 0 : containerModelDao.getUsage(beforeChange);
    if (isSetAndChanged(SequencingContainerModel::getPlatformType, model, beforeChange)) {
      if (usage > 0) {
        errors.add(new ValidationError("platform",
            String.format("Cannot change because the container model is used by %d existing sequencing containers.", usage)));
      }
      if (!model.getInstrumentModels().isEmpty()) {
        errors.add(new ValidationError("platform", String.format(
            "Cannot change because the container model is linked to %d instrument models", model.getInstrumentModels().size())));
      }
    }
    if (isSetAndChanged(SequencingContainerModel::getPartitionCount, model, beforeChange)
        && usage > 0) {
      errors.add(new ValidationError("partitionCount",
          String.format("Cannot change because the container model is used by %d existing sequencing containers.", usage)));
    }
  }

  @Override
  protected void applyChanges(SequencingContainerModel to, SequencingContainerModel from) throws IOException {
    to.setAlias(from.getAlias());
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    to.setFallback(from.isFallback());
    to.setArchived(from.isArchived());
    to.setPartitionCount(from.getPartitionCount());
    to.setPlatformType(from.getPlatformType());
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
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<SequencingContainerModel> list() throws IOException {
    return containerModelDao.list();
  }

  @Override
  public List<SequencingContainerModel> listByIdList(List<Long> ids) throws IOException {
    return containerModelDao.listByIdList(ids);
  }

  @Override
  public ValidationResult validateDeletion(SequencingContainerModel object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = containerModelDao.getUsage(object);
    if (usage > 0) {
      result.addError(ValidationError.forDeletionUsage(object, usage, "sequencing " + Pluralizer.containers(usage)));
    }
    return result;
  }

  @Override
  public void beforeDelete(SequencingContainerModel object) throws IOException {
    for (InstrumentModel instrumentModel : object.getInstrumentModels()) {
      instrumentModel.getContainerModels().remove(object);
      instrumentModelService.update(instrumentModel);
    }
    object.getInstrumentModels().clear();
  }

  @Override
  public List<SequencingContainerModel> find(PlatformType platform, String search) throws IOException {
    return containerModelDao.find(platform, search);
  }

  @Override
  public long getUsage(SequencingContainerModel containerModel, InstrumentModel instrumentModel) throws IOException {
    return containerModelDao.getUsage(containerModel, instrumentModel);
  }

}
