package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.ResultTransformer;

public class AliasTrackingCriteria implements Criteria {
  private final Set<String> aliases = new HashSet<>();
  private final Criteria backingCriteria;

  public AliasTrackingCriteria(Criteria backingCriteria) {
    this.backingCriteria = backingCriteria;
  }

  @Override
  public Criteria add(Criterion criterion) {
    backingCriteria.add(criterion);
    return this;
  }

  @Override
  public Criteria addOrder(Order order) {
    backingCriteria.addOrder(order);
    return this;
  }

  @Override
  public Criteria createAlias(String associationPath, String alias) throws HibernateException {
    if (!aliases.contains(alias)) {
      backingCriteria.createAlias(associationPath, alias);
      aliases.add(alias);
    }
    return this;
  }

  @SuppressWarnings("deprecation")
  @Override
  public Criteria createAlias(String associationPath, String alias, int joinType) throws HibernateException {
    if (!aliases.contains(alias)) {
      backingCriteria.createAlias(associationPath, alias, joinType);
      aliases.add(alias);
    }
    return this;
  }

  @SuppressWarnings("deprecation")
  @Override
  public Criteria createAlias(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
    if (!aliases.contains(alias)) {
      backingCriteria.createAlias(associationPath, alias, joinType, withClause);
      aliases.add(alias);
    }
    return this;
  }

  @Override
  public Criteria createAlias(String associationPath, String alias, JoinType joinType) throws HibernateException {
    if (!aliases.contains(alias)) {
      backingCriteria.createAlias(associationPath, alias, joinType);
      aliases.add(alias);
    }
    return this;
  }

  @Override
  public Criteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException {
    if (!aliases.contains(alias)) {
      backingCriteria.createAlias(associationPath, alias, joinType, withClause);
      aliases.add(alias);
    }
    return this;
  }

  @Override
  public Criteria createCriteria(String associationPath) throws HibernateException {
    return new AliasTrackingCriteria(backingCriteria.createCriteria(associationPath));
  }

  @SuppressWarnings("deprecation")
  @Override
  public Criteria createCriteria(String associationPath, int joinType) throws HibernateException {
    return new AliasTrackingCriteria(backingCriteria.createCriteria(associationPath, joinType));
  }

  @Override
  public Criteria createCriteria(String associationPath, JoinType joinType) throws HibernateException {
    return new AliasTrackingCriteria(backingCriteria.createCriteria(associationPath, joinType));
  }

  @Override
  public Criteria createCriteria(String associationPath, String alias) throws HibernateException {
    return new AliasTrackingCriteria(backingCriteria.createCriteria(associationPath, alias));
  }

  @SuppressWarnings("deprecation")
  @Override
  public Criteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException {
    return new AliasTrackingCriteria(backingCriteria.createCriteria(associationPath, alias, joinType));
  }

  @SuppressWarnings("deprecation")
  @Override
  public Criteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
    return new AliasTrackingCriteria(backingCriteria.createCriteria(associationPath, alias, joinType, withClause));
  }

  @Override
  public Criteria createCriteria(String associationPath, String alias, JoinType joinType) throws HibernateException {
    return new AliasTrackingCriteria(backingCriteria.createCriteria(associationPath, alias, joinType));
  }

  @Override
  public Criteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause)
      throws HibernateException {
    return new AliasTrackingCriteria(backingCriteria.createCriteria(associationPath, alias, joinType, withClause));
  }

  @Override
  public String getAlias() {
    return backingCriteria.getAlias();
  }

  @Override
  public boolean isReadOnly() {
    return backingCriteria.isReadOnly();
  }

  @Override
  public boolean isReadOnlyInitialized() {
    return backingCriteria.isReadOnlyInitialized();
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List list() throws HibernateException {
    return backingCriteria.list();
  }

  @Override
  public ScrollableResults scroll() throws HibernateException {
    return backingCriteria.scroll();
  }

  @Override
  public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
    return backingCriteria.scroll(scrollMode);
  }

  @Override
  public Criteria setCacheable(boolean cacheable) {
    backingCriteria.setCacheable(cacheable);
    return this;
  }

  @Override
  public Criteria setCacheMode(CacheMode cacheMode) {
    backingCriteria.setCacheMode(cacheMode);
    return this;
  }

  @Override
  public Criteria setCacheRegion(String cacheRegion) {
    backingCriteria.setCacheRegion(cacheRegion);
    return this;
  }

  @Override
  public Criteria setComment(String comment) {
    backingCriteria.setComment(comment);
    return this;
  }

  @Override
  public Criteria addQueryHint(String s) {
    backingCriteria.addQueryHint(s);
    return this;
  }

  @Override
  public Criteria setFetchMode(String associationPath, FetchMode mode) throws HibernateException {
    backingCriteria.setFetchMode(associationPath, mode);
    return this;
  }

  @Override
  public Criteria setFetchSize(int fetchSize) {
    backingCriteria.setFetchSize(fetchSize);
    return this;
  }

  @Override
  public Criteria setFirstResult(int firstResult) {
    backingCriteria.setFirstResult(firstResult);
    return this;
  }

  @Override
  public Criteria setFlushMode(FlushMode flushMode) {
    backingCriteria.setFlushMode(flushMode);
    return this;
  }

  @Override
  public Criteria setLockMode(LockMode lockMode) {
    backingCriteria.setLockMode(lockMode);
    return this;
  }

  @Override
  public Criteria setLockMode(String alias, LockMode lockMode) {
    backingCriteria.setLockMode(alias, lockMode);
    return this;
  }

  @Override
  public Criteria setMaxResults(int maxResults) {
    backingCriteria.setMaxResults(maxResults);
    return this;
  }

  @Override
  public Criteria setProjection(Projection projection) {
    backingCriteria.setProjection(projection);
    return this;
  }

  @Override
  public Criteria setReadOnly(boolean readOnly) {
    backingCriteria.setReadOnly(readOnly);
    return this;
  }

  @Override
  public Criteria setResultTransformer(ResultTransformer resultTransformer) {
    backingCriteria.setResultTransformer(resultTransformer);
    return this;
  }

  @Override
  public Criteria setTimeout(int timeout) {
    backingCriteria.setTimeout(timeout);
    return this;
  }

  @Override
  public Object uniqueResult() throws HibernateException {
    return backingCriteria.uniqueResult();
  }
}