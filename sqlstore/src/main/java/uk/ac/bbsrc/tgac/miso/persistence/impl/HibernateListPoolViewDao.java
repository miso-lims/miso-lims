package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Collections;
import java.util.Date;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.ListPoolViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateListPoolViewDao implements ListPoolViewDao, HibernatePaginatedDataSource<ListPoolView> {

  private final static String[] SEARCH_PROPERTIES = new String[] { "name", "alias", "identificationBarcode", "description" };

  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public String getFriendlyName() {
    return "Pool";
  }

  @Override
  public String getProjectColumn() {
    return "element.projectId";
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    criteria.createAlias("elements", "element");
    HibernatePaginatedDataSource.super.restrictPaginationByProjectId(criteria, projectId, errorHandler);
  }

  @Override
  public Class<? extends ListPoolView> getRealClass() {
    return ListPoolView.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return Collections.emptyList();
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
    case CREATE:
      return "creationDate";
    case ENTERED:
      return "creationTime";
    case UPDATE:
      return "lastModified";
    default:
      return null;
    }
  }

  @Override
  public String propertyForSortColumn(String original) {
    if ("creationDate".equals(original)) {
      return "creationTime";
    }
    return original;
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "creator" : "lastModifier";
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("platformType", platformType));
  }

  @Override
  public void restrictPaginationByBox(Criteria criteria, String name, Consumer<String> errorHandler) {
    criteria.add(Restrictions.or(Restrictions.ilike("boxName", name, MatchMode.ANYWHERE),
        Restrictions.ilike("boxAlias", name, MatchMode.ANYWHERE)));
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    criteria.createAlias("elements", "element");
    criteria.createAlias("element.indices", "indices");
    HibernateLibraryDao.restrictPaginationByIndices(criteria, index);
  }

  @Override
  public void restrictPaginationByFreezer(Criteria criteria, String freezer, Consumer<String> errorHandler) {
    criteria.createAlias("box", "box")
        .createAlias("box.storageLocation", "location1")
        .createAlias("location1.parentLocation", "location2", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location2.parentLocation", "location3", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location3.parentLocation", "location4", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location4.parentLocation", "location5", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location5.parentLocation", "location6", JoinType.LEFT_OUTER_JOIN)
        .add(Restrictions.or(
            Restrictions.and(Restrictions.eq("location1.locationUnit", LocationUnit.FREEZER),
                Restrictions.ilike("location1.alias", freezer, MatchMode.START)),
            Restrictions.and(Restrictions.eq("location2.locationUnit", LocationUnit.FREEZER),
                Restrictions.ilike("location2.alias", freezer, MatchMode.START)),
            Restrictions.and(Restrictions.eq("location3.locationUnit", LocationUnit.FREEZER),
                Restrictions.ilike("location3.alias", freezer, MatchMode.START)),
            Restrictions.and(Restrictions.eq("location4.locationUnit", LocationUnit.FREEZER),
                Restrictions.ilike("location4.alias", freezer, MatchMode.START)),
            Restrictions.and(Restrictions.eq("location5.locationUnit", LocationUnit.FREEZER),
                Restrictions.ilike("location5.alias", freezer, MatchMode.START)),
            Restrictions.and(Restrictions.eq("location6.locationUnit", LocationUnit.FREEZER),
                Restrictions.ilike("location6.alias", freezer, MatchMode.START))));
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
  public void restrictPaginationByDistributionRecipient(Criteria criteria, String recipient, Consumer<String> errorHandler) {
    criteria.createAlias("transfers", "transferItem")
        .createAlias("transferItem.transfer", "transfer")
        .add(Restrictions.ilike("transfer.recipient", recipient, MatchMode.ANYWHERE));
  }

}
