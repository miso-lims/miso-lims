package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.store.WorksetStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateWorksetDao implements WorksetStore, HibernatePaginatedDataSource<Workset> {

  private static final String[] SEARCH_PROPERTIES = new String[] { "alias", "description" };
  private static final List<String> STANDARD_ALIASES = Collections.emptyList();

  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public Workset get(long id) {
    return (Workset) currentSession().get(Workset.class, id);
  }

  @Override
  public Workset getByAlias(String alias) {
    return (Workset) currentSession().createCriteria(Workset.class)
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

  @Override
  public List<Workset> listBySearch(String query) {
    if (query == null) {
      throw new NullPointerException("No query string provided");
    }
    @SuppressWarnings("unchecked")
    List<Workset> results = currentSession().createCriteria(Workset.class)
        .add(Restrictions.ilike("alias", query, MatchMode.START))
        .list();
    return results;
  }

  @Override
  public List<Workset> listBySample(long sampleId) {
    @SuppressWarnings("unchecked")
    List<Workset> results = currentSession().createCriteria(Workset.class)
        .createAlias("samples", "sample")
        .add(Restrictions.eq("sample.id", sampleId))
        .list();
    return results;
  }

  @Override
  public List<Workset> listByLibrary(long libraryId) {
    @SuppressWarnings("unchecked")
    List<Workset> results = currentSession().createCriteria(Workset.class)
        .createAlias("libraries", "library")
        .add(Restrictions.eq("library.id", libraryId))
        .list();
    return results;
  }

  @Override
  public List<Workset> listByDilution(long dilutionId) {
    @SuppressWarnings("unchecked")
    List<Workset> results = currentSession().createCriteria(Workset.class)
        .createAlias("dilutions", "dilution")
        .add(Restrictions.eq("dilution.id", dilutionId))
        .list();
    return results;
  }

  @Override
  public long save(Workset workset) {
    if (!workset.isSaved()) {
      return (long) currentSession().save(workset);
    } else {
      currentSession().update(workset);
      return workset.getId();
    }
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
  public Class<? extends Workset> getRealClass() {
    return Workset.class;
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
  public Map<String, Integer> getColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(jdbcTemplate, "Workset");
  }

}
