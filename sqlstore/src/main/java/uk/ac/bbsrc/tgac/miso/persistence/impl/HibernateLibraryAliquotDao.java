package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.TemporalType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryAliquotStore;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryAliquotDao
    implements LibraryAliquotStore, HibernatePaginatedBoxableSource<LibraryAliquot> {

  // Make sure these match the HiberateListLibraryAliquotViewDao
  private final static String[] SEARCH_PROPERTIES = new String[] {"name", "alias", "identificationBarcode"};
  private final static List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("library"));

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(LibraryAliquot aliquot) throws IOException {
    if (aliquot.getId() == LibraryAliquot.UNSAVED_ID) {
      return (long) currentSession().save(aliquot);
    } else {
      currentSession().update(aliquot);
      return aliquot.getId();
    }
  }

  @Override
  public LibraryAliquot get(long id) throws IOException {
    return (LibraryAliquot) currentSession().get(LibraryAliquot.class, id);
  }

  @Override
  public List<LibraryAliquot> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryAliquot.class);
    @SuppressWarnings("unchecked")
    List<LibraryAliquot> records = criteria.list();
    return records;
  }

  @Override
  public List<LibraryAliquot> listByLibraryId(long libraryId) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryAliquot.class);
    criteria.add(Restrictions.eq("library.id", libraryId));
    @SuppressWarnings("unchecked")
    List<LibraryAliquot> records = criteria.list();
    return records;
  }

  @Override
  public LibraryAliquot getByBarcode(String barcode) throws IOException {
    if (barcode == null)
      throw new IOException("Barcode cannot be null!");
    Criteria criteria = currentSession().createCriteria(LibraryAliquot.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    return (LibraryAliquot) criteria.uniqueResult();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public String getProjectColumn() {
    return "project.id";
  }

  @Override
  public Class<? extends LibraryAliquot> getRealClass() {
    return LibraryAliquot.class;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    criteria.createAlias("library.sample", "sample");
    criteria.createAlias("sample.project", "project");
    HibernatePaginatedBoxableSource.super.restrictPaginationByProjectId(criteria, projectId, errorHandler);
  }

  @Override
  public void restrictPaginationByPoolId(Criteria criteria, long poolId, Consumer<String> errorHandler) {
    DetachedCriteria subquery = DetachedCriteria.forClass(PoolImpl.class)
        .createAlias("poolElements", "element")
        .createAlias("element.aliquot", "aliquot")
        .add(Restrictions.eq("id", poolId))
        .setProjection(Projections.property("aliquot.id"));
    criteria.add(Property.forName("id").in(subquery));
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType,
      Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("library.platformType", platformType));
  }

  @Override
  public String propertyForDate(Criteria item, DateType type) {
    switch (type) {
      case CREATE:
        return "creationDate";
      case ENTERED:
        return "creationTime";
      case UPDATE:
        return "lastUpdated";
      default:
        return null;
    }
  }

  @Override
  public TemporalType temporalTypeForDate(DateType type) {
    switch (type) {
      case CREATE:
        return TemporalType.DATE;
      case ENTERED:
      case UPDATE:
        return TemporalType.TIMESTAMP;
      default:
        return null;
    }
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "creator" : "lastModifier";
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String query, Consumer<String> errorHandler) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      criteria.add(Restrictions.isNull("library.index1"));
    } else {
      criteria.createAlias("library.index1", "index1")
          .createAlias("library.index2", "index2", JoinType.LEFT_OUTER_JOIN)
          .add(DbUtils.textRestriction(query, "index1.name", "index1.sequence", "index2.name", "index2.sequence"));
    }
  }

  @Override
  public void restrictPaginationByGroupId(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "groupId"));
  }

  @Override
  public void restrictPaginationByDesign(Criteria criteria, String design, Consumer<String> errorHandler) {
    criteria.createAlias("libraryDesignCode", "libraryDesignCode")
        .add(Restrictions.eq("libraryDesignCode.code", design));
  }

  @Override
  public void restrictPaginationByDistributionRecipient(Criteria criteria, String query,
      Consumer<String> errorHandler) {
    DbUtils.restrictPaginationByDistributionRecipient(criteria, query, "libraryAliquots", "aliquotId");
  }

  @Override
  public String getFriendlyName() {
    return "Library Aliquot";
  }

  @Override
  public List<LibraryAliquot> listByIdList(Collection<Long> idList) throws IOException {
    if (idList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(LibraryAliquot.class);
    criteria.add(Restrictions.in("id", idList));
    @SuppressWarnings("unchecked")
    List<LibraryAliquot> records = criteria.list();
    return records;
  }

  @Override
  public List<LibraryAliquot> listByPoolIds(Collection<Long> poolIds) throws IOException {
    if (poolIds.isEmpty()) {
      return Collections.emptyList();
    }
    DetachedCriteria subquery = DetachedCriteria.forClass(PoolImpl.class)
        .createAlias("poolElements", "element")
        .createAlias("element.aliquot", "aliquot")
        .add(Restrictions.in("id", poolIds))
        .setProjection(Projections.property("aliquot.id"));

    @SuppressWarnings("unchecked")
    List<LibraryAliquot> results = currentSession().createCriteria(LibraryAliquot.class)
        .add(Property.forName("id").in(subquery))
        .list();
    return results;
  }

  @Override
  public long getUsageByPoolOrders(LibraryAliquot aliquot) throws IOException {
    return (long) currentSession().createCriteria(PoolOrder.class)
        .createAlias("orderLibraryAliquots", "item")
        .add(Restrictions.eq("item.aliquot", aliquot))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public long getUsageByChildAliquots(LibraryAliquot aliquot) throws IOException {
    return (long) currentSession().createCriteria(LibraryAliquot.class)
        .createAlias("parentAliquot", "parentAliquot")
        .add(Restrictions.eq("parentAliquot", aliquot))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
