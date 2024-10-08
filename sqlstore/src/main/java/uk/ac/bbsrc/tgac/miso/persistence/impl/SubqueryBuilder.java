package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class SubqueryBuilder<R, T> {
  private final Subquery<R> subquery;
  private final Root<T> root;
  private List<Predicate> predicates;

  public SubqueryBuilder(CriteriaQuery<?> query, Class<T> entityClass, Class<R> resultType) {
    this.subquery = query.subquery(resultType);
    this.root = subquery.from(entityClass);
  }

  public Root<T> getRoot() {
    return root;
  }

  public void setColumn(Path<R> selection) {
    subquery.select(selection);
  }

  public void addPredicate(Predicate predicate) {
    if (predicates == null) {
      predicates = new ArrayList<>();
    }
    this.predicates.add(predicate);
  }

  public Subquery<R> build() {
    Predicate[] predicateArray = new Predicate[predicates.size()];
    predicateArray = predicates.toArray(predicateArray);
    return subquery.where(predicateArray);
  }

}
