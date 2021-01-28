package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.TextQuery;
import uk.ac.bbsrc.tgac.miso.persistence.PoolableElementViewDao;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolableElementViewDao implements PoolableElementViewDao, HibernatePaginatedDataSource<PoolableElementView> {

  // Make sure these match the HiberateLibraryAliquotDao
  private static final String[] SEARCH_PROPERTIES = new String[] { "aliquotName", "aliquotAlias", "aliquotBarcode" };
  private final static List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("sample"),
      new AliasDescriptor("sample.parentAttributes", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("parentAttributes.tissueAttributes", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("tissueAttributes.tissueOrigin", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("tissueAttributes.tissueType", JoinType.LEFT_OUTER_JOIN));

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public PoolableElementView get(Long aliquotId) throws IOException {
    return (PoolableElementView) currentSession().get(PoolableElementView.class, aliquotId);
  }

  @Override
  public PoolableElementView getByBarcode(String barcode) throws IOException {
    if (barcode == null) throw new IOException("Barcode cannot be null!");
    Criteria criteria = currentSession().createCriteria(PoolableElementView.class);
    criteria.add(Restrictions.eq("aliquotBarcode", barcode));
    return (PoolableElementView) criteria.uniqueResult();
  }

  @Override
  public PoolableElementView getByPreMigrationId(Long preMigrationId) throws IOException {
    if (preMigrationId == null) throw new NullPointerException("preMigrationId cannot be null");
    Criteria criteria = currentSession().createCriteria(PoolableElementView.class);
    criteria.add(Restrictions.eq("preMigrationId", preMigrationId));
    return (PoolableElementView) criteria.uniqueResult();
  }

  @Override
  public String getProjectColumn() {
    return "projectId";
  }

  @Override
  public Class<? extends PoolableElementView> getRealClass() {
    return PoolableElementView.class;
  }

  @Override
  public List<PoolableElementView> list(List<Long> aliquotIds) throws IOException {
    if (aliquotIds.size() == 0) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(PoolableElementView.class);
    criteria.add(Restrictions.in("id", aliquotIds));
    @SuppressWarnings("unchecked")
    List<PoolableElementView> results = criteria.list();
    return results;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForSortColumn(String original) {
    switch (original) {
    case "id":
      return "aliquotId";
    case "name":
      return "aliquotName";
    case "alias":
      return "aliquotAlias";
    case "volume":
      return "aliquotVolume";
    case "identificationBarcode":
      return "aliquotBarcode";
    case "qcPassed":
      return "aliquotQcPassed";
    case "library.id":
      return "libraryId";
    case "library.alias":
      return "libraryAlias";
    case "library.parentSampleId":
      return "sampleId";
    case "library.parentSampleAlias":
      return "sampleAlias";
    case "libraryPlatformType":
      return "platformType";
    case "creatorName":
      return "creator.fullName";
    case "creationDate":
      return "created";
    case "library.platformType":
      return "platformType";
    case "concentration":
      return "aliquotConcentration";
    case "concentrationUnits":
      return "aliquotConcentrationUnits";
    case "ngUsed":
      return "aliquotNgUsed";
    case "volumeUsed":
      return "aliquotVolumeUsed";
    case "dnaSize":
      return "aliquotDnaSize";
    case "effectiveTissueOriginLabel":
      return "tissueOrigin.alias";
    case "effectiveTissueTypeLabel":
      return "tissueType.alias";
    default:
      return original;
    }
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("platformType", platformType));
  }

  @Override
  public void restrictPaginationByPoolId(Criteria criteria, long poolId, Consumer<String> errorHandler) {
    criteria
        .add(Restrictions.sqlRestriction("EXISTS(SELECT * FROM Pool_LibraryAliquot WHERE poolId = ? AND aliquotId = aliquotId)",
            poolId, LongType.INSTANCE));
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public String propertyForDate(Criteria item, DateType type) {
    switch (type) {
    case CREATE:
      return "created";
    case UPDATE:
      return "lastModified";
    default:
      return null;
    }
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "creator" : "lastModifier";
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, TextQuery query, Consumer<String> errorHandler) {
    if (query.getText() == null) {
      criteria.add(Restrictions.isEmpty("indices"));
    } else {
      criteria.createAlias("indices", "indices", JoinType.LEFT_OUTER_JOIN)
          .add(DbUtils.textRestriction(query, "indices.name", "indices.sequence"));
    }
  }

  @Override
  public void restrictPaginationByBox(Criteria criteria, TextQuery query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "boxAlias", "boxName", "boxIdentificationBarcode", "boxLocationBarcode"));
  }

  @Override
  public String getFriendlyName() {
    return "Library Aliquot";
  }

  @Override
  public void restrictPaginationByFreezer(Criteria criteria, TextQuery query, Consumer<String> errorHandler) {
    criteria.createAlias("aliquot", "aliquot")
        .createAlias("aliquot.boxPosition", "boxPosition")
        .createAlias("boxPosition.box", "box");
    DbUtils.restrictPaginationByFreezer(criteria, query, "box.storageLocation");
  }

  @Override
  public void restrictPaginationByDate(Criteria criteria, Date start, Date end, DateType type, Consumer<String> errorHandler) {
    if (type == DateType.RECEIVE) {
      criteria.createAlias("transfers", "transferItem")
          .createAlias("transferItem.transfer", "transfer")
          .add(Restrictions.isNotNull("transfer.senderLab"))
          .add(Restrictions.between("transfer.transferTime", start, end));
    } else if (type == DateType.DISTRIBUTED) {
      criteria.createAlias("transfers", "transferItem")
          .createAlias("transferItem.transfer", "transfer")
          .add(Restrictions.isNotNull("transfer.recipient"))
          .add(Restrictions.between("transfer.transferTime", start, end));
    } else {
      HibernatePaginatedDataSource.super.restrictPaginationByDate(criteria, start, end, type, errorHandler);
    }
  }

  @Override
  public void restrictPaginationByDistributed(Criteria criteria, Consumer<String> errorHandler) {
    criteria.createAlias("transfers", "transferItem")
        .createAlias("transferItem.transfer", "transfer")
        .add(Restrictions.isNotNull("transfer.recipient"));
  }

  @Override
  public void restrictPaginationByDistributionRecipient(Criteria criteria, TextQuery query, Consumer<String> errorHandler) {
    criteria.createAlias("transfers", "transferItem")
        .createAlias("transferItem.transfer", "transfer")
        .add(DbUtils.textRestriction(query, "transfer.recipient"));
  }

  @Override
  public void restrictPaginationByTissueOrigin(Criteria criteria, TextQuery query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "tissueOrigin.alias"));
  }

  @Override
  public void restrictPaginationByTissueType(Criteria criteria, TextQuery query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "tissueType.alias"));
  }

  @Override
  public void restrictPaginationByWorksetId(Criteria criteria, long worksetId, Consumer<String> errorHandler) {
    DetachedCriteria subquery = DetachedCriteria.forClass(Workset.class)
        .createAlias("worksetLibraryAliquots", "worksetLibraryAliquot")
        .createAlias("worksetLibraryAliquot.item", "libraryaliquot")
        .add(Restrictions.eq("id", worksetId))
        .setProjection(Projections.property("libraryaliquot.id"));
    criteria.add(Property.forName("id").in(subquery));
  }
}
