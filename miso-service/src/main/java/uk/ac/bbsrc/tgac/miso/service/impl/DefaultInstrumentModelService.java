package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentDataManglingPolicy;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingContainerModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.InstrumentModelStore;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultInstrumentModelService extends AbstractSaveService<InstrumentModel> implements InstrumentModelService {

  @Autowired
  private InstrumentModelStore instrumentModelStore;
  @Autowired
  private SequencingContainerModelService containerModelService;
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

  public void setInstrumentModelStore(InstrumentModelStore instrumentModelStore) {
    this.instrumentModelStore = instrumentModelStore;
  }

  @Override
  public Set<PlatformType> listActivePlatformTypes() throws IOException {
    return instrumentModelStore.listActivePlatformTypes();
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
  public List<InstrumentModel> list() throws IOException {
    return instrumentModelStore.list();
  }

  @Override
  public SaveDao<InstrumentModel> getDao() {
    return instrumentModelStore;
  }

  @Override
  protected void authorizeSave(InstrumentModel object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void loadChildEntities(InstrumentModel object) throws IOException {
    Set<InstrumentPosition> positions = new HashSet<>();
    for (InstrumentPosition pos : object.getPositions()) {
      if (pos.isSaved()) {
        positions.add(instrumentModelStore.getPosition(pos.getId()));
      } else {
        positions.add(pos);
      }
    }
    object.getPositions().clear();
    object.getPositions().addAll(positions);

    Set<SequencingContainerModel> models = new HashSet<>();
    for (SequencingContainerModel model : object.getContainerModels()) {
      models.add(containerModelService.get(model.getId()));
    }
    object.getContainerModels().clear();
    object.getContainerModels().addAll(models);
  }

  @Override
  protected void collectValidationErrors(InstrumentModel object, InstrumentModel beforeChange, List<ValidationError> errors)
      throws IOException {
    if (object.getInstrumentType() == InstrumentType.SEQUENCER) {
      if (object.getNumContainers() < 1) {
        errors.add(new ValidationError("numContainers", "Should be 1 or greater for sequencer models"));
      } else if (beforeChange != null && object.getNumContainers() < beforeChange.getNumContainers()
          && instrumentModelStore.getMaxContainersUsed(beforeChange) > object.getNumContainers()) {
            errors.add(new ValidationError("numContainers", "There are already runs with more containers on instruments of this model"));
      }
    } else {
      if (!object.getPositions().isEmpty()) {
        errors.add(new ValidationError("Only sequencer models can have run positions"));
      }
      if (!object.getContainerModels().isEmpty()) {
        errors.add(new ValidationError("Only sequencer models can have container models"));
      }
      if (object.getNumContainers() != 0) {
        errors.add(new ValidationError("numContainers", "Should be 0 for non-sequencer models"));
      }
      if (object.getDataManglingPolicy() != InstrumentDataManglingPolicy.NONE) {
        errors.add(new ValidationError("dataManglingPolicy", "Should be 'normal' for non-sequencer models"));
      }
    }
    if (beforeChange != null) {
      Set<InstrumentPosition> removed = beforeChange.getPositions().stream()
          .filter(beforePos -> object.getPositions().stream().noneMatch(afterPos -> afterPos.getId() == beforePos.getId()))
          .collect(Collectors.toSet());
      for (InstrumentPosition pos : removed) {
        long usage = instrumentModelStore.getPositionUsage(pos);
        if (usage > 0L) {
          errors.add(new ValidationError(String.format("Position %s is used by %d existing runs", pos.getAlias(), usage)));
        }
      }
      for (SequencingContainerModel model : object.getContainerModels()) {
        if (model.getPlatformType() != object.getPlatformType()) {
          errors.add(new ValidationError(String.format("Container model %s is for a different platform", model.getAlias())));
        }
      }
      Set<SequencingContainerModel> removedModels = beforeChange.getContainerModels().stream()
          .filter(beforeModel -> object.getContainerModels().stream()
              .noneMatch(afterModel -> afterModel.getId() == beforeModel.getId()))
          .collect(Collectors.toSet());
      for (SequencingContainerModel removedModel : removedModels) {
        long usage = containerModelService.getUsage(removedModel, beforeChange);
        if (usage > 0L) {
          errors.add(new ValidationError(String.format("Cannot remove container model %s because there are already %d existing "
              + Pluralizer.containers(usage) + " of this model attached to runs for this instrument type", removedModel.getAlias(),
              usage)));
        }
      }
    }
  }

  @Override
  protected void applyChanges(InstrumentModel to, InstrumentModel from) throws IOException {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setNumContainers(from.getNumContainers());
    to.setDataManglingPolicy(from.getDataManglingPolicy());

    applyContainerModelChanges(to, from);
  }

  private void applyContainerModelChanges(InstrumentModel to, InstrumentModel from) {
    to.getContainerModels().removeIf(toModel -> from.getContainerModels().stream()
        .noneMatch(fromModel -> fromModel.getId() == toModel.getId()));
    for (SequencingContainerModel fromModel : from.getContainerModels()) {
      if (to.getContainerModels().stream().noneMatch(toModel -> toModel.getId() == fromModel.getId())) {
        to.getContainerModels().add(fromModel);
      }
    }
  }

  @Override
  public long create(InstrumentModel object) throws IOException {
    long savedId = super.create(object);
    applyPositionChanges(savedId, object);
    return savedId;
  }

  @Override
  public long update(InstrumentModel object) throws IOException {
    long savedId = super.update(object);
    applyPositionChanges(savedId, object);
    return savedId;
  }

  private void applyPositionChanges(long savedId, InstrumentModel from) throws IOException {
    InstrumentModel to = instrumentModelStore.get(savedId);
    Set<InstrumentPosition> toDelete = to.getPositions().stream()
        .filter(toPos -> from.getPositions().stream().noneMatch(fromPos -> fromPos.getId() == toPos.getId()))
        .collect(Collectors.toSet());
    for (InstrumentPosition pos : toDelete) {
      instrumentModelStore.deletePosition(pos);
    }

    for (InstrumentPosition fromPos : from.getPositions()) {
      if (!fromPos.isSaved()) {
        fromPos.setInstrumentModel(to);
        instrumentModelStore.createPosition(fromPos);
      }
    }
  }

  @Override
  public ValidationResult validateDeletion(InstrumentModel object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = instrumentModelStore.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.instruments(usage)));
    }
    return result;
  }

  @Override
  public void beforeDelete(InstrumentModel object) throws IOException {
    List<SequencingParameters> params = sequencingParametersService.listByInstrumentModelId(object.getId());
    if (!params.isEmpty()) {
      sequencingParametersService.bulkDelete(params);
    }
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return instrumentModelStore.count(errorHandler, filter);
  }

  @Override
  public List<InstrumentModel> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return instrumentModelStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

}
