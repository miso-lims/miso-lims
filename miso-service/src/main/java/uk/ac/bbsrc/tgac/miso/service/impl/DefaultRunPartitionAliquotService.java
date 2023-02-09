package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import uk.ac.bbsrc.tgac.miso.core.service.RunLibraryQcStatusService;
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
  private RunLibraryQcStatusService runLibraryQcStatusService;
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
  public List<RunPartitionAliquot> listByLibraryIdList(Collection<Long> libraryIds) throws IOException {
    return runPartitionAliquotDao.listByLibraryIdList(libraryIds);
  }

  @Override
  public void save(List<RunPartitionAliquot> runPartitionAliquots) throws IOException {
    for (RunPartitionAliquot runPartitionAliquot : runPartitionAliquots) {
      save(runPartitionAliquot);
    }
  }

  @Override
  public void save(RunPartitionAliquot runPartitionAliquot) throws IOException {
    RunPartitionAliquot managed =
        get(runPartitionAliquot.getRun(), runPartitionAliquot.getPartition(), runPartitionAliquot.getAliquot());
    loadChildEntity(runPartitionAliquot::setPurpose, runPartitionAliquot.getPurpose(), runPurposeService,
        "runPurposeId");
    loadChildEntity(runPartitionAliquot::setQcStatus, runPartitionAliquot.getQcStatus(), runLibraryQcStatusService,
        "qcStatusId");
    User user = authorizationManager.getCurrentUser();
    updateQcDetails(runPartitionAliquot, managed, RunPartitionAliquot::getQcStatus, RunPartitionAliquot::getQcUser,
        RunPartitionAliquot::setQcUser, authorizationManager, RunPartitionAliquot::getQcDate,
        RunPartitionAliquot::setQcDate);
    boolean qcStatusChanged = isChanged(RunPartitionAliquot::getQcStatus, runPartitionAliquot, managed);
    if (qcStatusChanged) {
      runPartitionAliquot.setDataReview(null);
    }
    ValidationUtils.updateQcDetails(runPartitionAliquot, managed, RunPartitionAliquot::getDataReview,
        RunPartitionAliquot::getDataReviewer,
        RunPartitionAliquot::setDataReviewer, authorizationManager, RunPartitionAliquot::getDataReviewDate,
        RunPartitionAliquot::setDataReviewDate);

    List<ValidationError> errors = new ArrayList<>();
    if (runPartitionAliquot.getDataReview() != null && runPartitionAliquot.getQcStatus() == null) {
      errors.add(new ValidationError("dataReview", "Cannot set data review before QC status"));
    }
    validateQcUser(runPartitionAliquot.getQcStatus(), runPartitionAliquot.getQcUser(), errors);
    // data review gets cleared if QC status changes; otherwise, it can only be changed by data
    // reviewers/admin
    if (isChanged(RunPartitionAliquot::getDataReview, runPartitionAliquot, managed)
        && !user.isRunReviewer() && !user.isAdmin()
        && (!qcStatusChanged || runPartitionAliquot.getDataReview() != null)) {
      errors.add(new ValidationError("dataReview", "You are not authorized to make this change"));
    }
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }

    managed.setPurpose(runPartitionAliquot.getPurpose());
    managed.setQcStatus(runPartitionAliquot.getQcStatus());
    managed.setQcNote(runPartitionAliquot.getQcNote());
    managed.setQcUser(runPartitionAliquot.getQcUser());
    managed.setQcDate(runPartitionAliquot.getQcDate());
    managed.setDataReview(runPartitionAliquot.getDataReview());
    managed.setDataReviewer(runPartitionAliquot.getDataReviewer());
    managed.setDataReviewDate(runPartitionAliquot.getDataReviewDate());
    managed.setLastModifier(user);
    runPartitionAliquotDao.save(managed);
  }

  @Override
  public void deleteForRunContainer(Run run, SequencerPartitionContainer container) throws IOException {
    runPartitionAliquotDao.deleteForRunContainer(run, container);
  }

  @Override
  public void deleteForPartition(Partition partition) throws IOException {
    runPartitionAliquotDao.deleteForPartition(partition);
  }

  @Override
  public void deleteForPoolAliquot(Pool pool, long aliquotId) throws IOException {
    runPartitionAliquotDao.deleteForPoolAliquot(pool, aliquotId);
  }

}
