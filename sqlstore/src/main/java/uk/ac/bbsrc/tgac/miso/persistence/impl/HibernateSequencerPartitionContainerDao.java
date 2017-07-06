package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencerPartitionContainerDao
    implements SequencerPartitionContainerStore, HibernatePaginatedDataSource<SequencerPartitionContainer> {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSequencerPartitionContainerDao.class);

  private final static String[] SEARCH_PROPERTIES = new String[] { "identificationBarcode" };
  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public SequencerPartitionContainer save(SequencerPartitionContainer spc) throws IOException {
    if (spc.getId() == SequencerPartitionContainerImpl.UNSAVED_ID) {
      currentSession().save(spc);
    } else {
      currentSession().update(spc);
    }
    return spc;
  }

  @Override
  public SequencerPartitionContainer get(long id) throws IOException {
    return (SequencerPartitionContainer) currentSession().get(SequencerPartitionContainerImpl.class, id);
  }

  @Override
  public List<SequencerPartitionContainer> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class);
    @SuppressWarnings("unchecked")
    List<SequencerPartitionContainer> results = criteria.list();
    return results;
  }

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(SequencerPartitionContainerImpl.class).setProjection(Projections.rowCount())
        .uniqueResult();
    return (int) c;
  }

  @Override
  public boolean remove(SequencerPartitionContainer spc) throws IOException {
    if (spc.isDeletable()) {
      Long spcId = spc.getId();
      currentSession().delete(spc);

      SequencerPartitionContainer testIfExists = get(spcId);
      return testIfExists == null;
    } else {
      return false;
    }
  }

  @Override
  public SequencerPartitionContainer getSequencerPartitionContainerByPartitionId(long partitionId)
      throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-partition relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class, "spc");
    criteria.createAlias("spc.partitions", "ps");
    criteria.add(Restrictions.eq("ps.id", partitionId));
    SequencerPartitionContainer record = (SequencerPartitionContainer) criteria
        .uniqueResult();
    return record;
  }

  @Override
  public List<SequencerPartitionContainer> listAllSequencerPartitionContainersByRunId(long runId)
      throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class);
    criteria.createAlias("runs", "run");
    criteria.add(Restrictions.eq("run.id", runId));
    @SuppressWarnings("unchecked")
    List<SequencerPartitionContainer> records = criteria.list();
    return records;
  }

  @Override
  public List<SequencerPartitionContainer> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException {
    Criteria criteria = currentSession().createCriteria(SequencerPartitionContainerImpl.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    @SuppressWarnings("unchecked")
    List<SequencerPartitionContainer> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Partition> listPartitionsByContainerId(long sequencerPartitionContainerId) throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-partition relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(PartitionImpl.class);
    criteria.add(Restrictions.eq("sequencerPartitionContainer.id", sequencerPartitionContainerId));
    @SuppressWarnings("unchecked")
    List<Partition> records = criteria.list();
    return records;
  }

  @Override
  public Partition getPartitionById(long partitionId) {
    return (Partition) currentSession().get(PartitionImpl.class, partitionId);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private final static List<String> STANDARD_ALIASES = Arrays.asList("lastModifier", "creator", "platform");

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<String> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
    case CREATE:
      return "creationDate";
    case UPDATE:
      return "lastModified";
    default:
      return null;
    }
  }

  @Override
  public String propertyForUserName(Criteria criteria, boolean creator) {
    return creator ? "creator.loginName" : "lastModifier.loginName";
  }

  @Override
  public Class<? extends SequencerPartitionContainer> getRealClass() {
    return SequencerPartitionContainerImpl.class;
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("platform.platformType", platformType));
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    criteria.createAlias("partitions", "partitions");
    criteria.createAlias("partitions.pool", "pool");
    criteria.createAlias("pool.pooledElementViews", "dilutionForIndex");
    criteria.createAlias("dilutionForIndex.indices", "indices");
    HibernateLibraryDao.restrictPaginationByIndices(criteria, index);
  }

  @Override
  public String getFriendlyName() {
    return "Container";
  }
}
