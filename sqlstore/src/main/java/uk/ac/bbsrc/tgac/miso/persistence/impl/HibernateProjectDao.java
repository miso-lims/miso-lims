package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.ProjectStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateProjectDao extends HibernateSaveDao<Project>
    implements ProjectStore, JpaCriteriaPaginatedDataSource<Project, ProjectImpl> {

  public HibernateProjectDao() {
    super(Project.class, ProjectImpl.class);
  }

  @Override
  public Project getByTitle(String title) throws IOException {
    return getBy(ProjectImpl_.TITLE, title);
  }

  @Override
  public Project getByCode(String code) throws IOException {
    return getBy(ProjectImpl_.CODE, code);
  }

  @Override
  public long getUsage(Project project) throws IOException {
    return getUsageInCollection(SampleImpl.class, SampleImpl_.PROJECT, project);
  }

  @Override
  public String getFriendlyName() {
    return "Project";
  }

  @Override
  public SingularAttribute<ProjectImpl, ?> getIdProperty() {
    return ProjectImpl_.id;
  }

  @Override
  public Class<ProjectImpl> getEntityClass() {
    return ProjectImpl.class;
  }

  @Override
  public Class<Project> getResultClass() {
    return Project.class;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<ProjectImpl> root) {
    return Arrays.asList(root.get(ProjectImpl_.name), root.get(ProjectImpl_.title),
        root.get(ProjectImpl_.description), root.get(ProjectImpl_.code));
  }

  @Override
  public Path<?> propertyForDate(Root<ProjectImpl> root, DateType type) {
    switch (type) {
      case CREATE:
      case ENTERED:
        return root.get(ProjectImpl_.creationTime);
      case UPDATE:
        return root.get(ProjectImpl_.lastModified);
      case REB_EXPIRY:
        return root.get(ProjectImpl_.rebExpiry);
      default:
        return null;
    }
  }

  @Override
  public SingularAttribute<ProjectImpl, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? ProjectImpl_.creator : ProjectImpl_.lastModifier;
  }

  @Override
  public void restrictPaginationByPipeline(QueryBuilder<?, ProjectImpl> builder, String query,
      Consumer<String> errorHandler) {
    Join<ProjectImpl, Pipeline> pipeline = builder.getJoin(builder.getRoot(), ProjectImpl_.pipeline);
    builder.addTextRestriction(pipeline.get(Pipeline_.alias), query);
  }

  @Override
  public void restrictPaginationByRebNumber(QueryBuilder<?, ProjectImpl> builder, String query,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(builder.getRoot().get(ProjectImpl_.rebNumber), query);
  }

  @Override
  public void restrictPaginationByStatus(QueryBuilder<?, ProjectImpl> builder, String query,
      Consumer<String> errorHandler) {
    StatusType value = Arrays.stream(StatusType.values())
        .filter(status -> status.getKey().toLowerCase().equals(query.toLowerCase()))
        .findFirst().orElse(null);
    if (value == null) {
      errorHandler.accept("Invalid status: %s".formatted(query));
    } else {
      builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(ProjectImpl_.status), value));
    }
  }

}
