package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate_;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryTemplateStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryTemplateDao extends HibernateSaveDao<LibraryTemplate>
    implements LibraryTemplateStore, HibernatePaginatedDataSource<LibraryTemplate> {

  private static final String[] SEARCH_PROPERTIES = new String[] {"alias"};

  public HibernateLibraryTemplateDao() {
    super(LibraryTemplate.class);
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
  public Iterable<AliasDescriptor> listAliases() {
    return Collections.emptyList();
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
  public String propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public LibraryTemplate getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public List<LibraryTemplate> listByProject(long projectId) {
    @SuppressWarnings("unchecked")
    List<LibraryTemplate> list = currentSession().createCriteria(LibraryTemplate.class)
        .createAlias("projects", "project")
        .add(Restrictions.eq("project.id", projectId))
        .list();
    return list;
  }

  @Override
  public List<LibraryTemplate> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(LibraryTemplate_.LIBRARY_TEMPLATE_ID, idList);
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    criteria.createAlias("projects", "project");
    criteria.add(Restrictions.eq("project.id", projectId));
  }

}
