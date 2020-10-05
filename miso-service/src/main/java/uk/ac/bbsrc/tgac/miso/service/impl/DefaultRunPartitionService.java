package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.PartitionQcTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.persistence.RunPartitionStore;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultRunPartitionService implements RunPartitionService {

  @Autowired
  private ContainerService containerService;
  @Autowired
  private RunPartitionStore runPartitionDao;
  @Autowired
  private PartitionQcTypeService partitionQcTypeService;
  @Autowired
  private RunService runService;
  @Autowired
  private RunPurposeService runPurposeService;
  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public RunPartition get(Run run, Partition partition) throws IOException {
    Run managedRun = runService.get(run.getId());
    return runPartitionDao.get(managedRun, partition);
  }

  @Override
  public void save(RunPartition runPartition) throws IOException {
    RunPartition managed = get(runPartition.getRun(), runPartition.getPartition());
    User user = authorizationManager.getCurrentUser();

    ValidationUtils.loadChildEntity(runPartition::setPurpose, runPartition.getPurpose(), runPurposeService, "runPurposeId");
    ValidationUtils.loadChildEntity(runPartition::setQcType, runPartition.getQcType(), partitionQcTypeService, "qcType");

    if (runPartition.getQcType().isNoteRequired() && runPartition.getNotes() == null) {
      throw new ValidationException("A note is required for the selected partition QC status");
    }

    if (managed == null) {
      runPartition.setRun(runService.get(runPartition.getRun().getId()));
      runPartition.setPartition(containerService.getPartition(runPartition.getPartition().getId()));
      runPartition.setLastModifier(user);
      runPartitionDao.create(runPartition);
    } else {
      managed.setQcType(runPartition.getQcType());
      managed.setNotes(runPartition.getNotes());
      managed.setPurpose(runPartition.getPurpose());
      managed.setLastModifier(user);
      runPartitionDao.update(managed);
    }
  }

  @Override
  public void deleteForRun(Run run) throws IOException {
    Run managed = runService.get(run.getId());
    authorizationManager.throwIfNonAdminOrMatchingOwner(managed.getCreator());
    runPartitionDao.deleteForRun(managed);
  }

  @Override
  public void deleteForRunContainer(Run run, SequencerPartitionContainer container) throws IOException {
    runPartitionDao.deleteForRunContainer(run, container);
  }
}
