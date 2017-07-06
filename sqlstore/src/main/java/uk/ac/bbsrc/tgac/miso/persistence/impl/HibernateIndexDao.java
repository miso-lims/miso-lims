package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.IndexStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateIndexDao implements IndexStore, HibernatePaginatedDataSource<Index> {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSubprojectDao.class);

  private static final String[] SEARCH_PROPERTIES = new String[] { "name", "sequence", "family.name" };

  private static final List<String> STANDARD_ALIASES = Arrays.asList("family");

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public String getFriendlyName() {
    return "Index";
  }

  @Override
  public Index getIndexById(long id) {
    Query query = currentSession().createQuery("from Index where id = :id");
    query.setLong("id", id);
    return (Index) query.uniqueResult();
  }

  @Override
  public List<IndexFamily> getIndexFamilies() {
    Query query = currentSession().createQuery("from IndexFamily");
    @SuppressWarnings("unchecked")
    List<IndexFamily> list = query.list();
    return list;
  }

  @Override
  public List<IndexFamily> getIndexFamiliesByPlatform(PlatformType platformType) {
    Query query = currentSession().createQuery("from IndexFamily where platformType = :platform");
    query.setParameter("platform", platformType);
    @SuppressWarnings("unchecked")
    List<IndexFamily> list = query.list();
    return list;
  }

  @Override
  public IndexFamily getIndexFamilyByName(String name) {
    Query query = currentSession().createQuery("from IndexFamily where name = :name");
    query.setString("name", name);
    return (IndexFamily) query.uniqueResult();
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends Index> getRealClass() {
    return Index.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public Iterable<String> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    return null;
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public String propertyForUserName(Criteria criteria, boolean creator) {
    return null;
  }

  @Override
  public void restrictPaginationByArchived(Criteria criteria, boolean isArchived, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("family.archived", isArchived));
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("family.platformType", platformType));
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
