package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControllable;
import uk.ac.bbsrc.tgac.miso.persistence.QcTargetStore;

public abstract class HibernateQcStore<T extends QC> implements QcTargetStore {

  @Autowired
  private SessionFactory sessionFactory;

  private final Class<? extends QualityControllable<T>> entityClass;
  private final Class<T> qcClass;

  public HibernateQcStore(Class<? extends QualityControllable<T>> entityClass, Class<T> qcClass) {
    this.entityClass = entityClass;
    this.qcClass = qcClass;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  protected Session currentSession() {
    return sessionFactory.getCurrentSession();
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
      return (long) currentSession().save(castedQc);
    } else {
      currentSession().update(castedQc);
      return castedQc.getId();
    }
  }

  @Override
  public void deleteControlRun(QcControlRun controlRun) throws IOException {
    currentSession().delete(controlRun);
  }

  @Override
  public long createControlRun(QcControlRun controlRun) throws IOException {
    return (long) currentSession().save(controlRun);
  }

  @Override
  public long updateControlRun(QcControlRun controlRun) throws IOException {
    currentSession().update(controlRun);
    return controlRun.getId();
  }

  @Override
  public List<T> listByIdList(List<Long> ids) throws IOException {
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(qcClass);
    criteria.add(Restrictions.in("id", ids));
    @SuppressWarnings("unchecked")
    List<T> records = criteria.list();
    return records;
  }

}
