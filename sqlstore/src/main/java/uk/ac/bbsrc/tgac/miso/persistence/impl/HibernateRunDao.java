package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Index_;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel_;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Run_;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentProject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentProject_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentSample_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement_;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.RunStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRunDao extends HibernateSaveDao<Run>
    implements RunStore, JpaCriteriaPaginatedDataSource<Run, Run> {

  public HibernateRunDao() {
    super(Run.class);
  }

  @Override
  public Run getLatestStartDateRunBySequencerPartitionContainerId(long containerId) throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships,
    // unexpected associations may show up
    currentSession().flush();

    QueryBuilder<Run, Run> builder = getQueryBuilder();
    Join<Run, RunPosition> position = builder.getJoin(builder.getRoot(), Run_.runPositions);
    Join<RunPosition, SequencerPartitionContainerImpl> container = builder.getJoin(position, RunPosition_.container);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(container.get(SequencerPartitionContainerImpl_.containerId), containerId));
    builder.addSort(builder.getRoot().get(Run_.startDate), false);
    List<Run> results = builder.getResultList(1, 0);
    return results.isEmpty() ? null : results.get(0);
  }

  @Override
  public Run getLatestRunIdRunBySequencerPartitionContainerId(long containerId) throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships,
    // unexpected associations may show up
    currentSession().flush();

    QueryBuilder<Run, Run> builder = getQueryBuilder();
    Join<Run, RunPosition> position = builder.getJoin(builder.getRoot(), Run_.runPositions);
    Join<RunPosition, SequencerPartitionContainerImpl> container = builder.getJoin(position, RunPosition_.container);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(container.get(SequencerPartitionContainerImpl_.containerId), containerId));
    builder.addSort(builder.getRoot().get(Run_.runId), false);
    List<Run> results = builder.getResultList(1, 0);
    return results.isEmpty() ? null : results.get(0);
  }

  @Override
  public Run getByAlias(String alias) throws IOException {
    return getBy(Run_.ALIAS, alias);
  }

  @Override
  public List<Run> listByPoolId(long poolId) throws IOException {
    QueryBuilder<Long, Run> builder = new QueryBuilder<>(currentSession(), Run.class, Long.class);
    Join<Run, RunPosition> position = builder.getJoin(builder.getRoot(), Run_.runPositions);
    Join<RunPosition, SequencerPartitionContainerImpl> container = builder.getJoin(position, RunPosition_.container);
    Join<SequencerPartitionContainerImpl, PartitionImpl> partition =
        builder.getJoin(container, SequencerPartitionContainerImpl_.partitions);
    Join<PartitionImpl, PoolImpl> pool = builder.getJoin(partition, PartitionImpl_.pool);
    builder.addPredicate(builder.getCriteriaBuilder().equal(pool.get(PoolImpl_.poolId), poolId));
    builder.setColumn(builder.getRoot().get(Run_.runId));
    List<Long> ids = builder.getResultList();
    return listByIdList(ids);
  }

  @Override
  public List<Run> listByLibraryAliquotId(long libraryAliquotId) throws IOException {
    QueryBuilder<Run, Run> builder = getQueryBuilder();
    Join<Run, RunPosition> position = builder.getJoin(builder.getRoot(), Run_.runPositions);
    Join<RunPosition, SequencerPartitionContainerImpl> container = builder.getJoin(position, RunPosition_.container);
    Join<SequencerPartitionContainerImpl, PartitionImpl> partition =
        builder.getJoin(container, SequencerPartitionContainerImpl_.partitions);
    Join<PartitionImpl, PoolImpl> pool = builder.getJoin(partition, PartitionImpl_.pool);
    Join<PoolImpl, PoolElement> element = builder.getJoin(pool, PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquot = builder.getJoin(element, PoolElement_.aliquot);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(aliquot.get(ListLibraryAliquotView_.aliquotId), libraryAliquotId));
    return builder.getResultList();
  }

  @Override
  public List<Run> listByLibraryIdList(Collection<Long> libraryIds) throws IOException {
    if (libraryIds == null || libraryIds.isEmpty()) {
      return Collections.emptyList();
    }
    QueryBuilder<Long, RunPosition> idBuilder = new QueryBuilder<>(currentSession(), RunPosition.class, Long.class);
    Join<RunPosition, SequencerPartitionContainerImpl> container =
        idBuilder.getJoin(idBuilder.getRoot(), RunPosition_.container);
    Join<SequencerPartitionContainerImpl, PartitionImpl> part =
        idBuilder.getJoin(container, SequencerPartitionContainerImpl_.partitions);
    Join<PartitionImpl, PoolImpl> pool = idBuilder.getJoin(part, PartitionImpl_.pool);
    Join<PoolImpl, PoolElement> element = idBuilder.getJoin(pool, PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquot = idBuilder.getJoin(element, PoolElement_.aliquot);
    Join<ListLibraryAliquotView, ParentLibrary> library =
        idBuilder.getJoin(aliquot, ListLibraryAliquotView_.parentLibrary);
    idBuilder.addInPredicate(library.get(ParentLibrary_.libraryId), libraryIds);
    Join<RunPosition, Run> run = idBuilder.getJoin(idBuilder.getRoot(), RunPosition_.run);
    idBuilder.setColumn(run.get(Run_.runId));
    List<Long> ids = idBuilder.getResultList();

    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }

    QueryBuilder<Run, Run> builder = getQueryBuilder();
    builder.addInPredicate(builder.getRoot().get(Run_.runId), ids);
    return builder.getResultList();
  }

  @Override
  public List<Run> listBySequencerPartitionContainerId(long containerId) throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships,
    // unexpected associations may show up
    currentSession().flush();

    QueryBuilder<Run, Run> builder = new QueryBuilder<>(currentSession(), Run.class, Run.class);
    Join<Run, RunPosition> position = builder.getJoin(builder.getRoot(), Run_.runPositions);
    Join<RunPosition, SequencerPartitionContainerImpl> container = builder.getJoin(position, RunPosition_.container);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(container.get(SequencerPartitionContainerImpl_.containerId), containerId));
    return builder.getResultList();
  }

  @Override
  public List<Run> listByProjectId(long projectId) throws IOException {
    QueryBuilder<Long, Run> idBuilder = new QueryBuilder<>(currentSession(), Run.class, Long.class);
    Join<Run, RunPosition> position = idBuilder.getJoin(idBuilder.getRoot(), Run_.runPositions);
    Join<RunPosition, SequencerPartitionContainerImpl> container = idBuilder.getJoin(position, RunPosition_.container);
    Join<SequencerPartitionContainerImpl, PartitionImpl> partition =
        idBuilder.getJoin(container, SequencerPartitionContainerImpl_.partitions);
    Join<PartitionImpl, PoolImpl> pool = idBuilder.getJoin(partition, PartitionImpl_.pool);
    Join<PoolImpl, PoolElement> element = idBuilder.getJoin(pool, PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquot = idBuilder.getJoin(element, PoolElement_.aliquot);
    Join<ListLibraryAliquotView, ParentLibrary> library =
        idBuilder.getJoin(aliquot, ListLibraryAliquotView_.parentLibrary);
    Join<ParentLibrary, ParentSample> sample = idBuilder.getJoin(library, ParentLibrary_.parentSample);
    Join<ParentSample, ParentProject> project = idBuilder.getJoin(sample, ParentSample_.parentProject);
    idBuilder.addPredicate(idBuilder.getCriteriaBuilder().equal(project.get(ParentProject_.projectId), projectId));
    idBuilder.setColumn(idBuilder.getRoot().get(Run_.runId));
    List<Long> ids = idBuilder.getResultList();

    if (ids.isEmpty()) {
      return Collections.emptyList();
    }

    QueryBuilder<Run, Run> builder = getQueryBuilder();
    builder.addInPredicate(builder.getRoot().get(Run_.runId), ids);
    return builder.getResultList();
  }

  @Override
  public List<Run> listByStatus(String health) throws IOException {
    QueryBuilder<Run, Run> builder = getQueryBuilder();
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Run_.health), HealthType.get(health)));
    return builder.getResultList();
  }

  @Override
  public List<Run> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(Run_.RUN_ID, ids);
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<Run> root) {
    return Arrays.asList(root.get(Run_.name), root.get(Run_.alias), root.get(Run_.description));
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, Run> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(Run_.runId);
      case "platformType":
        Join<Run, InstrumentImpl> sequencer = builder.getJoin(builder.getRoot(), Run_.sequencer);
        Join<InstrumentImpl, InstrumentModel> model = builder.getJoin(sequencer, InstrumentImpl_.instrumentModel);
        return model.get(InstrumentModel_.platformType);
      case "status":
        return builder.getRoot().get(Run_.health);
      case "endDate":
        return builder.getRoot().get(Run_.completionDate);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, Run> builder, DateType type) {
    switch (type) {
      case CREATE:
        return builder.getRoot().get(Run_.startDate);
      case ENTERED:
        return builder.getRoot().get(Run_.creationTime);
      case UPDATE:
        return builder.getRoot().get(Run_.lastModified);
      default:
        return null;
    }
  }

  @Override
  public SingularAttribute<Run, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? Run_.creator : Run_.lastModifier;
  }

  @Override
  public SingularAttribute<Run, ?> getIdProperty() {
    return Run_.runId;
  }

  @Override
  public Class<Run> getEntityClass() {
    return Run.class;
  }

  @Override
  public Class<Run> getResultClass() {
    return Run.class;
  }

  @Override
  public void restrictPaginationByProjectId(QueryBuilder<?, Run> builder, long projectId,
      Consumer<String> errorHandler) {
    Join<Run, RunPosition> position = builder.getJoin(builder.getRoot(), Run_.runPositions);
    Join<RunPosition, SequencerPartitionContainerImpl> container = builder.getJoin(position, RunPosition_.container);
    Join<SequencerPartitionContainerImpl, PartitionImpl> partition =
        builder.getJoin(container, SequencerPartitionContainerImpl_.partitions);
    Join<PartitionImpl, PoolImpl> pool = builder.getJoin(partition, PartitionImpl_.pool);
    Join<PoolImpl, PoolElement> element = builder.getJoin(pool, PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquot = builder.getJoin(element, PoolElement_.aliquot);
    Join<ListLibraryAliquotView, ParentLibrary> library =
        builder.getJoin(aliquot, ListLibraryAliquotView_.parentLibrary);
    Join<ParentLibrary, ParentSample> sample = builder.getJoin(library, ParentLibrary_.parentSample);
    Join<ParentSample, ParentProject> project = builder.getJoin(sample, ParentSample_.parentProject);
    builder.addPredicate(builder.getCriteriaBuilder().equal(project.get(ParentProject_.projectId), projectId));
  }

  @Override
  public void restrictPaginationBySequencingParametersName(QueryBuilder<?, Run> builder, String query,
      Consumer<String> errorHandler) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      builder.addPredicate(builder.getCriteriaBuilder().isNull(builder.getRoot().get(Run_.sequencingParameters)));
    } else {
      Join<Run, SequencingParameters> params = builder.getJoin(builder.getRoot(), Run_.sequencingParameters);
      builder.addTextRestriction(params.get(SequencingParameters_.name), query);
    }
  }

  @Override
  public void restrictPaginationByHealth(QueryBuilder<?, Run> builder, EnumSet<HealthType> healths,
      Consumer<String> errorHandler) {
    builder.addInPredicate(builder.getRoot().get(Run_.health), healths);
  }

  @Override
  public void restrictPaginationByPlatformType(QueryBuilder<?, Run> builder, PlatformType platformType,
      Consumer<String> errorHandler) {
    Join<Run, InstrumentImpl> sequencer = builder.getJoin(builder.getRoot(), Run_.sequencer);
    Join<InstrumentImpl, InstrumentModel> instrumentModel = builder.getJoin(sequencer, InstrumentImpl_.instrumentModel);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(instrumentModel.get(InstrumentModel_.platformType), platformType));
  }

  @Override
  public void restrictPaginationByIndex(QueryBuilder<?, Run> builder, String query, Consumer<String> errorHandler) {
    Join<Run, RunPosition> position = builder.getJoin(builder.getRoot(), Run_.runPositions);
    Join<RunPosition, SequencerPartitionContainerImpl> container = builder.getJoin(position, RunPosition_.container);
    Join<SequencerPartitionContainerImpl, PartitionImpl> partition =
        builder.getJoin(container, SequencerPartitionContainerImpl_.partitions);
    Join<PartitionImpl, PoolImpl> pool = builder.getJoin(partition, PartitionImpl_.pool);
    Join<PoolImpl, PoolElement> element = builder.getJoin(pool, PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquot = builder.getJoin(element, PoolElement_.aliquot);
    Join<ListLibraryAliquotView, ParentLibrary> library =
        builder.getJoin(aliquot, ListLibraryAliquotView_.parentLibrary);
    Join<ParentLibrary, Index> index1 = builder.getJoin(library, ParentLibrary_.index1);
    Join<ParentLibrary, Index> index2 = builder.getJoin(library, ParentLibrary_.index2);
    builder.addTextRestriction(Arrays.asList(index1.get(Index_.name), index1.get(Index_.sequence),
        index2.get(Index_.name), index2.get(Index_.sequence)), query);
  }

  @Override
  public void restrictPaginationBySequencerId(QueryBuilder<?, Run> builder, long id, Consumer<String> errorHandler) {
    Join<Run, InstrumentImpl> sequencer = builder.getJoin(builder.getRoot(), Run_.sequencer);
    builder.addPredicate(builder.getCriteriaBuilder().equal(sequencer.get(InstrumentImpl_.id), id));
  }

  @Override
  public String getFriendlyName() {
    return "Run";
  }

  @Override
  public List<Run> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter)
      throws IOException {
    List<Run> runs = JpaCriteriaPaginatedDataSource.super.list(errorHandler, offset, limit, sortDir, sortCol, filter);
    if (runs.isEmpty()) {
      return runs;
    }

    QueryBuilder<Object[], Run> builder = new QueryBuilder<>(currentSession(), Run.class, Object[].class);
    Join<Run, RunPosition> position = builder.getJoin(builder.getRoot(), Run_.runPositions, JoinType.INNER);
    Join<RunPosition, SequencerPartitionContainerImpl> container =
        builder.getJoin(position, RunPosition_.container, JoinType.INNER);
    Join<SequencerPartitionContainerImpl, PartitionImpl> partition =
        builder.getJoin(container, SequencerPartitionContainerImpl_.partitions, JoinType.INNER);
    Join<PartitionImpl, PoolImpl> pool = builder.getJoin(partition, PartitionImpl_.pool, JoinType.INNER);
    Join<PoolImpl, PoolElement> element = builder.getJoin(pool, PoolImpl_.poolElements, JoinType.INNER);
    Join<PoolElement, ListLibraryAliquotView> aliquot = builder.getJoin(element, PoolElement_.aliquot, JoinType.INNER);
    Join<ListLibraryAliquotView, ParentLibrary> library =
        builder.getJoin(aliquot, ListLibraryAliquotView_.parentLibrary, JoinType.INNER);
    Join<ParentLibrary, ParentSample> sample = builder.getJoin(library, ParentLibrary_.parentSample, JoinType.INNER);
    Join<ParentSample, ParentProject> project = builder.getJoin(sample, ParentSample_.parentProject, JoinType.INNER);

    List<Long> ids = runs.stream().map(Run::getId).toList();
    builder.addInPredicate(builder.getRoot().get(Run_.runId), ids);
    builder.setColumns(builder.getRoot().get(Run_.runId), project.get(ParentProject_.code),
        project.get(ParentProject_.name));

    List<Object[]> results = builder.getResultList();

    for (Run run : runs) {
      run.setProjectsLabel(results.stream()
          .filter(arr -> ((Long) arr[0]).longValue() == run.getId())
          .map(arr -> (String) (arr[1] == null ? arr[2] : arr[1]))
          .collect(Collectors.joining(", ")));
    }
    return runs;
  }

}
