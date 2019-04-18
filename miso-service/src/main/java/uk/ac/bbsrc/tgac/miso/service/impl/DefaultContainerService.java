package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OxfordNanoporeContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoreVersion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.ContainerModelService;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.KitService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultContainerService
    implements ContainerService, PaginatedDataSource<SequencerPartitionContainer> {
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private SequencerPartitionContainerStore containerDao;
  @Autowired
  private PoolService poolService;
  @Autowired
  private KitService kitService;
  @Autowired
  private ContainerModelService containerModelService;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }


  @Override
  public List<SequencerPartitionContainer> list() throws IOException {
    return containerDao.listAll();
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
  public SequencerPartitionContainer create(SequencerPartitionContainer container) throws IOException {
    loadChildEntities(container);
    validateChange(container, null);
    container.setChangeDetails(authorizationManager.getCurrentUser());

    return containerDao.save(container);
  }

  @Override
  public SequencerPartitionContainer update(SequencerPartitionContainer container) throws IOException {
    SequencerPartitionContainer managed = get(container.getId());
    loadChildEntities(container);
    validateChange(container, managed);
    applyChanges(container, managed);
    managed.setChangeDetails(authorizationManager.getCurrentUser());
    return containerDao.save(managed);
  }

  @Override
  public SequencerPartitionContainer save(SequencerPartitionContainer container) throws IOException {
    if (container.getId() == SequencerPartitionContainerImpl.UNSAVED_ID) {
      return create(container);
    } else {
      return update(container);
    }
  }

  private void validateChange(SequencerPartitionContainer container, SequencerPartitionContainer beforeChange) throws IOException {
    if (container.getModel().getPartitionCount() != container.getPartitions().size()) {
      // this is not user-correctable, so should not be reported as a validation error
      throw new IllegalArgumentException("Number of partitions does not match container model specifications");
    }

    List<ValidationError> errors = new ArrayList<>();

    if ((beforeChange == null || !container.getIdentificationBarcode().equals(beforeChange.getIdentificationBarcode()))
        && !listByBarcode(container.getIdentificationBarcode()).isEmpty()) {
      errors.add(new ValidationError("identificationBarcode", "There is already a container with this serial number"));
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
    managed.setMultiplexingKit(source.getMultiplexingKit());

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
   * Loads persisted objects into container fields. Should be called before saving new containers. Loads all member objects <b>except</b>
   * <ul>
   * <li>creator/lastModifier User objects</li>
   * </ul>
   * 
   * @param container the SequencerPartitionContainer to load entities into. Must contain at least the IDs of objects to load (e.g. to load
   *          the persisted SequencingContainerModel
   *          into container.model, container.model.id must be set)
   * @throws IOException
   */
  private void loadChildEntities(SequencerPartitionContainer container) throws IOException {
    container.setModel(containerModelService.get(container.getModel().getId()));
    if (container.getClusteringKit() != null) {
      KitDescriptor descriptor = kitService.getKitDescriptorById(container.getClusteringKit().getId());
      if (descriptor.getKitType() != KitType.CLUSTERING) {
        throw new IllegalArgumentException(descriptor.getName() + " is not a clustering kit.");
      }
      container.setClusteringKit(descriptor);
    }
    if (container.getMultiplexingKit() != null) {
      KitDescriptor descriptor = kitService.getKitDescriptorById(container.getMultiplexingKit().getId());
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
    containerDao.save(original.getSequencerPartitionContainer());
  }

  private void validateChange(Partition partition, Partition beforeChange) {
    List<ValidationError> errors = new ArrayList<>();

    ValidationUtils.validateConcentrationUnits(partition.getLoadingConcentration(), partition.getLoadingConcentrationUnits(),
        "loadingConcentration", "Loading Concentration", errors);

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(Partition target, Partition source) {
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

  @Override
  public PoreVersion getPoreVersion(long id) throws IOException {
    return containerDao.getPoreVersion(id);
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
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return containerDao.count(errorHandler, filter);
  }

  @Override
  public List<SequencerPartitionContainer> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return containerDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }
}
