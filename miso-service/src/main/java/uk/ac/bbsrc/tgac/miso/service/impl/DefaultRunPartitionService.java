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
    return get(run.getId(), partition.getId());
  }

  private RunPartition get(long runId, long partitionId) throws IOException {
    return runPartitionDao.get(runId, partitionId);
  }

  @Override
  public void save(RunPartition runPartition) throws IOException {
    RunPartition managed = get(runPartition.getRunId(), runPartition.getPartitionId());
    User user = authorizationManager.getCurrentUser();

    ValidationUtils.loadChildEntity(runPartition::setPurpose, runPartition.getPurpose(), runPurposeService, "runPurposeId");
    ValidationUtils.loadChildEntity(runPartition::setQcType, runPartition.getQcType(), partitionQcTypeService, "qcType");

    if (runPartition.getQcType() != null && runPartition.getQcType().isNoteRequired() && runPartition.getNotes() == null) {
      throw new ValidationException("A note is required for the selected partition QC status");
    }

    if (managed == null) {
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
