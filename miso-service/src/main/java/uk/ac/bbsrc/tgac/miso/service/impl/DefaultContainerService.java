package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OxfordNanoporeContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoreVersion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BarcodableReferenceService;
import uk.ac.bbsrc.tgac.miso.core.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingContainerModelService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.SequencerPartitionContainerStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultContainerService implements ContainerService {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private SequencerPartitionContainerStore containerDao;
  @Autowired
  private PoolService poolService;
  @Autowired
  private KitDescriptorService kitService;
  @Autowired
  private SequencingContainerModelService containerModelService;
  @Autowired
  private BarcodableReferenceService barcodableReferenceService;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private RunService runService;
  @Autowired
  private RunPartitionAliquotService runPartitionAliquotService;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public List<SequencerPartitionContainer> listByBarcode(String barcode) throws IOException {
    return containerDao.listSequencerPartitionContainersByBarcode(barcode);
  }

  @Override
  public Collection<SequencerPartitionContainer> listByRunId(long runId) throws IOException {
    return containerDao.listAllSequencerPartitionContainersByRunId(runId);
  }

  @Override
  public Collection<Partition> listPartitionsByPoolId(long poolId) throws IOException {
    return containerDao.listAllPartitionsByPoolId(poolId);
  }

  @Override
  public SequencerPartitionContainer get(long containerId) throws IOException, AuthorizationException {
    return containerDao.get(containerId);
  }

  @Override
  public long create(SequencerPartitionContainer container) throws IOException {
    loadChildEntities(container);
    validateChange(container, null);
    container.setChangeDetails(authorizationManager.getCurrentUser());

    return containerDao.create(container);
  }

  @Override
  public long update(SequencerPartitionContainer container) throws IOException {
    SequencerPartitionContainer managed = get(container.getId());
    loadChildEntities(container);
    validateChange(container, managed);
    applyChanges(container, managed);
    managed.setChangeDetails(authorizationManager.getCurrentUser());
    return containerDao.update(managed);
  }

  @Override
  public SequencerPartitionContainer save(SequencerPartitionContainer container) throws IOException {
    if (container.getId() == SequencerPartitionContainerImpl.UNSAVED_ID) {
      return get(create(container));
    } else {
      return get(update(container));
    }
  }

  private void validateChange(SequencerPartitionContainer container, SequencerPartitionContainer beforeChange)
      throws IOException {
    if (container.getModel().getPartitionCount() != container.getPartitions().size()) {
      // this is not user-correctable, so should not be reported as a validation error
      throw new IllegalArgumentException("Number of partitions does not match container model specifications");
    }

    List<ValidationError> errors = new ArrayList<>();

    if (LimsUtils.isStringBlankOrNull(container.getIdentificationBarcode())) {
      errors.add(new ValidationError("identificationBarcode", "Required"));
    }

    ValidationUtils.validateBarcodeUniqueness(container, beforeChange, barcodableReferenceService, errors);

    if (container.getClusteringKit() != null && container.getClusteringKit().getKitType() != KitType.CLUSTERING) {
      errors.add(new ValidationError("clusteringKitId", "Must be a clustering kit"));
    }
    if (container.getClusteringKitLot() != null && container.getClusteringKit() == null) {
      errors.add(new ValidationError("clusteringKitLot", "Clustering kit not specified"));
    }

    if (container.getMultiplexingKit() != null && container.getMultiplexingKit().getKitType() != KitType.MULTIPLEXING) {
      errors.add(new ValidationError("MultiplexingKitId", "Must be a multiplexing kit"));
    }
    if (container.getMultiplexingKitLot() != null && container.getMultiplexingKit() == null) {
      errors.add(new ValidationError("MultiplexingKitLot", "Multiplexing kit not specified"));
    }

    if (beforeChange != null
        && ValidationUtils.isSetAndChanged(SequencerPartitionContainer::getModel, container, beforeChange)) {
      SequencingContainerModel before = beforeChange.getModel();
      SequencingContainerModel after = container.getModel();
      if (after.getPlatformType() != before.getPlatformType()) {
        errors.add(new ValidationError("model.id",
            String.format("Can only be changed to a model of the same platform (%s)",
                before.getPlatformType().getKey())));
      } else if (after.getPartitionCount() != before.getPartitionCount()) {
        errors.add(new ValidationError("model.id",
            String.format("Can only be changed to a model with the same number of partitions (%d)",
                before.getPartitionCount())));
      }
      if (beforeChange.getRunPositions() != null) {
        Set<InstrumentModel> requiredInstrumentModels = beforeChange.getRunPositions().stream()
            .map(RunPosition::getRun)
            .map(Run::getSequencer)
            .map(Instrument::getInstrumentModel)
            .collect(Collectors.toSet());
        if (requiredInstrumentModels.stream().anyMatch(required -> after.getInstrumentModels().stream()
            .map(InstrumentModel::getId)
            .noneMatch(id -> id == required.getId()))) {
          errors.add(new ValidationError("model.id",
              String.format("Can only change to a model compatible with the linked runs' instrument models (%s)",
                  LimsUtils.joinWithConjunction(
                      requiredInstrumentModels.stream().map(InstrumentModel::getAlias).collect(Collectors.toSet()),
                      "and"))));
        }
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  @Override
  public void applyChanges(SequencerPartitionContainer source, SequencerPartitionContainer managed) throws IOException {
    managed.setIdentificationBarcode(source.getIdentificationBarcode());
    managed.setDescription(source.getDescription());
    managed.setClusteringKit(source.getClusteringKit());
    managed.setClusteringKitLot(source.getClusteringKitLot());
    managed.setMultiplexingKit(source.getMultiplexingKit());
    managed.setMultiplexingKitLot(source.getMultiplexingKitLot());
    managed.setModel(source.getModel());

    if (LimsUtils.isOxfordNanoporeContainer(managed)) {
      applyOxfordNanoporeChanges((OxfordNanoporeContainer) source, (OxfordNanoporeContainer) managed);
    }

    for (Partition sourcePartition : source.getPartitions()) {
      for (Partition managedPartition : managed.getPartitions()) {
        if (sourcePartition == null || managedPartition == null) {
          throw new IOException("Partition from " + (sourcePartition == null ? "client" : "database") + " is null.");
        }
        if (sourcePartition.getId() == managedPartition.getId()) {
          Pool sourcePool = sourcePartition.getPool();
          Pool managedPool = managedPartition.getPool();
          if (sourcePool == null) {
            managedPartition.setPool(null);
          } else if (managedPool == null || sourcePool.getId() != managedPool.getId()) {
            managedPartition.setPool(poolService.get(sourcePool.getId()));
          }
          break;
        }
      }
    }
  }

  public void applyOxfordNanoporeChanges(OxfordNanoporeContainer source, OxfordNanoporeContainer managed) {
    managed.setPoreVersion(source.getPoreVersion());
    managed.setReceivedDate(source.getReceivedDate());
    managed.setReturnedDate(source.getReturnedDate());
  }

  /**
   * Loads persisted objects into container fields. Should be called before saving new containers.
   * Loads all member objects <b>except</b>
   * <ul>
   * <li>creator/lastModifier User objects</li>
   * </ul>
   * 
   * @param container the SequencerPartitionContainer to load entities into. Must contain at least the
   *        IDs of objects to load (e.g. to load the persisted SequencingContainerModel into
   *        container.model, container.model.id must be set)
   * @throws IOException
   */
  private void loadChildEntities(SequencerPartitionContainer container) throws IOException {
    container.setModel(containerModelService.get(container.getModel().getId()));
    if (container.getClusteringKit() != null) {
      KitDescriptor descriptor = kitService.get(container.getClusteringKit().getId());
      if (descriptor.getKitType() != KitType.CLUSTERING) {
        throw new IllegalArgumentException(descriptor.getName() + " is not a clustering kit.");
      }
      container.setClusteringKit(descriptor);
    }
    if (container.getMultiplexingKit() != null) {
      KitDescriptor descriptor = kitService.get(container.getMultiplexingKit().getId());
      if (descriptor.getKitType() != KitType.MULTIPLEXING) {
        throw new IllegalArgumentException(descriptor.getName() + " is not a multiplexing kit.");
      }
      container.setMultiplexingKit(descriptor);
    }
    if (LimsUtils.isOxfordNanoporeContainer(container)) {
      OxfordNanoporeContainer ontContainer = (OxfordNanoporeContainer) container;
      if (ontContainer.getPoreVersion() != null) {
        ontContainer.setPoreVersion(containerDao.getPoreVersion(ontContainer.getPoreVersion().getId()));
      }
    }
  }

  @Override
  public Partition getPartition(long partitionId) throws IOException {
    Partition partition = containerDao.getPartitionById(partitionId);
    return partition;
  }

  public void setPoolService(PoolService poolService) {
    this.poolService = poolService;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setContainerDao(SequencerPartitionContainerStore containerStore) {
    this.containerDao = containerStore;
  }

  @Override
  public void update(Partition partition) throws IOException {
    Partition original = containerDao.getPartitionById(partition.getId());
    if (partition.getPool() != null) {
      partition.setPool(poolService.get(partition.getPool().getId()));
    }
    validateChange(partition, original);
    applyChanges(original, partition);
    original.getSequencerPartitionContainer().setChangeDetails(authorizationManager.getCurrentUser());
    containerDao.update(original.getSequencerPartitionContainer());
  }

  private void validateChange(Partition partition, Partition beforeChange) {
    List<ValidationError> errors = new ArrayList<>();

    ValidationUtils.validateConcentrationUnits(partition.getLoadingConcentration(),
        partition.getLoadingConcentrationUnits(),
        "loadingConcentration", "Loading Concentration", errors);

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(Partition target, Partition source) throws IOException {
    Pool targetPool = target.getPool();
    Pool sourcePool = source.getPool();
    if ((targetPool == null) != (sourcePool == null)
        || (targetPool != null && sourcePool != null && targetPool.getId() != sourcePool.getId())) {
      logPoolChanged(targetPool, sourcePool, target);
      if (target.getPool() != null) {
        runPartitionAliquotService.deleteForPartition(target);
      }
    }
    target.setPool(source.getPool());
    target.setLoadingConcentration(source.getLoadingConcentration());
    target.setLoadingConcentrationUnits(source.getLoadingConcentrationUnits());

    if (target.getPool() == null) {
      target.setLoadingConcentration(null);
    }
    if (target.getLoadingConcentration() == null) {
      target.setLoadingConcentrationUnits(null);
    }
  }

  private void logPoolChanged(Pool oldPool, Pool newPool, Partition partition) throws IOException {
    String containerBarcode = partition.getSequencerPartitionContainer().getIdentificationBarcode();
    Integer partitionNumber = partition.getPartitionNumber();
    User user = authorizationManager.getCurrentUser();
    String oldAlias = oldPool == null ? "n/a" : oldPool.getAlias();
    String newAlias = newPool == null ? "n/a" : newPool.getAlias();
    if (oldPool != null) {
      changeLogService.create(oldPool.createChangeLog(String.format("Removed from container %s (partition %d)",
          containerBarcode, partitionNumber), "container", user));
    }
    if (newPool != null) {
      changeLogService.create(newPool.createChangeLog(String.format("Added to container %s (partition %d)",
          containerBarcode, partitionNumber), "container", user));
    }
    changeLogService.create(partition.getSequencerPartitionContainer().createChangeLog(
        String.format("Pool changed in partition %d: %s → %s", partitionNumber, oldAlias, newAlias), "pool", user));
    Collection<Run> runs = runService.listByContainerId(partition.getSequencerPartitionContainer().getId());
    for (Run run : runs) {
      changeLogService.create(run.createChangeLog(
          String.format("Pool changed in partition %d of container %s: %s → %s", partitionNumber, containerBarcode,
              oldAlias, newAlias),
          "pool", user));
    }
  }

  @Override
  public List<PoreVersion> listPoreVersions() throws IOException {
    return containerDao.listPoreVersions();
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public void authorizeDeletion(SequencerPartitionContainer object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  @Override
  public ValidationResult validateDeletion(SequencerPartitionContainer object) {
    ValidationResult result = new ValidationResult();

    if (object.getRunPositions() != null && !object.getRunPositions().isEmpty()) {
      result.addError(new ValidationError(String.format("Container %s (%s) is used in %d run(s)", object.getId(),
          object.getIdentificationBarcode(), object.getRunPositions().size())));
    }

    return result;
  }

  @Override
  public Long getPartitionIdByRunIdAndPartitionNumber(long runId, int partitionNumber) throws IOException {
    return containerDao.getPartitionIdByRunIdAndPartitionNumber(runId, partitionNumber);
  }

}
