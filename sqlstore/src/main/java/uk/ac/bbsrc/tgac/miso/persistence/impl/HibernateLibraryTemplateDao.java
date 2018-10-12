package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryTemplateStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryTemplateDao implements LibraryTemplateStore, HibernatePaginatedDataSource<LibraryTemplate> {

  protected static final Logger log = LoggerFactory.getLogger(HibernateLibraryTemplateDao.class);

  private static final String[] SEARCH_PROPERTIES = new String[] {};
  private static final List<String> STANDARD_ALIASES = Arrays.asList();


  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public LibraryTemplate get(long id) throws IOException {
    return (LibraryTemplate) currentSession().get(LibraryTemplate.class, id);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public long create(LibraryTemplate libraryTemplate) throws IOException {
    return (Long) currentSession().save(libraryTemplate);
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void update(LibraryTemplate libraryTemplate) throws IOException {
    currentSession().update(libraryTemplate);
  }

  @Override
  public String getFriendlyName() {
    return "Library Template";
  }

  @Override
  public String getProjectColumn() {
    return "project.id";
  }

  @Override
  public Class<? extends LibraryTemplate> getRealClass() {
    return LibraryTemplate.class;
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
  public List<LibraryTemplate> list() throws IOException {
    @SuppressWarnings("unchecked")
    List<LibraryTemplate> list = currentSession().createCriteria(LibraryTemplate.class).list();
    return list;
  }

  @Override
  public List<LibraryTemplate> listLibraryTemplatesForProject(long projectId) {
    @SuppressWarnings("unchecked")
    List<LibraryTemplate> list = currentSession().createCriteria(LibraryTemplate.class)
        .createAlias("projects", "project")
        .add(Restrictions.eq("project.id", projectId))
        .list();
    return list;
  }

  @Override
  public List<LibraryTemplate> getByIdList(List<Long> idList) throws IOException {
    if (idList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(LibraryTemplate.class);
    criteria.add(Restrictions.in("id", idList));
    @SuppressWarnings("unchecked")
    List<LibraryTemplate> records = criteria.list();
    return records;
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    criteria.createAlias("projects", "project");
    criteria.add(Restrictions.eq("project.id", projectId));
  }

}
