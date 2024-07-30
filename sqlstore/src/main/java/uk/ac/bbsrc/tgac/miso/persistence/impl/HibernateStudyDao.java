package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment_;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.StudyStore;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateStudyDao extends HibernateSaveDao<Study>
    implements StudyStore, JpaCriteriaPaginatedDataSource<Study, StudyImpl> {

  public HibernateStudyDao() {
    super(Study.class, StudyImpl.class);
  }

  @Override
  public List<Study> listAllWithLimit(long limit) throws IOException {
    if (limit == 0)
      return Collections.emptyList();
    return getQueryBuilder().getResultList((int) limit, 0);
  }

  @Override
  public Study getByAlias(String alias) throws IOException {
    return getBy(StudyImpl_.ALIAS, alias);
  }

  @Override
  public List<Study> listByProjectId(long projectId) throws IOException {
    QueryBuilder<Study, StudyImpl> builder = getQueryBuilder();
    Join<StudyImpl, ProjectImpl> project = builder.getJoin(builder.getRoot(), StudyImpl_.project);
    builder.addPredicate(builder.getCriteriaBuilder().equal(project.get(ProjectImpl_.id), projectId));
    return builder.getResultList();
  }

  @Override
  public String getFriendlyName() {
    return "Study";
  }

  @Override
  public SingularAttribute<StudyImpl, ?> getIdProperty() {
    return StudyImpl_.studyId;
  }

  @Override
  public Class<StudyImpl> getEntityClass() {
    return StudyImpl.class;
  }

  @Override
  public Class<Study> getResultClass() {
    return Study.class;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<StudyImpl> root) {
    return Arrays.asList(root.get(StudyImpl_.name), root.get(StudyImpl_.alias), root.get(StudyImpl_.description));
  }

  @Override
  public Path<?> propertyForDate(Root<StudyImpl> root, DateType type) {
    return null;
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, StudyImpl> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(StudyImpl_.studyId);
      case "studyTypeId":
        return builder.getJoin(builder.getRoot(), StudyImpl_.studyType).get(StudyType_.typeId);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<StudyImpl, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? StudyImpl_.creator : StudyImpl_.lastModifier;
  }

  @Override
  public long getUsage(Study study) throws IOException {
    return getUsageBy(Experiment.class, Experiment_.STUDY, study);
  }

  @Override
  public void restrictPaginationByProjectId(QueryBuilder<?, StudyImpl> builder, long projectId,
      Consumer<String> errorHandler) {
    Join<StudyImpl, ProjectImpl> project = builder.getJoin(builder.getRoot(), StudyImpl_.project);
    builder.addPredicate(builder.getCriteriaBuilder().equal(project.get(ProjectImpl_.id), projectId));
  }

}
