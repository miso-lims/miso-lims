package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Tuple;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Group_;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilterSink;
import uk.ac.bbsrc.tgac.miso.core.util.TransferType;

/**
 * <p>
 * Retrieves a collection of items from Hibernate in a paginated way using the JPA Criteria API and
 * QueryBuilder. Type parameters: Query return type, and root Hibernate entity for query.
 * </p>
 * 
 * <p>
 * <strong>Limitations</strong>: The JPA Criteria API has some issues with polymorphism, which mean
 * this interface may not work well for Samples or some other entities that have subclasses:
 * </p>
 * 
 * <ul>
 * <li>When adding predicates based on subclass attributes, additional predicates may be added to
 * limit results to the relevant subclass, which may not be desirable. There are work-arounds for
 * this, but none perfect, and they can be pretty ugly</li>
 * <li>It is not possible to sort by subclass attributes</li>
 * </ul>
 */
@Transactional(rollbackFor = Exception.class)
public interface JpaCriteriaPaginatedDataSource<R, T extends R>
    extends PaginatedDataSource<R>, PaginationFilterSink<QueryBuilder<?, T>> {

  public default QueryBuilder<R, T> getQueryBuilder() {
    return new QueryBuilder<>(currentSession(), getEntityClass(), getResultClass());
  }

  @Override
  public default long count(Consumer<String> errorHandler, PaginationFilter... filters) throws IOException {
    if (filters.length == 0) {
      // try a quicker approach
      Table tableAnnotation = getEntityClass().getAnnotation(Table.class);
      if (tableAnnotation != null) {
        Query query = currentSession().createNativeQuery("SELECT COUNT(*) FROM " + tableAnnotation.name());
        return ((BigInteger) query.getSingleResult()).longValueExact();
      }
    }

    LongQueryBuilder<T> queryBuilder = new LongQueryBuilder<>(currentSession(), getEntityClass());
    for (PaginationFilter filter : filters) {
      filter.apply(this, queryBuilder, errorHandler);
    }

    return queryBuilder.getCount();
  }

  public Session currentSession();

  public String getFriendlyName();

  /**
   * @return the Hibernate entity class
   */
  Class<T> getEntityClass();

  /**
   * @return the class used for results, which may be the Hibernate entity class or its interface
   */
  Class<R> getResultClass();

  /**
   * @return a list containing the metamodel fields for all String identifier columns - usually name,
   *         alias, and/or identificationBarcode. Required for bulk lookup by identifiers (search by
   *         names feature)
   */
  default List<SingularAttribute<T, String>> getIdentifierProperties() {
    return null;
  }

  List<SingularAttribute<T, String>> getSearchProperties();

  SingularAttribute<T, ?> getIdProperty();

  @Override
  public default List<R> list(Consumer<String> errorHandler, int offset, int limit, boolean ascending, String sortCol,
      PaginationFilter... filters) throws IOException {
    if (offset < 0 || limit < 0) {
      throw new IOException("Limit and Offset must not be less than zero");
    }
    QueryBuilder<Tuple, T> idQueryBuilder = new QueryBuilder<>(currentSession(), getEntityClass(), Tuple.class);
    QueryBuilder<R, T> resultQueryBuilder = new QueryBuilder<>(currentSession(), getEntityClass(), getResultClass());

    Path<?> idProperty = idQueryBuilder.getRoot().get(getIdProperty());
    Path<?> sortProperty = sortCol == null ? null : propertyForSortColumn(idQueryBuilder.getRoot(), sortCol);
    if (sortProperty != null && !idProperty.equals(sortProperty)) {
      idQueryBuilder.addSort(sortProperty, ascending);
      resultQueryBuilder.addSort(sortProperty, ascending);
      idQueryBuilder.setColumns(idProperty, sortProperty);
    } else {
      idQueryBuilder.setColumns(idProperty);
    }
    // Always add second sort by IDs to ensure consistent order between pages (primary sort may not be
    // deterministic)
    idQueryBuilder.addSort(idProperty, ascending);
    resultQueryBuilder.addSort(idProperty, ascending);

    for (PaginationFilter filter : filters) {
      filter.apply(this, idQueryBuilder, errorHandler);
    }
    List<Tuple> tuples = idQueryBuilder.getResultList(limit, offset);
    // IDs are usually Longs, but could be composite ID classes, or something else
    List<Object> ids = tuples.stream().map(x -> x.get(0)).toList();

    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    // We do this in two steps to make a smaller query that that the database can optimise
    resultQueryBuilder.addPredicate(idProperty.in(ids));
    List<R> records = resultQueryBuilder.getResultList();
    return records;
  }

  /**
   * The property name for the modification/creation date of the object.
   * 
   * @return the name of the property or null if the search criterion should be ignored.
   */

  public abstract SingularAttribute<T, ?> propertyForDate(DateType type);

  /**
   * Determine the correct Hibernate property given the user-supplied sort column. Default
   * implementation always returns the original value unmodified
   */
  public default Path<?> propertyForSortColumn(Root<T> root, String original) {
    return root.get(original);
  }

  /**
   * The property name for the user
   * 
   * @param creator if true, the user that created this object; otherwise the last modifier
   * @return the name of the property or null if the search criterion should be ignored
   */
  public abstract SingularAttribute<T, ? extends UserImpl> propertyForUser(boolean creator);

  @Override
  public default void restrictPaginationByArchived(QueryBuilder<?, T> builder, boolean isArchived,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is not archivable.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByArrayed(QueryBuilder<?, T> builder, boolean isArrayed,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be arrayed.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByBatchId(QueryBuilder<?, T> builder, String batchId,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s does not have batches.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByBox(QueryBuilder<?, T> builder, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be boxed.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByBoxType(QueryBuilder<?, T> builder, BoxType boxType,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no box type.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByBoxUse(QueryBuilder<?, T> builder, long id, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no use.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByClass(QueryBuilder<?, T> builder, String name,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is exempt from class strugle.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByDate(QueryBuilder<?, T> builder, Date start, Date end, DateType type,
      Consumer<String> errorHandler) {
    SingularAttribute<T, ?> property = propertyForDate(type);
    if (property != null) {
      Path<?> propertyPath = builder.getRoot().get(property);
      if (propertyPath.getJavaType() == Date.class) {
        @SuppressWarnings("unchecked")
        Path<Date> dateProperty = (Path<Date>) propertyPath;
        builder.addPredicate(builder.getCriteriaBuilder().between(dateProperty, start, end));
      } else if (propertyPath.getJavaType() == LocalDate.class) {
        @SuppressWarnings("unchecked")
        Path<LocalDate> localDateProperty = (Path<LocalDate>) propertyPath;
        LocalDate startDate = LocalDate.ofInstant(start.toInstant(), ZoneId.systemDefault());
        LocalDate endDate = LocalDate.ofInstant(end.toInstant(), ZoneId.systemDefault());
        builder.addPredicate(builder.getCriteriaBuilder().between(localDateProperty, startDate, endDate));
      } else {
        throw new IllegalArgumentException("Unhandled date class: %s".formatted(propertyPath.getJavaType().getName()));
      }
    } else {
      errorHandler.accept(String.format("%s has no %s date.", getFriendlyName(), type.name().toLowerCase()));
    }
  }

  @Override
  public default void restrictPaginationByDesign(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no design.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByDistributionRecipient(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be distributed.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByExternalName(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no external name.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByFulfilled(QueryBuilder<?, T> builder, boolean isFulfilled,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no fulfillment (nor existential dread).", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByDraft(QueryBuilder<?, T> builder, boolean isDraft, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be drafted.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByGroupId(QueryBuilder<?, T> builder, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no group ID (and we are all happier for it).", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByGhost(QueryBuilder<?, T> builder, boolean isGhost, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no ghosts", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByHealth(QueryBuilder<?, T> builder, EnumSet<HealthType> healths,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no health information.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationById(QueryBuilder<?, T> builder, long id, Consumer<String> errorHandler) {
    Path<?> idPath = builder.getRoot().get(getIdProperty());
    builder.addPredicate(builder.getCriteriaBuilder().equal(idPath, id));
  }

  @Override
  public default void restrictPaginationByIds(QueryBuilder<?, T> builder, List<Long> ids,
      Consumer<String> errorHandler) {
    if (ids == null || ids.isEmpty()) {
      // Sabotage the query to return nothing if there are no IDs
      builder.addPredicate(builder.getCriteriaBuilder().or()); // empty disjunction = false
    } else {
      Path<?> idPath = builder.getRoot().get(getIdProperty());
      builder.addPredicate(idPath.in(ids));
    }
  }

  @Override
  public default void restrictPaginationByIndex(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is not indexed.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByLab(QueryBuilder<?, T> builder, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no lab associated with it.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByModel(QueryBuilder<?, T> builder, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no model associated with it.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByInstrumentType(QueryBuilder<?, T> builder, InstrumentType type,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by instrument type.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByKitType(QueryBuilder<?, T> builder, KitType type, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by pool.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByKitName(QueryBuilder<?, T> builder, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by kit name.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByPending(QueryBuilder<?, T> builder, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is not dependable.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByPipeline(QueryBuilder<?, T> builder, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s does not have a pipeline.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByPlatformType(QueryBuilder<?, T> builder, PlatformType platformType,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is not platform-specific.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByPoolId(QueryBuilder<?, T> builder, long poolId, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by pool.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByProjectId(QueryBuilder<?, T> builder, long projectId,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by project.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByProject(QueryBuilder<?, T> builder, String project, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by project.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByQuery(QueryBuilder<?, T> builder, String query, Consumer<String> errorHandler) {
    List<Path<String>> properties = pathsFromAttributes(builder, getSearchProperties());
    if (properties == null || properties.isEmpty()) {
      errorHandler
          .accept(String.format("%s does not have text fields that can be queried in this way.", getFriendlyName()));
    }
    builder.addTextRestriction(properties, query);
  }

  private List<Path<String>> pathsFromAttributes(QueryBuilder<?, T> builder,
      Collection<SingularAttribute<T, String>> attributes) {
    return attributes.stream()
        .map(x -> builder.getRoot().get(x))
        .toList();
  }

  @Override
  default void restrictPaginationBySequencerId(QueryBuilder<?, T> builder, long id, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by sequencer.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationBySequencingParametersId(QueryBuilder<?, T> builder, long id,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by sequencing parameters.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByStatus(QueryBuilder<?, T> builder, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by status.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationBySequencingParametersName(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by sequencing parameters.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByStage(QueryBuilder<?, T> builder, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by subproject.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationBySubproject(QueryBuilder<?, T> builder, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by subproject.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByUser(QueryBuilder<?, T> builder, String query, boolean creator,
      Consumer<String> errorHandler) {
    SingularAttribute<T, ? extends UserImpl> userAttribute = propertyForUser(creator);
    if (userAttribute == null) {
      errorHandler.accept(String.format("%s has no %s.", getFriendlyName(), (creator ? "creator" : "modifier")));
    } else {
      Join<T, ? extends UserImpl> userJoin = builder.getJoin(builder.getRoot(), userAttribute);
      builder.addPredicate(builder.getCriteriaBuilder().equal(userJoin.get(UserImpl_.loginName), query));
    }
  }

  @Override
  public default void restrictPaginationByUserOrGroup(QueryBuilder<?, T> builder, String query, boolean creator,
      Consumer<String> errorHandler) {
    Join<T, ? extends UserImpl> userJoin =
        builder.getJoin(builder.getRoot(), propertyForUser(creator));
    if (userJoin != null) {
      SetJoin<? extends UserImpl, Group> groupJoin = builder.getJoin(userJoin, UserImpl_.groups);
      builder.addTextRestriction(Arrays.asList(userJoin.get(UserImpl_.loginName), groupJoin.get(Group_.name)), query);
    } else {
      errorHandler.accept(String.format("%s has no %s.", getFriendlyName(), (creator ? "creator" : "modifier")));
    }
  }

  @Override
  public default void restrictPaginationByFreezer(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by freezer.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByRequisitionId(QueryBuilder<?, T> builder, long requisitionId,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by requisition ID.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByRequisition(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by requisition.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationBySupplementalToRequisitionId(QueryBuilder<?, T> builder, long requisitionId,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by supplemental to requisition ID.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByRebNumber(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by REB"));
  }

  @Override
  public default void restrictPaginationByRecipientGroups(QueryBuilder<?, T> builder, Collection<Group> groups,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no recipient groups", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByTransferType(QueryBuilder<?, T> builder, TransferType transferType,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no transfer type", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByTimepoint(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no timepoint", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByTissueOrigin(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no tissue origin", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByTissueType(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no tissue type", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByCategory(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no category", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByCategory(QueryBuilder<?, T> builder, SopCategory category,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no category", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByWorksetId(QueryBuilder<?, T> builder, long worksetId,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by workset.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByWorkstation(QueryBuilder<?, T> builder, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by workstation.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByWorkstationId(QueryBuilder<?, T> builder, long workstationId,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by workstation.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByIdentifiers(QueryBuilder<?, T> builder, Collection<String> identifiers,
      Consumer<String> errorHandler) {
    List<Path<String>> fields = pathsFromAttributes(builder, getIdentifierProperties());
    if (fields == null || fields.isEmpty()) {
      errorHandler.accept(String.format("%s cannot be filtered by identifiers", getFriendlyName()));
      return;
    }
    Predicate disjunction = builder.getCriteriaBuilder().disjunction();
    for (Path<String> field : fields) {
      disjunction = builder.getCriteriaBuilder().or(disjunction, field.in(identifiers));
    }
    builder.addPredicate(disjunction);
  }

  @Override
  public default void restrictPaginationByIdentityIds(QueryBuilder<?, T> builder, List<Long> identityIds,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by identity ID.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByBarcode(QueryBuilder<?, T> builder, String barcode,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by barcode.", getFriendlyName()));
  }
}
