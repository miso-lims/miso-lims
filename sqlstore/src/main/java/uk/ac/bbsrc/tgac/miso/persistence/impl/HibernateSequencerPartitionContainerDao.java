package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencerPartitionContainerDao implements SequencerPartitionContainerStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSequencerPartitionContainerDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  private Criterion searchRestrictions(String querystr) {
    String sanitizedQuery = DbUtils.convertStringToSearchQuery(querystr);
    Criterion criteria = Restrictions.or(Restrictions.eq("spc.platform", PlatformType.get(sanitizedQuery)),
        Restrictions.ilike("spc.identificationBarcode", sanitizedQuery));
    return criteria;
  }

  @Override
  public long save(SequencerPartitionContainer<SequencerPoolPartition> spc) throws IOException {
    long id;
    if (spc.getId() == SequencerPartitionContainerImpl.UNSAVED_ID) {
      id = (Long) currentSession().save(spc);
    } else {
      currentSession().update(spc);
      id = spc.getId();
    }
    return id;
  }

  @SuppressWarnings("unchecked")
  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> get(long id) throws IOException {
    return (SequencerPartitionContainer<SequencerPoolPartition>) currentSession().get(SequencerPartitionContainerImpl.class, id);
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class);
    @SuppressWarnings("unchecked")
    List<SequencerPartitionContainer<SequencerPoolPartition>> results = criteria.list();
    return results;
  }

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(SequencerPartitionContainerImpl.class).setProjection(Projections.rowCount())
        .uniqueResult();
    return (int) c;
  }

  @Override
  public boolean remove(SequencerPartitionContainer<SequencerPoolPartition> spc) throws IOException {
    if (spc.isDeletable()) {
      Long spcId = spc.getId();
      currentSession().delete(spc);

      SequencerPartitionContainer<SequencerPoolPartition> testIfExists = get(spcId);
      return testIfExists == null;
    } else {
      return false;
    }
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainerByPartitionId(long partitionId)
      throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-partition relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class, "spc");
    criteria.createAlias("spc.partitions", "ps");
    criteria.add(Restrictions.eq("ps.id", partitionId));
    @SuppressWarnings("unchecked")
    SequencerPartitionContainer<SequencerPoolPartition> record = (SequencerPartitionContainer<SequencerPoolPartition>) criteria
        .uniqueResult();
    return record;
  }

  @Override
  public List<SequencerPartitionContainer<SequencerPoolPartition>> listAllSequencerPartitionContainersByRunId(long runId)
      throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-partition relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class);
    criteria.createAlias("runs", "run");
    criteria.add(Restrictions.eq("run.id", runId));
    @SuppressWarnings("unchecked")
    List<SequencerPartitionContainer<SequencerPoolPartition>> records = criteria.list();
    return records;
  }

  @Override
  public List<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException {
    Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    @SuppressWarnings("unchecked")
    List<SequencerPartitionContainer<SequencerPoolPartition>> records = criteria.list();
    return records;
  }

  @Override
  public Collection<? extends SequencerPoolPartition> listPartitionsByContainerId(long sequencerPartitionContainerId) throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-partition relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(PartitionImpl.class);
    criteria.add(Restrictions.eq("sequencerPartitionContainer.id", sequencerPartitionContainerId));
    @SuppressWarnings("unchecked")
    List<? extends SequencerPoolPartition> records = criteria.list();
    return records;
  }

  @Override
  public long countBySearch(String querystr) throws IOException {
    if (isStringEmptyOrNull(querystr)) {
      return count();
    } else {
      Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class, "spc");
      criteria.add(searchRestrictions(querystr));
      return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }
  }

  @Override
  public List<SequencerPartitionContainer<SequencerPoolPartition>> listBySearchOffsetAndNumResults(int offset, int limit, String querystr,
      String sortDir, String sortCol) throws IOException {
    if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must not be less than zero");
    if ("lastModified".equals(sortCol)) sortCol = "derivedInfo.lastModified";
    Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class, "spc");
    criteria.add(searchRestrictions(querystr));
    // required to sort by 'derivedInfo.lastModifier'
    criteria.createAlias("derivedInfo", "derivedInfo");
    criteria.setFirstResult(offset);
    criteria.setMaxResults(limit);
    criteria.addOrder("asc".equalsIgnoreCase(sortDir) ? Order.asc(sortCol) : Order.desc(sortCol));
    criteria.setProjection(Projections.property("id"));
    @SuppressWarnings("unchecked")
    List<Long> ids = criteria.list();
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    // We do this in two steps to make a smaller query that that the database can optimise
    Criteria query = currentSession().createCriteria(SequencerPartitionContainerImpl.class);
    query.add(Restrictions.in("id", ids));
    query.addOrder("asc".equalsIgnoreCase(sortDir) ? Order.asc(sortCol) : Order.desc(sortCol));
    query.createAlias("derivedInfo", "derivedInfo");
    @SuppressWarnings("unchecked")
    List<SequencerPartitionContainer<SequencerPoolPartition>> records = query.list();
    return records;
  }

  @Override
  public List<SequencerPartitionContainer<SequencerPoolPartition>> listByOffsetAndNumResults(int offset, int limit, String sortDir,
      String sortCol) throws IOException {
    if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must not be less than zero");
    if ("lastModified".equals(sortCol)) sortCol = "derivedInfo.lastModified";
    Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class);
    // I don't know why this alias is required, but without it, you can't sort by 'derivedInfo.lastModifier', which is the field on which we
    // want to sort most List X pages
    criteria.createAlias("derivedInfo", "derivedInfo");
    criteria.setFirstResult(offset);
    criteria.setMaxResults(limit);
    criteria.addOrder("asc".equalsIgnoreCase(sortDir.toLowerCase()) ? Order.asc(sortCol) : Order.desc(sortCol));
    @SuppressWarnings("unchecked")
    List<SequencerPartitionContainer<SequencerPoolPartition>> records = criteria.list();
    return records;
  }

  @Override
  public SequencerPoolPartition getPartitionById(long partitionId) {
    return (SequencerPoolPartition) currentSession().get(PartitionImpl.class, partitionId);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
