package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.List;
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

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListContainerView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.ListContainerViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateListContainerViewDao implements ListContainerViewDao, HibernatePaginatedDataSource<ListContainerView> {

  private static final String[] SEARCH_PROPERTIES = new String[] { "identificationBarcode" };
  private static final List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("model"));

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public String getFriendlyName() {
    return "Container";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends ListContainerView> getRealClass() {
    return ListContainerView.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
    case ENTERED:
      return "created";
    case UPDATE:
      return "lastModified";
    default:
      return null;
    }
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "creator" : "lastModifier";
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("model.platformType", platformType));
  }

  @Override
  public void restrictPaginationByKitName(Criteria criteria, String name, Consumer<String> errorHandler) {
    criteria.createAlias("clusteringKit", "clusteringKit", JoinType.LEFT_OUTER_JOIN);
    criteria.createAlias("multiplexingKit", "multiplexingKit", JoinType.LEFT_OUTER_JOIN);
    criteria.add(Restrictions.disjunction()
        .add(Restrictions.ilike("clusteringKit.name", name, MatchMode.START))
        .add(Restrictions.ilike("multiplexingKit.name", name, MatchMode.START)));
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    criteria.createAlias("partitions", "partitions");
    criteria.createAlias("partitions.pool", "pool");
    criteria.createAlias("pool.poolElements", "poolElement");
    criteria.createAlias("poolElement.poolableElementView", "aliquotForIndex");
    criteria.createAlias("aliquotForIndex.indices", "indices");
    HibernateLibraryDao.restrictPaginationByIndices(criteria, index);
  }

}
