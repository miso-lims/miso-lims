package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.store.ArrayRunStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateArrayRunDao implements ArrayRunStore, HibernatePaginatedDataSource<ArrayRun> {

  private static final String[] SEARCH_PROPERTIES = new String[] { "alias", "description" };

  private static final List<String> STANDARD_ALIASES = Arrays.asList("lastModifier", "creator", "instrument", "instrument.platform");

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(ArrayRun arrayRun) throws IOException {
    if (arrayRun.getId() == ArrayRun.UNSAVED_ID) {
      return (long) currentSession().save(arrayRun);
    } else {
      currentSession().update(arrayRun);
      return arrayRun.getId();
    }
  }

  @Override
  public ArrayRun get(long id) throws IOException {
    return (ArrayRun) currentSession().get(ArrayRun.class, id);
  }

  @Override
  public ArrayRun getByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(ArrayRun.class);
    criteria.add(Restrictions.eq("alias", alias));
    return (ArrayRun) criteria.uniqueResult();
  }

  @Override
  public List<ArrayRun> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(ArrayRun.class);
    @SuppressWarnings("unchecked")
    List<ArrayRun> list = criteria.list();
    return list;
  }

  @Override
  public List<ArrayRun> listByArrayId(long arrayId) throws IOException {
    Criteria criteria = currentSession().createCriteria(ArrayRun.class);
    criteria.add(Restrictions.eq("array.id", arrayId));
    @SuppressWarnings("unchecked")
    List<ArrayRun> list = criteria.list();
    return list;
  }

  @Override
  public List<ArrayRun> listBySampleId(long sampleId) throws IOException {
    Criteria criteria = currentSession().createCriteria(ArrayRun.class);
    criteria.createAlias("array.samples", "sample");
    criteria.add(Restrictions.eq("sample.id", sampleId));
    @SuppressWarnings("unchecked")
    List<ArrayRun> list = criteria.list();
    return list;
  }

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(ArrayRun.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  @Override
  public String getFriendlyName() {
    return "ArrayRun";
  }

  @Override
  public Class<? extends ArrayRun> getRealClass() {
    return ArrayRun.class;
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
    if ("platformType".equals(original)) return "platform.platformType";
    return original;
  }

  @Override
  public String propertyForUserName(Criteria criteria, boolean creator) {
    return creator ? "creator.loginName" : "lastModifier.loginName";
  }

  @Override
  public Map<String, Integer> getArrayColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(jdbcTemplate, "ArrayRun");
  }

  @Override
  public String getProjectColumn() {
    return "sample.project.id";
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    criteria.createAlias("array", "array");
    criteria.createAlias("array.samples", "sample");
    HibernatePaginatedDataSource.super.restrictPaginationByProjectId(criteria, projectId, errorHandler);
  }

}
