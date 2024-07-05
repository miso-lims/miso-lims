package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;

import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView_;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Holds all of the necessary objects and provides methods to help build queries via the JPA
 * Criteria API. Type parameters: Query return type and root Hibernate entity class for the query
 */
public class QueryBuilder<R, T> {

  private final Session session;
  private final CriteriaBuilder criteriaBuilder;
  private final CriteriaQuery<R> query;
  private final Root<T> root;
  private List<Predicate> predicates;
  private List<Order> orders;
  private ResultTransformer resultTransformer = null;

  public QueryBuilder(Session session, Class<T> entityClass, Class<R> resultClass) {
    this.session = session;
    this.criteriaBuilder = session.getCriteriaBuilder();
    this.query = criteriaBuilder.createQuery(resultClass);
    this.root = query.from(entityClass);
    if (resultClass.isAssignableFrom(entityClass)) {
      @SuppressWarnings("unchecked")
      Root<? extends R> castedRoot = (Root<? extends R>) root;
      query.select(castedRoot);
    }
  }

  public QueryBuilder(Session session, Class<T> entityClass, Class<R> resultClass,
      ResultTransformer resultTransformer) {
    this(session, entityClass, resultClass);
    this.resultTransformer = resultTransformer;
  }

  public Session getSession() {
    return session;
  }

  public CriteriaBuilder getCriteriaBuilder() {
    return criteriaBuilder;
  }

  public CriteriaQuery<R> getQuery() {
    return this.query;
  }

  public Root<T> getRoot() {
    return root;
  }

  /**
   * Get a Root of the specified subclass. Note that this also causes the query to include a WHERE
   * clause to only include objects of that subclass
   * 
   * @param subclass the subclass to look at
   * @return the root of requested subclass type
   */
  public <S extends T> Root<S> getRoot(Class<S> subclass) {
    return criteriaBuilder.treat(root, subclass);
  }

  public <X, Y, S extends Y> Join<X, S> treatJoin(Join<X, Y> join, Class<S> subclass) {
    return criteriaBuilder.treat(join, subclass);
  }

  public <X, Y> Join<X, Y> getJoin(From<?, X> from, SingularAttribute<? super X, Y> attribute) {
    return getJoin(from, attribute, JoinType.LEFT);
  }

  public <X, Y> Join<X, Y> getJoin(From<?, X> from, SingularAttribute<? super X, Y> attribute, JoinType joinType) {
    @SuppressWarnings("unchecked")
    Join<X, Y> result = (Join<X, Y>) findJoin(from, attribute, joinType)
        .orElseGet(() -> from.join(attribute, joinType));
    return result;
  }

  /**
   * Get a join. Prefer getJoin over this method as it is more type-safe. This method is primarily
   * useful when dealing with interface attributes in a generic way
   * 
   * @param <X> source
   * @param <Y> target
   * @param from source root or join
   * @param attributeName source attribute name to join on
   * @param joinClass class of target
   * @return the join
   */
  public <X, Y> Join<X, Y> getSingularJoin(From<?, X> from, String attributeName, Class<Y> joinClass) {
    return getSingularJoin(from, attributeName, joinClass, JoinType.LEFT);
  }

  public <X, Y> Join<X, Y> getSingularJoin(From<?, X> from, String attributeName, Class<Y> joinClass,
      JoinType joinType) {
    @SuppressWarnings("unchecked")
    Join<X, Y> result = (Join<X, Y>) findJoin(from, attributeName, joinType)
        .orElseGet(() -> from.join(attributeName, joinType));
    return result;
  }

  /**
   * Get a set join. Prefer getJoin over this method as it is more type-safe. This method is primarily
   * useful when dealing with interface attributes in a generic way
   * 
   * @param <X> source
   * @param <Y> target
   * @param from source root or join
   * @param attributeName source attribute name to join on
   * @param joinClass class of target
   * @return the join
   */
  public <X, Y> SetJoin<X, Y> getSetJoin(From<?, X> from, String attributeName, Class<Y> joinClass) {
    return getSetJoin(from, attributeName, joinClass, JoinType.LEFT);
  }

  public <X, Y> SetJoin<X, Y> getSetJoin(From<?, X> from, String attributeName, Class<Y> joinClass, JoinType joinType) {
    @SuppressWarnings("unchecked")
    SetJoin<X, Y> result = (SetJoin<X, Y>) findJoin(from, attributeName, joinType)
        .orElseGet(() -> from.join(attributeName, joinType));
    return result;
  }

  public <X, Y> SetJoin<X, Y> getJoin(From<?, X> from, SetAttribute<? super X, Y> attribute) {
    return getJoin(from, attribute, JoinType.LEFT);
  }

  public <X, Y> SetJoin<X, Y> getJoin(From<?, X> from, SetAttribute<? super X, Y> attribute, JoinType joinType) {
    @SuppressWarnings("unchecked")
    SetJoin<X, Y> result = (SetJoin<X, Y>) findJoin(from, attribute, joinType)
        .orElseGet(() -> from.join(attribute, joinType));
    return result;
  }

