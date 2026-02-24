package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunItemQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcStatusUpdate;
import uk.ac.bbsrc.tgac.miso.core.data.qc.DetailedQcItem;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.PartitionQcTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.QcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.RunItemQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultQcStatusService implements QcStatusService {

  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryAliquotService libraryAliquotService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private RunPartitionService runPartitionService;
  @Autowired
  private RunPartitionAliquotService runPartitionAliquotService;
  @Autowired
  private DetailedQcStatusService detailedQcStatusService;
  @Autowired
  private PartitionQcTypeService partitionQcTypeService;
  @Autowired
  private RunItemQcStatusService runItemQcStatusService;
  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public void update(QcStatusUpdate update) throws IOException {
    // Since we're editing managed objects in session here, we can't depend on the saving service
    // to detect changes and set QC users
    switch (update.getEntityType()) {
      case SAMPLE: {
        Sample sample = sampleService.get(update.getId());
        updateDetailedStatus(EntityType.SAMPLE.getLabel(), sample, update);
        sampleService.update(sample);
        break;
      }
      case LIBRARY: {
        Library library = libraryService.get(update.getId());
        updateDetailedStatus(EntityType.LIBRARY.getLabel(), library, update);
        libraryService.update(library);
        break;
      }
      case LIBRARY_ALIQUOT: {
        LibraryAliquot aliquot = libraryAliquotService.get(update.getId());
        updateDetailedStatus(EntityType.LIBRARY_ALIQUOT.getLabel(), aliquot, update);
        libraryAliquotService.update(aliquot);
        break;
      }
      case POOL: {
        Pool pool = poolService.get(update.getId());
        throwIfNull(EntityType.POOL.getLabel(), pool);
        pool.setQcPassed(update.getQcPassed());
        poolService.update(pool);
        break;
      }
      case RUN: {
        Run run = runService.get(update.getId());
        throwIfNull("Run", run);
        if (update.getQcPassed() == null) {
          // QC Passed not set
          run.setQcPassed(null);
          run.setQcUser(null);
          run.setQcDate(null);
          clearDataReview(run);
        } else if (!update.getQcPassed().equals(run.getQcPassed())) {
          // QC Passed set and changed
          run.setQcPassed(update.getQcPassed());
          run.setQcUser(authorizationManager.getCurrentUser());
          run.setQcDate(LocalDate.now(ZoneId.systemDefault()));
          clearDataReview(run);
        } else if (update.getDataReview() == null) {
          // QC Passed set and not changed, data review not set
          clearDataReview(run);
        } else {
          // QC Passed set and not changed, data review set and changed
          run.setDataReview(update.getDataReview());
          run.setDataReviewer(authorizationManager.getCurrentUser());
          run.setDataReviewDate(LocalDate.now(ZoneId.systemDefault()));
        }
        runService.update(run);
        break;
      }
      case RUN_PARTITION: {
        if (update.getIds() == null || update.getIds().length != 2) {
          throw new ValidationException("Invalid ID for run-partition");
        }
        Run run = runService.get(update.getIds()[0]);
        throwIfNull("Run", run);
        Partition partition = findPartitionInRun(run, update.getIds()[1]);
        RunPartition runPartition = runPartitionService.get(run, partition);

        if (update.getQcStatusId() == null) {
          runPartition.setQcType(null);
        } else {
          PartitionQCType status = partitionQcTypeService.get(update.getQcStatusId());
          throwIfNull("Partition QC type", status);
          runPartition.setQcType(status);
        }
        runPartition.setNotes(update.getQcNote());
        runPartition.setLastModifier(authorizationManager.getCurrentUser());
        runPartitionService.save(runPartition);
        break;
      }
      case RUN_LIBRARY: {
        if (update.getIds() == null || update.getIds().length != 3) {
          throw new ValidationException("Invalid ID for run-partition");
        }
        Run run = runService.get(update.getIds()[0]);
        throwIfNull("Run", run);
        Partition partition = findPartitionInRun(run, update.getIds()[1]);
        ListLibraryAliquotView aliquotView = partition.getPool().getPoolContents().stream()
            .map(PoolElement::getAliquot)
            .filter(x -> x.getId() == update.getIds()[2])
            .findFirst().orElse(null);
        throwIfNull("Run-library", aliquotView);
        RunPartitionAliquot runLib = runPartitionAliquotService.get(run, partition, aliquotView);
        if (update.getQcStatusId() == null) {
          // QC status not set
          runLib.setQcStatus(null);
          runLib.setQcUser(null);
          runLib.setQcDate(null);
          clearDataReview(runLib);
        } else if (runLib.getQcStatus() == null || runLib.getQcStatus().getId() != update.getQcStatusId().longValue()) {
          // QC status set and changed
          RunItemQcStatus status = runItemQcStatusService.get(update.getQcStatusId());
          throwIfNull("Run-Item QC status", status);
          runLib.setQcStatus(status);
          runLib.setQcUser(authorizationManager.getCurrentUser());
          runLib.setQcDate(LocalDate.now(ZoneId.systemDefault()));
          clearDataReview(runLib);
        } else if (update.getDataReview() == null) {
          // QC status set and not changed, data review not set
          clearDataReview(runLib);
        } else if (update.getDataReview() != runLib.getDataReview()) {
          // QC status set and not changed, data review set and changed
          runLib.setDataReview(update.getDataReview());
          runLib.setDataReviewer(authorizationManager.getCurrentUser());
          runLib.setDataReviewDate(LocalDate.now(ZoneId.systemDefault()));
        }
        runLib.setQcNote(update.getQcNote());
        runPartitionAliquotService.save(runLib);
        break;
      }
      default:
        throw new ValidationException("Unsupported entity type");
    }
  }

  private static void clearDataReview(Run run) {
    run.setDataReview(null);
    run.setDataReviewer(null);
    run.setDataReviewDate(null);
  }

  private static void clearDataReview(RunPartitionAliquot runLibrary) {
    runLibrary.setDataReview(null);
    runLibrary.setDataReviewer(null);
    runLibrary.setDataReviewDate(null);
  }

  private void updateDetailedStatus(String typeLabel, DetailedQcItem item, QcStatusUpdate update) throws IOException {
    throwIfNull(typeLabel, item);
    if (update.getQcStatusId() == null) {
      item.setDetailedQcStatus(null);
      item.setQcUser(null);
      item.setQcDate(null);
    } else if (item.getDetailedQcStatus() == null
        || item.getDetailedQcStatus().getId() != update.getQcStatusId().longValue()) {
      DetailedQcStatus status = detailedQcStatusService.get(update.getQcStatusId());
      throwIfNull("QC status", status);
      item.setDetailedQcStatus(status);
      item.setQcUser(authorizationManager.getCurrentUser());
      item.setQcDate(LocalDate.now(ZoneId.systemDefault()));
    }
    item.setDetailedQcStatusNote(update.getQcNote());
  }

  private void throwIfNull(String typeLabel, Object item) {
    if (item == null) {
      throw new ValidationException(typeLabel + " not found");
    }
  }

  private Partition findPartitionInRun(Run run, long partitionId) {
    Partition partition = run.getRunPositions().stream()
        .map(RunPosition::getContainer)
        .flatMap(x -> x.getPartitions().stream())
        .filter(x -> x.getId() == partitionId)
        .findFirst().orElse(null);
    throwIfNull("Partition", partition);
    return partition;
  }

  @Override
  public void update(Collection<QcStatusUpdate> updates) throws IOException {
    for (QcStatusUpdate update : updates) {
      update(update);
    }
  }

}
