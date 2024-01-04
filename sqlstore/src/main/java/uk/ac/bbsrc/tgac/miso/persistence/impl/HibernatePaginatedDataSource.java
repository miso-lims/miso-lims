package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.Table;
import javax.persistence.TemporalType;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilterSink;
import uk.ac.bbsrc.tgac.miso.core.util.TransferType;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

/**
 * Retrieves a collection of items from Hibernate in a paginated way.
 */
@Transactional(rollbackFor = Exception.class)
public interface HibernatePaginatedDataSource<T> extends PaginatedDataSource<T>, PaginationFilterSink<Criteria> {

  @Override
  public default long count(Consumer<String> errorHandler, PaginationFilter... filters) throws IOException {
    if (filters.length == 0) {
      // try a quicker approach
      Table tableAnnotation = getRealClass().getAnnotation(Table.class);
      if (tableAnnotation != null) {
        Query query = currentSession().createSQLQuery("SELECT COUNT(*) FROM " + tableAnnotation.name());
        return ((BigInteger) query.uniqueResult()).longValueExact();
      }
    }

    Criteria criteria = createPaginationCriteria();
    for (PaginationFilter filter : filters) {
      filter.apply(this, criteria, errorHandler);
    }
    criteria.setProjection(Projections.countDistinct("id"));
    return (Long) criteria.uniqueResult();
  }

  default Criteria createPaginationCriteria() throws IOException {
    final Criteria backingCriteria = currentSession().createCriteria(getRealClass());
    Criteria criteria = new AliasTrackingCriteria(backingCriteria);
    for (AliasDescriptor descriptor : listAliases()) {
      criteria.createAlias(descriptor.getAssociationPath(), descriptor.getAlias(), descriptor.getJoinType());
    }
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    return criteria;
  }

  public Session currentSession();

  public String getFriendlyName();

  /**
   * @return the property name of the project to which the item is connected, or null if not
   *         applicable
   */
  String getProjectColumn();

  Class<? extends T> getRealClass();

  /**
   * @return an array containing the field names for all String identifier columns - usually name,
   *         alias, and/or identificationBarcode. Required for bulk lookup by identifiers (search by
   *         names feature)
   */
  default String[] getIdentifierProperties() {
    return null;
  }

  String[] getSearchProperties();

