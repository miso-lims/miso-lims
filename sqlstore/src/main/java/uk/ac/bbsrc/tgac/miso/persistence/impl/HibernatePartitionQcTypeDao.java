package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.persistence.PartitionQcTypeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernatePartitionQcTypeDao implements PartitionQcTypeDao {

  @Autowired
  private SessionFactory sessionFactory;

  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public PartitionQCType get(long id) throws IOException {
    return (PartitionQCType) currentSession().get(PartitionQCType.class, id);
  }

  @Override
  public PartitionQCType getByDescription(String description) throws IOException {
    return (PartitionQCType) currentSession().createCriteria(PartitionQCType.class)
        .add(Restrictions.eq("description", description))
        .uniqueResult();
  }

  @Override
  public List<PartitionQCType> list() throws IOException {
    @SuppressWarnings("unchecked")
    List<PartitionQCType> results = currentSession().createCriteria(PartitionQCType.class).list();
    return results;
  }

  @Override
  public long create(PartitionQCType type) throws IOException {
    return (long) currentSession().save(type);
  }

  @Override
  public long update(PartitionQCType type) throws IOException {
    currentSession().update(type);
    return type.getId();
  }

  @Override
  public long getUsage(PartitionQCType type) throws IOException {
    return (long) currentSession().createCriteria(RunPartition.class)
        .add(Restrictions.eq("qcType", type))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
