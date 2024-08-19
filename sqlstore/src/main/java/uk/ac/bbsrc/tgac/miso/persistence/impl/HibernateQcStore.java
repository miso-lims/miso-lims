package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControllable;
import uk.ac.bbsrc.tgac.miso.persistence.QcTargetStore;

public abstract class HibernateQcStore<T extends QC> implements QcTargetStore {

  private final Class<? extends QualityControllable<T>> entityClass;
  private final Class<T> qcClass;

  public HibernateQcStore(Class<? extends QualityControllable<T>> entityClass, Class<T> qcClass) {
    this.entityClass = entityClass;
    this.qcClass = qcClass;
  }

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public T get(long id) throws IOException {
    return qcClass.cast(currentSession().get(qcClass, id));
  }

  @Override
  public QualityControlEntity getEntity(long id) throws IOException {
    return entityClass.cast(currentSession().get(entityClass, id));
  }

  @Override
  public Collection<T> listForEntity(long id) throws IOException {
    return entityClass.cast(currentSession().get(entityClass, id)).getQCs();
  }

  @Override
  public long save(QC qc) throws IOException {
    T castedQc = qcClass.cast(qc);
    if (!qc.isSaved()) {
      currentSession().persist(castedQc);
      return qc.getId();
    } else {
      return currentSession().merge(castedQc).getId();
    }
  }

  @Override
  public void deleteControlRun(QcControlRun controlRun) throws IOException {
    currentSession().remove(controlRun);
  }

  @Override
  public long createControlRun(QcControlRun controlRun) throws IOException {
    currentSession().persist(controlRun);
    return controlRun.getId();
  }

  @Override
  public long updateControlRun(QcControlRun controlRun) throws IOException {
    currentSession().merge(controlRun);
    return controlRun.getId();
  }

  @Override
  public List<T> listByIdList(List<Long> ids) throws IOException {
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }

    QueryBuilder<? extends QualityControllable<T>, ? extends QualityControllable<T>> builder =
        new QueryBuilder<>(currentSession(), entityClass, entityClass);
    In<Long> inClause = builder.getCriteriaBuilder().in(builder.getRoot().get(getIdProperty()));
    for (Long id : ids) {
      inClause.value(id);
    }
    builder.addPredicate(inClause);

    List<T> results = new ArrayList<>();
    builder.getResultList().forEach(result -> results.addAll(entityClass.cast(result).getQCs()));
    return results;
  }

  public abstract String getIdProperty();

}
