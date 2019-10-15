package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ContainerQC;
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
  public Collection<QcType> list() throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    @SuppressWarnings("unchecked")
    List<QcType> records = criteria.list();
    return records;
  }

  @Override
  public long create(QcType qcType) throws IOException {
    return (Long) currentSession().save(qcType);
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
    Criteria criteria = null;
    switch (qcType.getQcTarget()) {
    case Container:
      criteria = currentSession().createCriteria(ContainerQC.class);
      break;
    case Library:
      criteria = currentSession().createCriteria(LibraryQC.class);
      break;
    case Pool:
      criteria = currentSession().createCriteria(PoolQC.class);
      break;
    case Run:
      throw new IllegalArgumentException("Unhandled QC target: Run");
    case Sample:
      criteria = currentSession().createCriteria(SampleQC.class);
      break;
    default:
      throw new IllegalArgumentException("Unhandled QC target: " + qcType.getQcTarget() == null ? "null" : qcType.getQcTarget().getLabel());
    }
    return (long) criteria.add(Restrictions.eq("type", qcType))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

}
