package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.persistence.RunPartitionAliquotDao;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultRunPartitionAliquotService implements RunPartitionAliquotService {

  @Autowired
  private RunPartitionAliquotDao runPartitionAliquotDao;
  @Autowired
  private RunService runService;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private LibraryAliquotService libraryAliquotService;
  @Autowired
  private RunPurposeService runPurposeService;
  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public RunPartitionAliquot get(Run run, Partition partition, LibraryAliquot aliquot) throws IOException {
    return runPartitionAliquotDao.get(run, partition, aliquot);
  }

  @Override
  public List<RunPartitionAliquot> listByRunId(long runId) throws IOException {
    return runPartitionAliquotDao.listByRunId(runId);
  }

  @Override
  public List<RunPartitionAliquot> listByAliquotId(long aliquotId) throws IOException {
    return runPartitionAliquotDao.listByAliquotId(aliquotId);
  }

  @Override
  public void save(List<RunPartitionAliquot> runPartitionAliquots) throws IOException {
    for (RunPartitionAliquot runPartitionAliquot : runPartitionAliquots) {
      save(runPartitionAliquot);
    }
  }

  @Override
  public void save(RunPartitionAliquot runPartitionAliquot) throws IOException {
    RunPartitionAliquot managed = get(runPartitionAliquot.getRun(), runPartitionAliquot.getPartition(), runPartitionAliquot.getAliquot());
    if (runPartitionAliquot.getPurpose() != null) {
      runPartitionAliquot.setPurpose(runPurposeService.get(runPartitionAliquot.getPurpose().getId()));
    }
    User user = authorizationManager.getCurrentUser();
    updateQcUser(runPartitionAliquot, managed);

    List<ValidationError> errors = new ArrayList<>();
    ValidationUtils.validateQcUser(runPartitionAliquot.getQcPassed(), runPartitionAliquot.getQcUser(), errors);
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }

    if (managed == null) {
      runPartitionAliquot.setRun(runService.get(runPartitionAliquot.getRun().getId()));
      runPartitionAliquot.setPartition(containerService.getPartition(runPartitionAliquot.getPartition().getId()));
      runPartitionAliquot.setAliquot(libraryAliquotService.get(runPartitionAliquot.getAliquot().getId()));
      runPartitionAliquot.setLastModifier(user);
      runPartitionAliquotDao.create(runPartitionAliquot);
    } else {
      managed.setPurpose(runPartitionAliquot.getPurpose());
      managed.setQcPassed(runPartitionAliquot.getQcPassed());
      managed.setQcNote(runPartitionAliquot.getQcNote());
      managed.setQcUser(runPartitionAliquot.getQcUser());
      managed.setLastModifier(user);
      runPartitionAliquotDao.update(managed);
    }
  }

  private void updateQcUser(RunPartitionAliquot runPartitionAliquot, RunPartitionAliquot beforeChange) throws IOException {
    if (ValidationUtils.isChanged(RunPartitionAliquot::getQcPassed, runPartitionAliquot, beforeChange)) {
      if (runPartitionAliquot.getQcPassed() == null) {
        runPartitionAliquot.setQcUser(null);
      } else {
        runPartitionAliquot.setQcUser(authorizationManager.getCurrentUser());
      }
    } else if (beforeChange != null) {
      runPartitionAliquot.setQcUser(beforeChange.getQcUser());
    }
  }

  @Override
  public void deleteForRunContainer(Run run, SequencerPartitionContainer container) throws IOException {
    runPartitionAliquotDao.deleteForRunContainer(run, container);
  }

  @Override
  public void deleteForPoolAliquot(Pool pool, long aliquotId) throws IOException {
    runPartitionAliquotDao.deleteForPoolAliquot(pool, aliquotId);
  }

}
