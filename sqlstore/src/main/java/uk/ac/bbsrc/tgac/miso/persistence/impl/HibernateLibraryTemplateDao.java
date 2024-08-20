package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryTemplateStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryTemplateDao extends HibernateSaveDao<LibraryTemplate>
    implements LibraryTemplateStore, JpaCriteriaPaginatedDataSource<LibraryTemplate, LibraryTemplate> {

  public HibernateLibraryTemplateDao() {
    super(LibraryTemplate.class);
  }

  @Override
  public String getFriendlyName() {
    return "Library Template";
  }

  @Override
  public SingularAttribute<LibraryTemplate, ?> getIdProperty() {
    return LibraryTemplate_.libraryTemplateId;
  }

  @Override
  public Class<LibraryTemplate> getEntityClass() {
    return LibraryTemplate.class;
  }

  @Override
  public Class<LibraryTemplate> getResultClass() {
    return LibraryTemplate.class;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<LibraryTemplate> root) {
    return Arrays.asList(root.get(LibraryTemplate_.alias));
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, LibraryTemplate> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(LibraryTemplate_.libraryTemplateId);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, LibraryTemplate> builder, DateType type) {
    return null;
  }

  @Override
  public SingularAttribute<LibraryTemplate, ? extends UserImpl> propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public LibraryTemplate getByAlias(String alias) throws IOException {
    return getBy(LibraryTemplate_.ALIAS, alias);
  }

  @Override
  public List<LibraryTemplate> listByProject(long projectId) {
    QueryBuilder<LibraryTemplate, LibraryTemplate> builder = getQueryBuilder();
    Join<LibraryTemplate, ProjectImpl> projectJoin = builder.getJoin(builder.getRoot(), LibraryTemplate_.projects);
    builder.addPredicate(builder.getCriteriaBuilder().equal(projectJoin.get(ProjectImpl_.id), projectId));
    return builder.getResultList();
  }

  @Override
  public List<LibraryTemplate> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(LibraryTemplate_.LIBRARY_TEMPLATE_ID, idList);
  }

  @Override
  public void restrictPaginationByProjectId(QueryBuilder<?, LibraryTemplate> builder, long projectId,
      Consumer<String> errorHandler) {
    Join<LibraryTemplate, ProjectImpl> projectJoin = builder.getJoin(builder.getRoot(), LibraryTemplate_.projects);
    builder.addPredicate(builder.getCriteriaBuilder().equal(projectJoin.get(ProjectImpl_.id), projectId));
  }

}
