package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListWorksetView;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.ListWorksetViewStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateListWorksetViewDao implements ListWorksetViewStore, HibernatePaginatedDataSource<ListWorksetView> {

  private static final String[] SEARCH_PROPERTIES = new String[] { "alias", "description" };

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
    return "Workset";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends ListWorksetView> getRealClass() {
    return ListWorksetView.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return Collections.emptySet();
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
  public List<ListWorksetView> listBySearch(String query) throws IOException {
    if (query == null) {
      throw new NullPointerException("No query string provided");
    }
    @SuppressWarnings("unchecked")
    List<ListWorksetView> results = currentSession().createCriteria(ListWorksetView.class)
        .add(Restrictions.ilike("alias", query, MatchMode.START))
        .list();
    return results;
  }

}
