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

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun_;
import uk.ac.bbsrc.tgac.miso.core.data.Array_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayRunStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateArrayRunDao extends HibernateSaveDao<ArrayRun>
    implements ArrayRunStore, JpaCriteriaPaginatedDataSource<ArrayRun, ArrayRun> {

  public HibernateArrayRunDao() {
    super(ArrayRun.class);
  }

  private static final List<SingularAttribute<ArrayRun, String>> SEARCH_PROPERTIES =
      Arrays.asList(ArrayRun_.alias, ArrayRun_.description);

  @Override
  public ArrayRun getByAlias(String alias) throws IOException {
    return getBy(ArrayRun_.ALIAS, alias);
  }

  @Override
  public List<ArrayRun> listByArrayId(long arrayId) throws IOException {
    QueryBuilder<ArrayRun, ArrayRun> builder = getQueryBuilder();
    Root<ArrayRun> root = builder.getRoot();
    Join<ArrayRun, Array> arrayJoin = builder.getJoin(root, ArrayRun_.array);
    builder.addPredicate(builder.getCriteriaBuilder().equal(arrayJoin.get(Array_.id), arrayId));
    return builder.getResultList();
  }

  @Override
  public List<ArrayRun> listBySampleId(long sampleId) throws IOException {
    QueryBuilder<ArrayRun, ArrayRun> builder = getQueryBuilder();
    Root<ArrayRun> root = builder.getRoot();
    Join<ArrayRun, Array> arrayJoin = builder.getJoin(root, ArrayRun_.array);
    Join<Array, SampleImpl> sampleJoin = builder.getJoin(arrayJoin, Array_.samples);
    builder.addPredicate(builder.getCriteriaBuilder().equal(sampleJoin.get(SampleImpl_.sampleId), sampleId));
    return builder.getResultList();
  }

  @Override
  public String getFriendlyName() {
    return "ArrayRun";
  }

  @Override
  public List<SingularAttribute<ArrayRun, String>> getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public SingularAttribute<ArrayRun, ?> propertyForDate(DateType type) {
    switch (type) {
      case CREATE:
        return ArrayRun_.startDate;
      case ENTERED:
        return ArrayRun_.creationTime;
      case UPDATE:
        return ArrayRun_.lastModified;
      default:
        return null;
    }
  }

  @Override
  public Path<?> propertyForSortColumn(Root<ArrayRun> root, String original) {
    if ("platformType".equals(original))
      return root.get("instrumentModel.platformType");
    if ("status".equals(original))
      return root.get(ArrayRun_.HEALTH);
    return root.get(original);
  }

  @Override
  public SingularAttribute<ArrayRun, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? ArrayRun_.creator : ArrayRun_.lastModifier;
  }

  @Override
  public void restrictPaginationByProjectId(QueryBuilder<?, ArrayRun> builder, long projectId,
      Consumer<String> errorHandler) {
    Join<ArrayRun, Array> arrayJoin = builder.getJoin(builder.getRoot(), ArrayRun_.array);
    Join<Array, SampleImpl> sampleJoin = builder.getJoin(arrayJoin, Array_.samples);
    Join<SampleImpl, ProjectImpl> projectJoin = builder.getJoin(sampleJoin, SampleImpl_.project);
    builder.addPredicate(builder.getCriteriaBuilder().equal(projectJoin.get(ProjectImpl_.id), projectId));
  }

  @Override
  public SingularAttribute<ArrayRun, ?> getIdProperty() {
    return ArrayRun_.id;
  }

  @Override
  public Class<ArrayRun> getEntityClass() {
    return ArrayRun.class;
  }

  @Override
  public Class<ArrayRun> getResultClass() {
    return ArrayRun.class;
  }

}
