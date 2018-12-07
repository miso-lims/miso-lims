package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringBlankOrNull;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.Table;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilterSink;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

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
    for (String alias : listAliases()) {
      String[] parts = alias.split("\\.");
      criteria.createAlias(alias, parts[parts.length - 1]);
    }
    return criteria;
  }

  Session currentSession();

  public String getFriendlyName();

  /**
   * @return the property name of the project to which the item is connected, or null if not applicable
   */
  String getProjectColumn();

  Class<? extends T> getRealClass();

  String[] getSearchProperties();

  @Override
  public default List<T> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filters)
      throws IOException {

    if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must not be less than zero");
    String sortProperty = propertyForSortColumn(sortCol);
    Order order = sortDir ? Order.asc(sortProperty) : Order.desc(sortProperty);

    Criteria idCriteria = createPaginationCriteria();
    idCriteria.addOrder(order);

    for (PaginationFilter filter : filters) {
      filter.apply(this, idCriteria, errorHandler);
    }

    idCriteria.setFirstResult(offset);
    if (limit > 0) {
      idCriteria.setMaxResults(limit);
    }
    // We need to keep both the id column and the sort column in the result set for the database to provide us with sorted, duplicate-free
    // results. We will throw the sort property out later.
    idCriteria.setProjection(
        Projections.projectionList().add(Projections.groupProperty("id")).add(Projections.groupProperty(sortProperty)));

    @SuppressWarnings("unchecked")
    List<Object[]> ids = idCriteria.list();
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    // We do this in two steps to make a smaller query that that the database can optimise
    Criteria criteria = createPaginationCriteria();
    criteria.addOrder(order);
    criteria.add(Restrictions.in("id", ids.stream().map(x -> x[0]).toArray()));

    @SuppressWarnings("unchecked")
    List<T> records = criteria.list();
    return records;
  }

  /**
   * List all the aliases that should be created for the {@link Criteria}. Probably at least "derivedInfo".
   */
  Iterable<String> listAliases();

  /**
   * The property name for the modification/creation date of the object.
   * 
   * @return the name of the property or null if the search criterion should be ignored.
   */

  public abstract String propertyForDate(Criteria criteria, DateType type);

  /**
   * Determine the correct Hibernate property given the user-supplied sort column.
   */
  String propertyForSortColumn(String original);

  /**
   * The property name for the login name of a user.
   * 
   * @param creator if the true, the user that created this object; otherwise the last modifier
   * @return the name of the property or null if the search criterion should be ignored.
   */
  public abstract String propertyForUserName(Criteria criteria, boolean creator);

  @Override
  default void restrictPaginationByArchived(Criteria criteria, boolean isArchived, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is not archivable.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByArrayed(Criteria criteria, boolean isArrayed, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be arrayed.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByBox(Criteria criteria, String name, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be boxed.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByBoxUse(Criteria criteria, long id, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no use.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByClass(Criteria criteria, String name, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is exempt from class strugle.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByDate(Criteria criteria, Date start, Date end, DateType type, Consumer<String> errorHandler) {
    String property = propertyForDate(criteria, type);
    if (property != null) {
      criteria.add(Restrictions.between(property, start, end));
    } else {
      errorHandler.accept(String.format("%s has no %s date.", getFriendlyName(), type.name().toLowerCase()));
    }
  }

  @Override
  default void restrictPaginationByExternalName(Criteria criteria, String name, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no external name.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByFulfilled(Criteria criteria, boolean isFulfilled, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no fulfillment (nor existential dread).", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByGroupId(Criteria criteria, String groupId, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no group ID (and we are all happier for it).", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByHealth(Criteria criteria, EnumSet<HealthType> healths, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no health information.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is not indexed.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByInstitute(Criteria criteria, String name, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s has no institute associated with it.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByInstrumentType(Criteria criteria, InstrumentType type, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by instrument type.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByKitType(Criteria criteria, KitType type, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by pool.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByKitName(Criteria criteria, String name, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by kit name.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByPending(Criteria criteria, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s is not dependable.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
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
  default void restrictPaginationByQuery(Criteria criteria, String query, boolean exact, Consumer<String> errorHandler) {
    if (!isStringBlankOrNull(query)) {
      criteria.add(DbUtils.searchRestrictions(query, exact, getSearchProperties()));
    }
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
  default void restrictPaginationBySequencingParametersName(Criteria criteria, String name, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by sequencing parameters.", getFriendlyName()));
  }

  @Override
  default void restrictPaginationBySubproject(Criteria criteria, String query, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by subproject.", getFriendlyName()));
  }

  @Override
  public default void restrictPaginationByUser(Criteria criteria, String userName, boolean creator, Consumer<String> errorHandler) {
    String property = propertyForUserName(criteria, creator);
    if (property != null) {
      criteria.add(Restrictions.ilike(property, userName, MatchMode.START));
    } else {
      errorHandler.accept(String.format("%s has no %s.", getFriendlyName(), (creator ? "creator" : "modifier")));
    }
  }
}
