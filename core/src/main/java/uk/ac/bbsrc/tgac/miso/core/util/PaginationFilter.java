package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
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
        sink.restrictPaginationByBox(item, new TextQuery(name), errorHandler);
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

  public static <M extends Identifiable, D, X extends RuntimeException> List<D> bulkSearch(Collection<String> names,
      PaginatedDataSource<M> service,
      Function<M, D> dto, Function<String, X> makeException) {
    List<String> namesNotFound = new LinkedList<>();
    List<D> dtos = names.stream()//
        .filter(name -> !LimsUtils.isStringBlankOrNull(name))//
        .flatMap(WhineyFunction.flatRethrow(name -> {
          Collection<M> matches = service.list(0, 0, true, "id", query(name));
          if (matches.isEmpty()) {
            namesNotFound.add(name);
          }
          return matches;
        }))//
        .collect(Collectors.groupingBy(Identifiable::getId))//
        .values()//
        .stream()//
        .map(list -> list.get(0))//
        .map(dto).collect(Collectors.toList());
    if(!namesNotFound.isEmpty()){
      StringBuilder exceptionMessage = new StringBuilder().append("Cannot find: \n");
      for(String name : namesNotFound){
        exceptionMessage.append(name).append("\n");
      }
      throw makeException.apply(exceptionMessage.toString());
    }
    return dtos;
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
        sink.restrictPaginationByDistributionRecipient(item, new TextQuery(recipient), errorHandler);
      }
    };
  }

  public static PaginationFilter external(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByExternalName(item, new TextQuery(name), errorHandler);
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
        sink.restrictPaginationByGroupId(item, new TextQuery(groupId), errorHandler);
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

  public static PaginationFilter index(String index) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByIndex(item, new TextQuery(index), errorHandler);
      }
    };
  }

  public static PaginationFilter lab(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByLab(item, new TextQuery(name), errorHandler);
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
        sink.restrictPaginationByKitName(item, new TextQuery(name), errorHandler);
      }
    };
  }

  public static PaginationFilter sequencingParameters(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationBySequencingParametersName(item, new TextQuery(name), errorHandler);
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

  public static PaginationFilter pool(final Pool pool) {
    if (!pool.isSaved()) {
      throw new IllegalArgumentException("Cannot filter by unsaved pool.");
    }
    return pool(pool.getId());
  }

  public static PaginationFilter project(final long projectId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByProjectId(item, projectId, errorHandler);
      }
    };
  }

  public static PaginationFilter query(final String query) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByQuery(item, new TextQuery(query), errorHandler);
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

  public static PaginationFilter subproject(String subproject) {
    return new PaginationFilter() {
      
      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationBySubproject(item, new TextQuery(subproject), errorHandler);
      }
    };
  }

  public static PaginationFilter user(String loginName, boolean creator) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByUser(item, new TextQuery(loginName), creator, errorHandler);
      }
    };
  }

  public static PaginationFilter userOrGroup(String name, boolean creator) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByUserOrGroup(item, new TextQuery(name), creator, errorHandler);
      }
    };
  }

  public static PaginationFilter freezer(String freezer) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByFreezer(item, new TextQuery(freezer), errorHandler);
      }
    };
  }


  public static PaginationFilter requisitionId(String requisitionId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByRequisitionId(item, new TextQuery(requisitionId), errorHandler);
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
        sink.restrictPaginationByTimepoint(item, new TextQuery(timepoint), errorHandler);
      }
    };
  }

  public static PaginationFilter tissueOrigin(String origin) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByTissueOrigin(item, new TextQuery(origin), errorHandler);
      }
    };
  }

  public static PaginationFilter tissueType(String type) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByTissueType(item, new TextQuery(type), errorHandler);
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

  public abstract <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler);

}
