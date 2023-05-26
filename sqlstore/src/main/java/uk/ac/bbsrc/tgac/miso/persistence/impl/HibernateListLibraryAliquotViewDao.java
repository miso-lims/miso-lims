package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.ListLibraryAliquotViewDao;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateListLibraryAliquotViewDao
    implements ListLibraryAliquotViewDao, HibernatePaginatedDataSource<ListLibraryAliquotView> {

  // Make sure these match the HiberateLibraryAliquotDao
  private static final String[] SEARCH_PROPERTIES = new String[] {"name", "alias", "identificationBarcode"};
  private final static List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(
      new AliasDescriptor("parentLibrary", "library"),
      new AliasDescriptor("library.parentSample", "sample"),
      new AliasDescriptor("sample.parentAttributes", "parentAttributes", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("parentAttributes.tissueAttributes", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("tissueAttributes.tissueOrigin", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("tissueAttributes.tissueType", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("creator"));

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
  public ListLibraryAliquotView get(Long aliquotId) throws IOException {
    return (ListLibraryAliquotView) currentSession().get(ListLibraryAliquotView.class, aliquotId);
  }

  @Override
  public String getProjectColumn() {
    return "project.id";
  }

  @Override
  public Class<? extends ListLibraryAliquotView> getRealClass() {
    return ListLibraryAliquotView.class;
  }

  @Override
  public List<ListLibraryAliquotView> listByIdList(List<Long> aliquotIds) throws IOException {
    if (aliquotIds.size() == 0) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(ListLibraryAliquotView.class);
    criteria.add(Restrictions.in("id", aliquotIds));
    @SuppressWarnings("unchecked")
    List<ListLibraryAliquotView> results = criteria.list();
    return results;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForSortColumn(String original) {
    switch (original) {
      case "lastModified":
        return "lastUpdated";
      case "library.parentSampleId":
        return "sample.id";
      case "library.parentSampleAlias":
        return "sample.alias";
      case "libraryPlatformType":
      case "library.platformType":
        return "library.platformType";
      case "creatorName":
        return "creator.fullName";
      case "creationDate":
        return "created";
      case "effectiveTissueOriginAlias":
        return "tissueOrigin.alias";
      case "effectiveTissueTypeAlias":
        return "tissueType.alias";
      default:
        return original;
    }
  }

  @Override
  public String[] getIdentifierProperties() {
    return SEARCH_PROPERTIES;
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
        return "lastUpdated";
      default:
        return null;
    }
  }

  @Override
  public TemporalType temporalTypeForDate(DateType type) {
    switch (type) {
      case CREATE:
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
  public String getFriendlyName() {
    return "Library Aliquot";
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    criteria.createAlias("parentLibrary", "library")
        .createAlias("library.parentSample", "sample")
        .createAlias("sample.parentProject", "project");
    HibernatePaginatedDataSource.super.restrictPaginationByProjectId(criteria, projectId, errorHandler);
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType,
      Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("library.platformType", platformType));
  }

  @Override
  public void restrictPaginationByPoolId(Criteria criteria, long poolId, Consumer<String> errorHandler) {
    criteria
        .add(Restrictions.sqlRestriction(
            "EXISTS(SELECT * FROM Pool_LibraryAliquot WHERE poolId = ? AND aliquotId = aliquotId)",
            poolId, LongType.INSTANCE));
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
  public void restrictPaginationByBox(Criteria criteria, String query, Consumer<String> errorHandler) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      criteria.createAlias("boxPosition", "boxPosition", JoinType.LEFT_OUTER_JOIN)
          .add(Restrictions.isNull("boxPosition.box"));
    } else {
      criteria.createAlias("boxPosition", "boxPosition")
          .createAlias("boxPosition.box", "box")
          .add(DbUtils.textRestriction(query, HibernatePaginatedBoxableSource.BOX_SEARCH_PROPERTIES));
    }
  }

  @Override
  public void restrictPaginationByFreezer(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.createAlias("boxPosition", "boxPosition")
        .createAlias("boxPosition.box", "box");
    DbUtils.restrictPaginationByFreezer(criteria, query, "box.storageLocation");
  }

  @Override
  public void restrictPaginationByDate(Criteria criteria, Date start, Date end, DateType type,
      Consumer<String> errorHandler) {
    if (type == DateType.RECEIVE) {
      DbUtils.restrictPaginationByReceiptTransferDate(criteria, start, end);
    } else if (type == DateType.DISTRIBUTED) {
      DbUtils.restrictPaginationByDistributionTransferDate(criteria, start, end);
    } else {
      HibernatePaginatedDataSource.super.restrictPaginationByDate(criteria, start, end, type, errorHandler);
    }
  }

  @Override
  public void restrictPaginationByDistributionRecipient(Criteria criteria, String query,
      Consumer<String> errorHandler) {
    DbUtils.restrictPaginationByDistributionRecipient(criteria, query, "libraryAliquots", "aliquotId");
  }

  @Override
  public void restrictPaginationByTissueOrigin(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "tissueOrigin.alias"));
  }

  @Override
  public void restrictPaginationByTissueType(Criteria criteria, String query, Consumer<String> errorHandler) {
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

  @Override
  public void restrictPaginationByBarcode(Criteria criteria, String barcode, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(barcode, "identificationBarcode"));
  }
}