  public <X, Y> ListJoin<X, Y> getJoin(From<?, X> from, ListAttribute<? super X, Y> attribute) {
    return getJoin(from, attribute, JoinType.LEFT);
  }

  public <X, Y> ListJoin<X, Y> getJoin(From<?, X> from, ListAttribute<? super X, Y> attribute, JoinType joinType) {
    @SuppressWarnings("unchecked")
    ListJoin<X, Y> result = (ListJoin<X, Y>) findJoin(from, attribute, joinType)
        .orElseGet(() -> from.join(attribute, joinType));
    return result;
  }

  public <X, K, Y> MapJoin<X, K, Y> getJoin(From<?, X> from, MapAttribute<? super X, K, Y> attribute) {
    return getJoin(from, attribute, JoinType.LEFT);
  }

  public <X, K, Y> MapJoin<X, K, Y> getJoin(From<?, X> from, MapAttribute<? super X, K, Y> attribute,
      JoinType joinType) {
    @SuppressWarnings("unchecked")
    MapJoin<X, K, Y> result =
        (MapJoin<X, K, Y>) findJoin(from, attribute, joinType).orElseGet(() -> from.join(attribute, joinType));
    return result;
  }

  private <X, Y> Optional<Join<X, ?>> findJoin(From<?, X> from, Attribute<? super X, Y> attribute, JoinType joinType) {
    return from.getJoins().stream()
        .filter(join -> join.getAttribute().getName().equals(attribute.getName()))
        .filter(join -> join.getJoinType().equals(joinType))
        .findFirst();
  }

  private <X, Y> Optional<Join<X, ?>> findJoin(From<?, X> from, String attributeName, JoinType joinType) {
    return from.getJoins().stream()
        .filter(join -> join.getAttribute().getName().equals(attributeName))
        .filter(join -> join.getJoinType().equals(joinType))
        .findFirst();
  }

  public void setColumns(Path<?>... paths) {
    query.multiselect(paths);
  }

  public void setColumn(Selection<? extends R> selection) {
    query.select(selection);
  }

  public void addPredicate(Predicate predicate) {
    if (predicates == null) {
      predicates = new ArrayList<>();
    }
    this.predicates.add(predicate);
  }

  public <X> void addInPredicate(Path<X> path, Collection<X> items) {
    In<X> inClause = criteriaBuilder.in(path);
    items.forEach(item -> inClause.value(item));
    addPredicate(inClause);
  }

  public void addTextRestriction(Path<String> path, String text) {
    addPredicate(makeTextRestriction(path, text));
  }

  public void addTextRestriction(List<Path<String>> paths, String text) {
    if (paths == null || paths.isEmpty()) {
      // Sabotage the query to return nothing if there are no properties
      addPredicate(criteriaBuilder.or()); // empty disjunction = false
    } else if (paths.size() == 1) {
      addPredicate(makeTextRestriction(paths.get(0), text));
    } else {
      Predicate[] predicates = new Predicate[paths.size()];
      for (int i = 0; i < paths.size(); i++) {
        predicates[i] = makeTextRestriction(paths.get(i), text);
      }
      if (LimsUtils.isStringBlankOrNull(text)) {
        // if null, all search properties must be null
        addPredicate(criteriaBuilder.and(predicates));
      } else {
        // if not null, match on any search property
        addPredicate(criteriaBuilder.or(predicates));
      }
    }
  }

  public Predicate makeTextRestriction(Path<String> path, String text) {
    if (LimsUtils.isStringEmptyOrNull(text)) {
      return criteriaBuilder.isNull(path);
    } else if (isQuoted(text)) {
      String finalText = removeQuotes(text);
      return criteriaBuilder.equal(path, finalText);
    } else if (containsWildcards(text)) {
      String sanitized = sanitizeQueryString(text);
      String finalText = replaceWildcards(sanitized);
      return criteriaBuilder.like(path, finalText);
    } else {
      String sanitized = sanitizeQueryString(text);
      String finalText = "%" + sanitized + "%";
      return criteriaBuilder.like(path, finalText, '\\');
    }
  }

  public void addFreezerPredicate(From<T, ?> boxPositionJoin, String query) {
    Join<?, BoxImpl> boxJoin = getSingularJoin(boxPositionJoin, "box", BoxImpl.class);
    Join<BoxImpl, StorageLocation> locationJoin1 = getJoin(boxJoin, BoxImpl_.storageLocation);
    if (LimsUtils.isStringBlankOrNull(query)) {
      addPredicate(criteriaBuilder.isNull(locationJoin1));
    } else {
      Join<StorageLocation, StorageLocation> locationJoin2 = getJoin(locationJoin1, StorageLocation_.parentLocation);
      Join<StorageLocation, StorageLocation> locationJoin3 = getJoin(locationJoin2, StorageLocation_.parentLocation);
      Join<StorageLocation, StorageLocation> locationJoin4 = getJoin(locationJoin3, StorageLocation_.parentLocation);
      Join<StorageLocation, StorageLocation> locationJoin5 = getJoin(locationJoin4, StorageLocation_.parentLocation);
      Join<StorageLocation, StorageLocation> locationJoin6 = getJoin(locationJoin5, StorageLocation_.parentLocation);
      addPredicate(criteriaBuilder.or(
          isMatchingFreezer(locationJoin1, query),
          isMatchingFreezer(locationJoin2, query),
          isMatchingFreezer(locationJoin3, query),
          isMatchingFreezer(locationJoin4, query),
          isMatchingFreezer(locationJoin5, query),
          isMatchingFreezer(locationJoin6, query)));
    }
  }

