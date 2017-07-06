package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringBlankOrNull;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
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
    Criteria criteria = createPaginationCriteria();
    for (PaginationFilter filter : filters) {
      filter.apply(this, criteria, errorHandler);
    }
    criteria.setProjection(Projections.countDistinct("id"));
    return ((Long) criteria.uniqueResult()).intValue();

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
   * Get the property name of the project to which the item is connected.
   * 
   * @return
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
  default void restrictPaginationByBox(Criteria criteria, String name, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " cannot be boxed.");
  }

  @Override
  default void restrictPaginationByBoxUse(Criteria criteria, long id, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " has no use.");
  }

  @Override
  public default void restrictPaginationByClass(Criteria criteria, String name, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " is exempt from class strugle.");
  }

  @Override
  public default void restrictPaginationByDate(Criteria criteria, Date start, Date end, DateType type, Consumer<String> errorHandler) {
    String property = propertyForDate(criteria, type);
    if (property != null) {
      criteria.add(Restrictions.between(property, start, end));
    } else {
      errorHandler.accept(getFriendlyName() + " has no " + type.name().toLowerCase() + " date.");
    }
  }

  @Override
  default void restrictPaginationByExternalName(Criteria criteria, String name, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " has no external name.");
  }

  @Override
  default void restrictPaginationByFulfilled(Criteria criteria, boolean isFulfilled, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " has no fulfillment (nor existential dread).");
  }

  @Override
  public default void restrictPaginationByHealth(Criteria criteria, EnumSet<HealthType> healths, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " has no health information.");
  }

  @Override
  public default void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " has no indices.");
  }

  @Override
  default void restrictPaginationByInstitute(Criteria criteria, String name, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " has no institute associated with it.");
  }

  @Override
  default void restrictPaginationByKitType(Criteria criteria, KitType type, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " cannot be filtered by pool.");
  }

  @Override
  default void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " is not platform-specific.");
  }

  @Override
  default void restrictPaginationByPoolId(Criteria criteria, long poolId, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " cannot be filtered by pool.");
  }

  @Override
  default void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    String column = getProjectColumn();
    if (column != null) {
      criteria.add(Restrictions.eq(column, projectId));
    } else {
      errorHandler.accept(getFriendlyName() + " cannot be filtered by project.");
    }
  }

  @Override
  default void restrictPaginationByQuery(Criteria criteria, String query, Consumer<String> errorHandler) {
    if (!isStringBlankOrNull(query)) {
      criteria.add(DbUtils.searchRestrictions(query, getSearchProperties()));
    }
  }

  @Override
  default void restrictPaginationBySequencerId(Criteria criteria, long id, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " cannot be filtered by sequencer.");
  }

  @Override
  public default void restrictPaginationByUser(Criteria criteria, String userName, boolean creator, Consumer<String> errorHandler) {
    String property = propertyForUserName(criteria, creator);
    if (property != null) {
      criteria.add(Restrictions.ilike(property, userName, MatchMode.START));
    } else {
      errorHandler.accept(getFriendlyName() + " has no " + (creator ? "creator" : "modifier") + ".");
    }
  }

  @Override
  default void restrictPaginationByArchived(Criteria criteria, boolean isArchived, Consumer<String> errorHandler) {
    errorHandler.accept(getFriendlyName() + " is not archivable.");
  }

}
