package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@SuppressWarnings("squid:S1604")
public abstract interface PaginationFilter {

  public static PaginationFilter archived(boolean isArchived) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByArchived(item, isArchived, errorHandler);
      }
    };
  }

  public static PaginationFilter arrayed(boolean isArrayed) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByArrayed(item, isArrayed, errorHandler);
      }
    };
  }

  public static PaginationFilter barcode(String barcode) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByBarcode(item, barcode, errorHandler);
      }
    };
  }

  public static PaginationFilter batchId(String batchId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByBatchId(item, batchId, errorHandler);
      }
    };
  }

  public static PaginationFilter box(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByBox(item, name, errorHandler);
      }
    };
  }

  public static PaginationFilter boxType(BoxType boxType) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByBoxType(item, boxType, errorHandler);
      }
    };
  }

  public static PaginationFilter boxUse(long id) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByBoxUse(item, id, errorHandler);
      }
    };
  }

  public static PaginationFilter bulkLookup(Collection<String> identifiers) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByIdentifiers(item, identifiers, errorHandler);
      }

    };
  }

  public static PaginationFilter date(Date start, Date end, DateType type) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByDate(item, start, end, type, errorHandler);
      }
    };
  }

  public static PaginationFilter distributedTo(String recipient) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByDistributionRecipient(item, recipient, errorHandler);
      }
    };
  }

  public static PaginationFilter external(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByExternalName(item, name, errorHandler);
      }
    };
  }

  public static PaginationFilter fulfilled(final boolean isFulfilled) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByFulfilled(item, isFulfilled, errorHandler);
      }
    };
  }

  public static PaginationFilter draft(final boolean isDraft) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByDraft(item, isDraft, errorHandler);
      }
    };
  }

  public static PaginationFilter ghost(boolean isGhost) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByGhost(item, isGhost, errorHandler);
      }
    };
  }

  public static PaginationFilter groupId(String groupId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByGroupId(item, groupId, errorHandler);
      }
    };
  }

  public static PaginationFilter health(EnumSet<HealthType> healths) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByHealth(item, healths, errorHandler);
      }
    };
  }

  public static PaginationFilter health(HealthType health) {
    return health(EnumSet.of(health));
  }

  public static PaginationFilter id(long id) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationById(item, id, errorHandler);
      }
    };
  }

  public static PaginationFilter ids(List<Long> ids) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByIds(item, ids, errorHandler);
      }
    };
  }

  public static PaginationFilter identityIds(List<Long> ids) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByIdentityIds(item, ids, errorHandler);
      }
    };
  }

  public static PaginationFilter index(String index) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByIndex(item, index, errorHandler);
      }
    };
  }

  public static PaginationFilter lab(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByLab(item, name, errorHandler);
      }
    };
  }

  public static PaginationFilter instrumentType(InstrumentType type) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByInstrumentType(item, type, errorHandler);
      }
    };
  }

  public static PaginationFilter kitType(KitType type) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByKitType(item, type, errorHandler);
      }
    };
  }

  public static PaginationFilter kitName(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByKitName(item, name, errorHandler);
      }
    };
  }

  public static PaginationFilter sequencingParameters(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationBySequencingParametersName(item, name, errorHandler);
      }
    };
  }

  public static PaginationFilter pending() {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByPending(item, errorHandler);
      }
    };
  }

  public static PaginationFilter pipeline(String pipeline) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByPipeline(item, pipeline, errorHandler);
      }
    };
  }

  public static PaginationFilter platformType(final PlatformType platformType) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByPlatformType(item, platformType, errorHandler);
      }
    };
  }

  public static PaginationFilter pool(final long poolId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByPoolId(item, poolId, errorHandler);
      }
    };
  }

  public static PaginationFilter workset(final long worksetId) {
    return new PaginationFilter() {
      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByWorksetId(item, worksetId, errorHandler);
      }
    };
  }

  public static PaginationFilter project(final long projectId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByProjectId(item, projectId, errorHandler);
      }
    };
  }

  public static PaginationFilter project(String project) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByProject(item, project, errorHandler);
      }
    };
  }

  public static PaginationFilter query(final String query) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByQuery(item, query, errorHandler);
      }
    };
  }

  public static PaginationFilter rebNumber(String rebNumber) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByRebNumber(item, rebNumber, errorHandler);
      }
    };
  }

  public static PaginationFilter sampleClass(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByClass(item, name, errorHandler);
      }
    };
  }

  public static PaginationFilter sequencer(long id) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationBySequencerId(item, id, errorHandler);
      }
    };
  }

  public static PaginationFilter sequencingParameters(long parametersId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationBySequencingParametersId(item, parametersId, errorHandler);
      }
    };
  }

  public static PaginationFilter status(String status) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByStatus(item, status, errorHandler);
      }
    };
  }

  public static PaginationFilter subproject(String subproject) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationBySubproject(item, subproject, errorHandler);
      }
    };
  }

  public static PaginationFilter user(String loginName, boolean creator) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByUser(item, loginName, creator, errorHandler);
      }
    };
  }

  public static PaginationFilter userOrGroup(String name, boolean creator) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByUserOrGroup(item, name, creator, errorHandler);
      }
    };
  }

  public static PaginationFilter freezer(String freezer) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByFreezer(item, freezer, errorHandler);
      }
    };
  }

  public static PaginationFilter requisitionId(final long requisitionId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByRequisitionId(item, requisitionId, errorHandler);
      }
    };
  }

  public static PaginationFilter requisition(String requisition) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByRequisition(item, requisition, errorHandler);
      }
    };
  }

  public static PaginationFilter supplementalToRequisitionId(final long requisitionId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationBySupplementalToRequisitionId(item, requisitionId, errorHandler);
      }
    };
  }

  public static PaginationFilter recipientGroups(Collection<Group> groups) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByRecipientGroups(item, groups, errorHandler);
      }
    };
  }

  public static PaginationFilter transferType(TransferType transferType) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByTransferType(item, transferType, errorHandler);
      }
    };
  }

  public static PaginationFilter timepoint(String timepoint) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByTimepoint(item, timepoint, errorHandler);
      }
    };
  }

  public static PaginationFilter tissueOrigin(String origin) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByTissueOrigin(item, origin, errorHandler);
      }
    };
  }

  public static PaginationFilter tissueType(String type) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByTissueType(item, type, errorHandler);
      }
    };
  }

  public static PaginationFilter category(SopCategory category) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByCategory(item, category, errorHandler);
      }
    };
  }

  public static PaginationFilter category(String category) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByCategory(item, category, errorHandler);
      }
    };
  }

  public static PaginationFilter stage(String stage) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByStage(item, stage, errorHandler);
      }
    };
  }

  public static PaginationFilter model(String model) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByModel(item, model, errorHandler);
      }
    };
  }

  public static PaginationFilter workstation(String workstation) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByWorkstation(item, workstation, errorHandler);
      }
    };
  }

  public static PaginationFilter workstationId(long id) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByWorkstationId(item, id, errorHandler);
      }
    };
  }

  public abstract <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler);

}