  private Predicate isMatchingFreezer(Join<?, StorageLocation> locationJoin,
      String query) {
    return criteriaBuilder.and(
        criteriaBuilder.equal(locationJoin.get(StorageLocation_.locationUnit), LocationUnit.FREEZER),
        makeTextRestriction(locationJoin.get(StorageLocation_.alias), query));
  }

  public void addReceiptTransferDatePredicate(Date start, Date end) {
    SetJoin<T, ListTransferView> transferJoin =
        getSetJoin(root, SampleImpl_.LIST_TRANSFER_VIEWS, ListTransferView.class);
    addPredicate(criteriaBuilder.and(
        criteriaBuilder.isNotNull(transferJoin.get(ListTransferView_.senderLab)),
        criteriaBuilder.between(transferJoin.get(ListTransferView_.transferTime), start, end)));
  }

  public void addDistributionTransferDatePredicate(Date start, Date end) {
    SetJoin<T, ListTransferView> transferJoin =
        getSetJoin(root, SampleImpl_.LIST_TRANSFER_VIEWS, ListTransferView.class);
    addPredicate(criteriaBuilder.and(
        criteriaBuilder.isNotNull(transferJoin.get(ListTransferView_.recipient)),
        criteriaBuilder.between(transferJoin.get(ListTransferView_.transferTime), start, end)));
  }

  public void addDistributionRecipientPredicate(String query, String collectionProperty, String itemIdProperty,
      String id) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      Subquery<?> subquery = createSubquery(Long.class);
      Root<ListTransferView> subqueryRoot = subquery.from(ListTransferView.class);

      Join<ListTransferView, ?> join = getSingularJoin(subqueryRoot, collectionProperty, null);
      subquery.select(join.on(subqueryRoot.get(ListTransferView_.recipient).isNotNull()).get(itemIdProperty));

      this.query.where(criteriaBuilder.not(root.get(id).in(subquery)));
    } else {
      Join<T, ListTransferView> transferJoin = getSetJoin(root, "listTransferViews", ListTransferView.class);
      addTextRestriction(transferJoin.get(ListTransferView_.recipient), query);
    }
  }

  public <X> Subquery<X> createSubquery(Class<X> resultClass) {
    return query.subquery(resultClass);
  }

  public void addSort(Expression<?> expression, boolean ascending) {
    Order order = ascending ? criteriaBuilder.asc(expression) : criteriaBuilder.desc(expression);
    if (orders == null) {
      orders = new ArrayList<>();
    }
    orders.add(order);
    query.orderBy(order);
  }

  public void addGroup(List<Expression<?>> expression) {
    query.groupBy(expression);
  }

  public List<R> getResultList() {
    return buildQuery().getResultList();
  }

  /**
   * Retrieves result list from query with a particular offset and limit
   * 
   * @param offset the index of the first element to retrieve
   * @param limit the maximum number of items to retrieve. Limit of 0 indicates "no limit"
   * @return list of result class objects from query
   */
  public List<R> getResultList(int limit, int offset) {
    if (query.getSelection() == null) {
      throw new IllegalStateException("No selection has been specified");
    }
    return limit > 0 ? buildQuery().setFirstResult(offset).setMaxResults(limit).getResultList()
        : buildQuery().setFirstResult(offset).getResultList();
  }

  private Query<R> buildQuery() {
    applyPredicates();
    if (!root.getJoins().isEmpty() && query.getGroupList().isEmpty()) {
      query.distinct(true);
    }
    if (orders != null && !orders.isEmpty()) {
      query.orderBy(orders);
    }

    return resultTransformer != null ? session.createQuery(query).setResultTransformer(resultTransformer)
        : session.createQuery(query);
  }

  private void applyPredicates() {
    if (predicates == null) {
      return;
    }
    Predicate[] predicateArray = new Predicate[predicates.size()];
    predicateArray = predicates.toArray(predicateArray);
    query.where(predicateArray);
  }

  public R getSingleResultOrNull() {
    List<R> results = getResultList();
    if (results == null || results.isEmpty()) {
      return null;
    } else if (results.size() == 1) {
      return results.get(0);
    } else {
      throw new IllegalStateException("Query unexpectedly produced multiple results");
    }
  }

}
