package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.persistence.ProviderDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public abstract class HibernateProviderDao<T> implements ProviderDao<T> {

  @PersistenceContext
  EntityManager entityManager;

  private final Class<T> resultClass;
  private final Class<? extends T> entityClass;

  public HibernateProviderDao(Class<T> resultClass, Class<? extends T> entityClass) {
    this.resultClass = resultClass;
    this.entityClass = entityClass;
  }

  public HibernateProviderDao(Class<T> resultClass) {
    this.resultClass = resultClass;
    this.entityClass = resultClass;
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  protected Class<T> getResultClass() {
    return resultClass;
  }

  protected Class<? extends T> getEntityClass() {
    return entityClass;
  }

  public Session currentSession() {
    return getEntityManager().unwrap(Session.class);
  }

  @Override
  public T get(long id) throws IOException {
    return entityClass.cast(currentSession().get(entityClass, id));
  }

  @Override
  public List<T> list() throws IOException {
    CriteriaBuilder builder = currentSession().getCriteriaBuilder();
    CriteriaQuery<T> query = builder.createQuery(getResultClass());
    Root<? extends T> root = query.from(getEntityClass());
    query.select(root);
    return currentSession().createQuery(query).getResultList();
  }

  protected <V> T getBy(SingularAttribute<T, V> property, V value) {
    CriteriaBuilder builder = currentSession().getCriteriaBuilder();
    CriteriaQuery<T> query = builder.createQuery(getResultClass());
    Root<? extends T> root = query.from(getEntityClass());
    query.select(root).where(builder.equal(root.get(property), value));
    List<T> results = currentSession().createQuery(query).getResultList();
    return singleResultOrNull(results);
  }

  protected <V> T getBy(String property, V value) {
    CriteriaBuilder builder = currentSession().getCriteriaBuilder();
    CriteriaQuery<T> query = builder.createQuery(getResultClass());
    Root<? extends T> root = query.from(getEntityClass());
    query.select(root).where(builder.equal(root.get(property), value));
    List<T> results = currentSession().createQuery(query).getResultList();
    return singleResultOrNull(results);
  }

  private T singleResultOrNull(List<T> results) {
    if (results == null || results.isEmpty()) {
      return null;
    } else if (results.size() == 1) {
      return results.get(0);
    } else {
      throw new IllegalStateException("Query unexpectedly produced multiple results");
    }
  }

  protected List<T> listByIdList(String idProperty, Collection<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }

    CriteriaBuilder builder = currentSession().getCriteriaBuilder();
    CriteriaQuery<T> query = builder.createQuery(getResultClass());
    Root<? extends T> root = query.from(getEntityClass());
    query.select(root).where(root.get(idProperty).in(ids));
    return currentSession().createQuery(query).getResultList();
  }

  protected List<T> listByIdList(SingularAttribute<T, Long> idProperty, Collection<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }

    CriteriaBuilder builder = currentSession().getCriteriaBuilder();
    CriteriaQuery<T> query = builder.createQuery(getResultClass());
    Root<? extends T> root = query.from(getEntityClass());
    query.select(root).where(root.get(idProperty).in(ids));
    return currentSession().createQuery(query).getResultList();
  }

  protected <U> long getUsageBy(Class<U> user, SingularAttribute<U, T> property, T value) {
    CriteriaBuilder builder = currentSession().getCriteriaBuilder();
    CriteriaQuery<Long> query = builder.createQuery(Long.class);
    Root<U> root = query.from(user);
    query.select(builder.count(root)).where(builder.equal(root.get(property), value));
    return currentSession().createQuery(query).getSingleResult();
  }

  protected <U> long getUsageBy(Class<U> user, String property, T value) {
    CriteriaBuilder builder = currentSession().getCriteriaBuilder();
    CriteriaQuery<Long> query = builder.createQuery(Long.class);
    Root<U> root = query.from(user);
    query.select(builder.count(root)).where(builder.equal(root.get(property), value));
    return currentSession().createQuery(query).getSingleResult();
  }

  protected <U, V> long getUsageInCollection(Class<U> user, SingularAttribute<U, T> collectionProperty, T value) {
    CriteriaBuilder builder = currentSession().getCriteriaBuilder();
    CriteriaQuery<Long> query = builder.createQuery(Long.class);
    Root<U> root = query.from(user);
    Join<U, T> collection = root.join(collectionProperty);
    query.select(builder.count(root)).where(builder.equal(collection, value));
    return currentSession().createQuery(query).getSingleResult();
  }

  protected <U, V> long getUsageInCollection(Class<U> user, String collectionProperty, T value) {
    CriteriaBuilder builder = currentSession().getCriteriaBuilder();
    CriteriaQuery<Long> query = builder.createQuery(Long.class);
    Root<U> root = query.from(user);
    Join<U, T> collection = root.join(collectionProperty);
    query.select(builder.count(root)).where(builder.equal(collection, value));
    return currentSession().createQuery(query).getSingleResult();
  }

}
