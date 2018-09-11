package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OxfordNanoporeContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoreVersion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityProfileStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.service.ContainerModelService;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.KitService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizedPaginatedDataSource;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultContainerService
    implements ContainerService, AuthorizedPaginatedDataSource<SequencerPartitionContainer> {
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private SequencerPartitionContainerStore containerDao;
  @Autowired
  private PoolService poolService;
  @Autowired
  private SecurityProfileStore securityProfileDao;
  @Autowired
  private KitService kitService;
  @Autowired
  private ContainerModelService containerModelService;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public PaginatedDataSource<SequencerPartitionContainer> getBackingPaginationSource() {
    return containerDao;
  }

  @Override
  public List<SequencerPartitionContainer> list() throws IOException {
    Collection<SequencerPartitionContainer> containers = containerDao.listAll();
    return authorizationManager.filterUnreadable(containers);
  }

  @Override
  public Collection<SequencerPartitionContainer> listByBarcode(String barcode) throws IOException {
    Collection<SequencerPartitionContainer> containers = containerDao.listSequencerPartitionContainersByBarcode(barcode);
    for (SequencerPartitionContainer container : containers) {
      authorizationManager.throwIfNotReadable(container);
    }
    return containers;
  }

  @Override
  public Collection<SequencerPartitionContainer> listByRunId(long runId) throws IOException {
    Collection<SequencerPartitionContainer> containers = containerDao.listAllSequencerPartitionContainersByRunId(runId);
    return authorizationManager.filterUnreadable(containers);
  }

  @Override
  public Collection<Partition> listPartitionsByPoolId(long poolId) throws IOException {
    return containerDao.listAllPartitionsByPoolId(poolId);
  }

  @Override
  public SequencerPartitionContainer get(long containerId) throws IOException, AuthorizationException {
    SequencerPartitionContainer container = containerDao.get(containerId);
    authorizationManager.throwIfNotReadable(container);
    return container;
  }

  @Override
  public SequencerPartitionContainer create(SequencerPartitionContainer container) throws IOException {
    loadChildEntities(container);
    authorizationManager.throwIfNotWritable(container);
    setChangeDetails(container);

    container.setSecurityProfile(securityProfileDao.get(securityProfileDao.save(container.getSecurityProfile())));
    return containerDao.save(container);
  }

  @Override
  public SequencerPartitionContainer update(SequencerPartitionContainer container) throws IOException {
    SequencerPartitionContainer managed = get(container.getId());
    authorizationManager.throwIfNotWritable(managed);
    applyChanges(container, managed);
    setChangeDetails(managed);
    loadChildEntities(managed);
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

  /**
   * Updates all user data associated with the change
   * 
   * @param container the SequencerPartitionContainer to update
   * @throws IOException
   */
  private void setChangeDetails(SequencerPartitionContainer container) throws IOException {
    User user = authorizationManager.getCurrentUser();
    Date now = new Date();
    container.setLastModifier(user);

    if (container.getId() == Sample.UNSAVED_ID) {
      container.setCreator(user);
      if (container.getCreationTime() == null) {
        container.setCreationTime(now);
        container.setLastModified(now);
      } else if (container.getLastModified() == null) {
        container.setLastModified(now);
      }
    } else {
      container.setLastModified(now);
    }
  }

  @Override
  public Partition getPartition(long partitionId) throws IOException {
    Partition partition = containerDao.getPartitionById(partitionId);
    authorizationManager.throwIfNotReadable(partition.getSequencerPartitionContainer());
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

  public void setSecurityProfileDao(SecurityProfileStore securityProfileStore) {
    this.securityProfileDao = securityProfileStore;
  }

  @Override
  public void update(Partition partition) throws IOException {
    Partition original = containerDao.getPartitionById(partition.getId());
    authorizationManager.throwIfNotWritable(original.getSequencerPartitionContainer());
    Pool pool = partition.getPool() == null ? null : poolService.get(partition.getPool().getId());
    original.setPool(pool);
    setChangeDetails(original.getSequencerPartitionContainer());
    containerDao.save(original.getSequencerPartitionContainer());
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

    if (object.getRuns() != null && !object.getRuns().isEmpty()) {
      result.addError(new ValidationError(String.format("Container %s (%s) is used in %d run(s)", object.getId(),
          object.getIdentificationBarcode(), object.getRuns().size())));
    }

    return result;
  }
}
