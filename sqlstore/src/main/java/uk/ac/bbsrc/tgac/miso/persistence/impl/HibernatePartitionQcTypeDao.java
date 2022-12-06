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
public class HibernatePartitionQcTypeDao extends HibernateSaveDao<PartitionQCType> implements PartitionQcTypeDao {

  public HibernatePartitionQcTypeDao() {
    super(PartitionQCType.class);
  }

  @Override
  public PartitionQCType getByDescription(String description) throws IOException {
    return (PartitionQCType) currentSession().createCriteria(PartitionQCType.class)
        .add(Restrictions.eq("description", description))
        .uniqueResult();
  }

  @Override
  public List<PartitionQCType> listByIdList(List<Long> idList) throws IOException {
    return listByIdList("partitionQcTypeId", idList);
  }

  @Override
  public long getUsage(PartitionQCType type) throws IOException {
    return (long) currentSession().createCriteria(RunPartition.class)
        .add(Restrictions.eq("qcType", type))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