  @Override
  public default List<T> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filters)
      throws IOException {
    if (offset < 0 || limit < 0)
      throw new IOException("Limit and Offset must not be less than zero");
    String sortProperty = propertyForSortColumn(sortCol);
    Order primaryOrder = null;
    if (sortProperty != null && !"id".equals(sortProperty)) {
      primaryOrder = sortDir ? Order.asc(sortProperty) : Order.desc(sortProperty);
    }

    Criteria idCriteria = null;
    if (filters.length == 0 && (sortProperty == null || !sortProperty.contains("."))) {
      // Faster method
      ProjectionList projections = Projections.projectionList().add(Projections.property("id"));
      if (primaryOrder != null) {
        projections.add(Projections.property(sortProperty));
      }
      idCriteria = currentSession().createCriteria(getRealClass()).setProjection(projections);
    } else {
      // We need to keep both the id column and the sort column in the result set for the database to
      // provide us with sorted, duplicate-free
      // results. We will throw the sort property out later.
      ProjectionList projections = Projections.projectionList().add(Projections.groupProperty("id"));
      if (primaryOrder != null) {
        projections.add(Projections.groupProperty(sortProperty));
      }
      idCriteria = createPaginationCriteria().setProjection(projections);
    }

    Order idOrder = null;
    if (primaryOrder != null) {
      idCriteria.addOrder(primaryOrder);
      idOrder = Order.asc("id");
    } else {
      idOrder = sortDir ? Order.asc("id") : Order.desc("id");
    }
    // Always add second sort by IDs to ensure consistent order between pages (primary sort may not be
    // deterministic)
    idCriteria.addOrder(idOrder);

    for (PaginationFilter filter : filters) {
      filter.apply(this, idCriteria, errorHandler);
    }

    idCriteria.setFirstResult(offset);
    if (limit > 0) {
      idCriteria.setMaxResults(limit);
    }

    // IDs are usually Longs, but could be composite ID classes, or something else
    List<Object> ids;
    if (primaryOrder == null) {
      @SuppressWarnings("unchecked")
      List<Object> idResults = idCriteria.list();
      ids = idResults;
    } else {
      @SuppressWarnings("unchecked")
      List<Object[]> idResults = idCriteria.list();
      ids = idResults.stream().map(x -> x[0]).collect(Collectors.toList());
    }
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    // We do this in two steps to make a smaller query that that the database can optimise
    Criteria criteria = createPaginationCriteria();
    if (primaryOrder != null) {
      criteria.addOrder(primaryOrder);
    }
    criteria.addOrder(idOrder);
    criteria.add(Restrictions.in("id", ids));

    @SuppressWarnings("unchecked")
    List<T> records = criteria.list();
    return records;
  }

  /**
   * List all the aliases that should be created for the {@link Criteria}.
   */
  Iterable<AliasDescriptor> listAliases();

  /**
   * The property name for the modification/creation date of the object.
   * 
   * @return the name of the property or null if the search criterion should be ignored.
   */

  public abstract String propertyForDate(Criteria criteria, DateType type);

  public default TemporalType temporalTypeForDate(DateType type) {
    return TemporalType.TIMESTAMP;
  }

  /**
   * Determine the correct Hibernate property given the user-supplied sort column. Default
   * implementation always returns the original value unmodified
   */
  public default String propertyForSortColumn(String original) {
    return original;
  }

  /**
   * The property name for the user
   * 
   * @param creator if true, the user that created this object; otherwise the last modifier
   * @return the name of the property or null if the search criterion should be ignored
   */
  public abstract String propertyForUser(boolean creator);

  @Override
  public default void restrictPaginationByArchived(Criteria criteria, boolean isArchived,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is not archivable.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByArrayed(Criteria criteria, boolean isArrayed, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be arrayed.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByBatchId(Criteria criteria, String batchId, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s does not have batches.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByBox(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be boxed.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByBoxType(Criteria criteria, BoxType boxType, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no box type.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByBoxUse(Criteria criteria, long id, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no use.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByClass(Criteria criteria, String name, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is exempt from class strugle.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByDate(Criteria criteria, Date start, Date end, DateType type,
      Consumer<String> errorHandler) {
    String property = propertyForDate(criteria, type);
    TemporalType temporalType = temporalTypeForDate(type);
    if (property != null) {
      Object startObject = start;
      Object endObject = end;
      if (temporalType == TemporalType.DATE) {
        if (start != null) {
          startObject = LocalDate.ofInstant(start.toInstant(), ZoneId.systemDefault());
        }
        if (end != null) {
          endObject = LocalDate.ofInstant(end.toInstant(), ZoneId.systemDefault());
        }
      }
      if (start != null) {
        if (end != null) {
          criteria.add(Restrictions.between(property, startObject, endObject));
        } else {
          criteria.add(Restrictions.ge(property, startObject));
        }
      } else if (end != null) {
        criteria.add(Restrictions.le(property, endObject));
      } else {
        throw new IllegalArgumentException("Start and/or end date must be specified");
      }
    } else {
      errorHandler.accept(String.format("%s has no %s date.", getFriendlyName(), type.getLabel()));
    }
  }

  public default void restrictPaginationByLocalDate(Criteria criteria, Date start, Date end, DateType type,
      Consumer<String> errorHandler) {
    LocalDate startDate = LocalDate.ofInstant(start.toInstant(), ZoneId.systemDefault());
    LocalDate endDate = LocalDate.ofInstant(end.toInstant(), ZoneId.systemDefault());
    String property = propertyForDate(criteria, type);
    if (property != null) {
      criteria.add(Restrictions.between(property, startDate, endDate));
    } else {
      errorHandler.accept(String.format("%s has no %s date.", getFriendlyName(), type.name().toLowerCase()));
    }
  }

  @Override
  public default void restrictPaginationByDesign(Criteria criteria, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no design.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByDistributionRecipient(Criteria criteria, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be distributed.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByExternalName(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no external name.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByFulfilled(Criteria criteria, boolean isFulfilled, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no fulfillment (nor existential dread).", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByDraft(Criteria criteria, boolean isDraft, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be drafted.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByGroupId(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no group ID (and we are all happier for it).", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByGhost(Criteria criteria, boolean isGhost, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no ghosts", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByHealth(Criteria criteria, EnumSet<HealthType> healths,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no health information.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationById(Criteria criteria, long id, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("id", id));
  }

  @Override
  public default void restrictPaginationByIds(Criteria criteria, List<Long> ids, Consumer<String> errorHandler) {
    if (ids == null || ids.isEmpty()) {
      // IDs should never be negative, so this just ensures nothing is returned
      criteria.add(Restrictions.eq("id", -1L));
    } else {
      criteria.add(Restrictions.in("id", ids));
    }
  }

  @Override
  public default void restrictPaginationByIndex(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is not indexed.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByLab(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no lab associated with it.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByModel(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no model associated with it.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByInstrumentType(Criteria criteria, InstrumentType type,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by instrument type.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByKitType(Criteria criteria, KitType type, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by pool.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByKitName(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by kit name.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByPending(Criteria criteria, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is not dependable.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByPipeline(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s does not have a pipeline.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is not platform-specific.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByPoolId(Criteria criteria, long poolId, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by pool.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    String column = getProjectColumn();
    if (column != null) {
      criteria.add(Restrictions.eq(column, projectId));
    } else {
      errorHandler.accept(String.format("%s cannot be filtered by project.", getFriendlyName()));
    }
  }

  @Override
  default void restrictPaginationByProject(Criteria criteria, String project, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by project.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByQuery(Criteria criteria, String query, Consumer<String> errorHandler) {
    String[] properties = getSearchProperties();
    if (properties == null || properties.length == 0) {
      errorHandler
          .accept(String.format("%s does not have text fields that can be queried in this way.", getFriendlyName()));
    }
    criteria.add(DbUtils.textRestriction(query, properties));
  }

  @Override
  default void restrictPaginationBySequencerId(Criteria criteria, long id, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by sequencer.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationBySequencingParametersId(Criteria criteria, long id, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by sequencing parameters.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByStatus(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by status.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationBySequencingParametersName(Criteria criteria, String query,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by sequencing parameters.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByStage(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by subproject.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationBySubproject(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by subproject.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByUser(Criteria criteria, String query, boolean creator,
      Consumer<String> errorHandler) {
    String property = propertyForUser(creator);
    if (property != null) {
      criteria.createAlias(property, property)
          .createAlias(property + ".groups", property + "Group", JoinType.LEFT_OUTER_JOIN)
          .add(DbUtils.textRestriction(query, property + ".loginName"));
    } else {
      errorHandler.accept(String.format("%s has no %s.", getFriendlyName(), (creator ? "creator" : "modifier")));
    }
  }

  @Override
  public default void restrictPaginationByUserOrGroup(Criteria criteria, String query, boolean creator,
      Consumer<String> errorHandler) {
    String property = propertyForUser(creator);
    if (property != null) {
      criteria.createAlias(property, property)
          .createAlias(property + ".groups", property + "Group")
          .add(DbUtils.textRestriction(query, property + ".loginName", property + "Group.name"));
    } else {
      errorHandler.accept(String.format("%s has no %s.", getFriendlyName(), (creator ? "creator" : "modifier")));
    }
  }

  @Override
  public default void restrictPaginationByFreezer(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by freezer.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByRequisitionId(Criteria criteria, long requisitionId,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by requisition ID.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByRequisition(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by requisition.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationBySupplementalToRequisitionId(Criteria criteria, long requisitionId,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by supplemental to requisition ID.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByRebNumber(Criteria item, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by REB"));
  }

  @Override
  public default void restrictPaginationByRecipientGroups(Criteria item, Collection<Group> groups,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no recipient groups", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByTransferType(Criteria item, TransferType transferType,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no transfer type", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByTimepoint(Criteria item, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no timepoint", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByTissueOrigin(Criteria item, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no tissue origin", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByTissueType(Criteria item, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no tissue type", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByCategory(Criteria item, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no category", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByCategory(Criteria item, SopCategory category, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no category", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByWorksetId(Criteria criteria, long worksetId, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by workset.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByWorkstation(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by workstation.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByWorkstationId(Criteria criteria, long workstationId, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by workstation.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByIdentifiers(Criteria criteria, Collection<String> identifiers,
      Consumer<String> errorHandler) {
    String[] fields = getIdentifierProperties();
    if (fields == null) {
      errorHandler.accept(String.format("%s cannot be filtered by identifiers", getFriendlyName()));
      return;
    }
    Disjunction disjunction = Restrictions.disjunction();
    for (String field : fields) {
      disjunction.add(Restrictions.in(field, identifiers));
    }
    criteria.add(disjunction);
  }

  @Override
  public default void restrictPaginationByIdentityIds(Criteria criteria, List<Long> identityIds,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by identity ID.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByBarcode(Criteria criteria, String barcode, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by barcode.", getFriendlyName()));
  }
}
