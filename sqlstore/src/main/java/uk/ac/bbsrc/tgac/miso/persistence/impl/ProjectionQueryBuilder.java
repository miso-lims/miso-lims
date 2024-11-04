package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.Session;
import org.hibernate.query.TupleTransformer;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ProjectionQueryBuilder<R, T> {

  private final QueryBuilder<Tuple, T> queryBuilder;
  private final TupleTransformer<R> tupleTransformer;

  public ProjectionQueryBuilder(Session session, Class<T> entityClass, TupleTransformer<R> tupleTransformer) {
    this.queryBuilder = new QueryBuilder<Tuple, T>(session, entityClass, Tuple.class);
    this.tupleTransformer = tupleTransformer;
  }

  public Root<T> getRoot() {
    return queryBuilder.getRoot();
  }

  public CriteriaBuilder getCriteriaBuilder() {
    return queryBuilder.getCriteriaBuilder();
  }

  public void addPredicate(Predicate predicate) {
    queryBuilder.addPredicate(predicate);
  }

  public void setColumns(Path<?>... paths) {
    queryBuilder.getQuery().multiselect(paths);
  }

  public R getSingleResultOrNull() {
    return queryBuilder.buildQuery()
        .setTupleTransformer(tupleTransformer)
        .uniqueResult();
  }

}
