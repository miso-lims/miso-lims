package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringBlankOrNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

/**
 * Retrieves a collection of items from Hibernate in a paginated way.
 */
@Transactional(rollbackFor = Exception.class)
public interface BaseHibernatePaginatedDataSource<T, Filter extends PaginationFilter> extends PaginatedDataSource<T, Filter> {

  @Override
  public default long count(Filter filter) throws IOException {
    Criteria criteria = createPaginationCriteria();
    if (!isStringBlankOrNull(filter.getQuery())) {
      criteria.add(searchRestrictions(filter.getQuery()));
    }
    if (filter.getProjectId() != null) {
      criteria.add(Restrictions.eq(getProjectColumn(), filter.getProjectId()));
    }
    setAdditionalPaginationCriteria(filter, criteria);
    criteria.setProjection(Projections.countDistinct("id"));
    return ((Long) criteria.uniqueResult()).intValue();

  }

  default Criteria createPaginationCriteria() throws IOException {
    Criteria criteria = currentSession().createCriteria(getRealClass());
    for (String alias : listAliases()) {
      criteria.createAlias(alias, alias);
    }
    return criteria;
  }

  Session currentSession();

  /**
   * Get the property name of the project to which the item is connected.
   * 
   * @return
   */
  String getProjectColumn();

  Class<? extends T> getRealClass();

  @Override
  public default List<T> list(Filter filter, int offset, int limit, boolean sortDir, String sortCol)
      throws IOException {

    if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must not be less than zero");
    String sortProperty = propertyForSortColumn(sortCol);
    Order order = sortDir ? Order.asc(sortProperty) : Order.desc(sortProperty);

    Criteria idCriteria = createPaginationCriteria();
    idCriteria.addOrder(order);

    if (!isStringBlankOrNull(filter.getQuery())) {
      idCriteria.add(searchRestrictions(filter.getQuery()));
    }
    if (filter.getProjectId() != null) {
      idCriteria.add(Restrictions.eq(getProjectColumn(), filter.getProjectId()));
    }
    setAdditionalPaginationCriteria(filter, idCriteria);

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
   * Determine the correct Hibernate property given the user-supplied sort column.
   */
  String propertyForSortColumn(String original);

  /**
   * Create a set of restrictions given the user-supplied search string.
   */
  Criterion searchRestrictions(String query);

  /**
   * Add additional restrictions to the {@link Criteria} object. Note, during listing, a collection of IDs is fetched and then these are
   * used to perform a second query. In that instance, this method is <b>not</b> called for the ID-driven fetch.
   */
  default void setAdditionalPaginationCriteria(Filter filter, Criteria criteria) {
  }
}
