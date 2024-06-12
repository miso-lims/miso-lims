package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.persistence.QualityControlTypeStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateQcTypeDao implements QualityControlTypeStore {

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public QcType get(long id) throws IOException {
    return (QcType) currentSession().get(QcType.class, id);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public List<QcType> list() throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    @SuppressWarnings("unchecked")
    List<QcType> records = criteria.list();
    return records;
  }

  @Override
  public List<QcType> listByNameAndTarget(String name, QcTarget target) throws IOException {
    @SuppressWarnings("unchecked")
    List<QcType> records = currentSession().createCriteria(QcType.class)
        .add(Restrictions.eq("name", name))
        .add(Restrictions.eq("qcTarget", target))
        .list();
    return records;
  }

  @Override
  public long create(QcType qcType) throws IOException {
    return (long) currentSession().save(qcType);
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void update(QcType qcType) throws IOException {
    currentSession().update(qcType);
  }

  @Override
  public long getUsage(QcType qcType) throws IOException {
    return (long) getCriteriaForQcTarget(qcType)
        .add(Restrictions.eq("type", qcType))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

  private Criteria getCriteriaForQcTarget(QcType qcType) {
    switch (qcType.getQcTarget()) {
      case Container:
        return currentSession().createCriteria(ContainerQC.class);
      case Library:
        return currentSession().createCriteria(LibraryQC.class);
      case Pool:
        return currentSession().createCriteria(PoolQC.class);
      case Sample:
        return currentSession().createCriteria(SampleQC.class);
      default:
        throw new IllegalArgumentException(
            "Unhandled QC target: " + qcType.getQcTarget() == null ? "null" : qcType.getQcTarget().getLabel());
    }
  }

  @Override
  public QcControl getControl(long id) throws IOException {
    return (QcControl) currentSession().get(QcControl.class, id);
  }

  @Override
  public long createControl(QcControl control) throws IOException {
    return (long) currentSession().save(control);
  }

  @Override
  public void deleteControl(QcControl control) throws IOException {
    currentSession().delete(control);
  }

  @Override
  public long getControlUsage(QcControl control) throws IOException {
    return (long) getCriteriaForTargetQcControlRun(control)
        .add(Restrictions.eq("control", control))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

  @Override
  public long getKitUsage(QcType qcType, KitDescriptor kit) throws IOException {
    return (long) getCriteriaForQcTarget(qcType)
        .add(Restrictions.eq("type", qcType))
        .add(Restrictions.eq("kit", kit))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

  private Criteria getCriteriaForTargetQcControlRun(QcControl control) {
    switch (control.getQcType().getQcTarget()) {
      case Container:
        return currentSession().createCriteria(ContainerQcControlRun.class);
      case Library:
        return currentSession().createCriteria(LibraryQcControlRun.class);
      case Pool:
        return currentSession().createCriteria(PoolQcControlRun.class);
      case Sample:
        return currentSession().createCriteria(SampleQcControlRun.class);
      default:
        throw new IllegalArgumentException(
            "Unhandled QC target: " + control.getQcType().getQcTarget() == null ? "null"
                : control.getQcType().getQcTarget().getLabel());
    }
  }

}
