package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.ListPoolViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateListPoolViewDao implements ListPoolViewDao, HibernatePaginatedDataSource<ListPoolView> {

  private final static String[] SEARCH_PROPERTIES = new String[] { "name", "alias", "identificationBarcode", "description" };
  private static final List<String> STANDARD_ALIASES = Arrays.asList("lastModifier", "creator");

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
  public Iterable<String> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
    case CREATE:
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
  public String propertyForUserName(Criteria criteria, boolean creator) {
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

}
