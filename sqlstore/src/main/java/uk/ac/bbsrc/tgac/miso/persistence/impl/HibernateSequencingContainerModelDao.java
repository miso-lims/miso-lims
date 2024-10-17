package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel_;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Run_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel_;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingContainerModelStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencingContainerModelDao extends HibernateSaveDao<SequencingContainerModel>
    implements SequencingContainerModelStore {

  public HibernateSequencingContainerModelDao() {
    super(SequencingContainerModel.class);
  }

  @Override
  public SequencingContainerModel find(InstrumentModel instrumentModel, String search, int partitionCount) {
    SequencingContainerModel model;
    QueryBuilder<SequencingContainerModel, SequencingContainerModel> builder =
        new QueryBuilder<>(currentSession(), SequencingContainerModel.class, SequencingContainerModel.class);
    Join<SequencingContainerModel, InstrumentModel> instrument =
        builder.getJoin(builder.getRoot(), SequencingContainerModel_.instrumentModels);
    builder.addPredicate(builder.getCriteriaBuilder().equal(instrument.get(InstrumentModel_.instrumentModelId),
        instrumentModel.getId()));
    builder.addPredicate(builder.getCriteriaBuilder()
        .equal(builder.getRoot().get(SequencingContainerModel_.partitionCount), partitionCount));

    if (LimsUtils.isStringEmptyOrNull(search)) {
      builder.addPredicate(
          builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingContainerModel_.fallback),
              true));
      model = (SequencingContainerModel) builder.getSingleResultOrNull();
    } else {
      builder.addPredicate(builder.getCriteriaBuilder().or(
          builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingContainerModel_.alias), search),
          builder.getCriteriaBuilder().equal(
              builder.getRoot().get(SequencingContainerModel_.identificationBarcode),
              search)));
      model = (SequencingContainerModel) builder.getSingleResultOrNull();
      if (model == null) {
        // remove search restriction and get fallback option if search did not retrieve anything
        QueryBuilder<SequencingContainerModel, SequencingContainerModel> fallback =
            new QueryBuilder<>(currentSession(), SequencingContainerModel.class,
                SequencingContainerModel.class);
        Join<SequencingContainerModel, InstrumentModel> instrumentJoin =
            fallback.getJoin(fallback.getRoot(), SequencingContainerModel_.instrumentModels);
        fallback.addPredicate(fallback.getCriteriaBuilder()
            .equal(instrumentJoin.get(InstrumentModel_.instrumentModelId), instrumentModel.getId()));
        fallback.addPredicate(fallback.getCriteriaBuilder()
            .equal(fallback.getRoot().get(SequencingContainerModel_.partitionCount), partitionCount));
        fallback.addPredicate(
            fallback.getCriteriaBuilder().equal(fallback.getRoot().get(SequencingContainerModel_.fallback),
                true));
        model = (SequencingContainerModel) fallback.getSingleResultOrNull();
      }
    }
    return model;
  }

  @Override
  public List<SequencingContainerModel> find(PlatformType platform, String search) throws IOException {
    QueryBuilder<SequencingContainerModel, SequencingContainerModel> builder =
        new QueryBuilder<>(currentSession(), SequencingContainerModel.class, SequencingContainerModel.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingContainerModel_.platformType),
            platform));
    builder.addPredicate(builder.getCriteriaBuilder().or(
        builder.getCriteriaBuilder().like(builder.getRoot().get(SequencingContainerModel_.alias), search + '%'),
        builder.getCriteriaBuilder().equal(
            builder.getRoot().get(SequencingContainerModel_.identificationBarcode),
            search)));
    return builder.getResultList();
  }

  @Override
  public SequencingContainerModel getByPlatformAndAlias(PlatformType platform, String alias) throws IOException {
    QueryBuilder<SequencingContainerModel, SequencingContainerModel> builder =
        new QueryBuilder<>(currentSession(), SequencingContainerModel.class, SequencingContainerModel.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingContainerModel_.platformType),
            platform));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingContainerModel_.alias), alias));
    return builder.getSingleResultOrNull();
  }

  @Override
  public SequencingContainerModel getByPlatformAndBarcode(PlatformType platform, String identificationBarcode)
      throws IOException {
    QueryBuilder<SequencingContainerModel, SequencingContainerModel> builder =
        new QueryBuilder<>(currentSession(), SequencingContainerModel.class, SequencingContainerModel.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingContainerModel_.platformType),
            platform));
    builder.addPredicate(builder.getCriteriaBuilder()
        .equal(builder.getRoot().get(SequencingContainerModel_.identificationBarcode), identificationBarcode));
    return builder.getSingleResultOrNull();
  }

  @Override
  public long getUsage(SequencingContainerModel model) throws IOException {
    return getUsageBy(SequencerPartitionContainerImpl.class, SequencerPartitionContainerImpl_.model, model);
  }

  @Override
  public long getUsage(SequencingContainerModel containerModel, InstrumentModel instrumentModel) throws IOException {
    LongQueryBuilder<SequencerPartitionContainerImpl> builder =
        new LongQueryBuilder<>(currentSession(), SequencerPartitionContainerImpl.class);
    Join<SequencerPartitionContainerImpl, RunPosition> runPosition =
        builder.getJoin(builder.getRoot(), SequencerPartitionContainerImpl_.runPositions);
    Join<RunPosition, Run> run = builder.getJoin(runPosition, RunPosition_.run);
    Join<Run, InstrumentImpl> sequencer = builder.getJoin(run, Run_.sequencer);
    builder.addPredicate(builder.getCriteriaBuilder()
        .equal(builder.getRoot().get(SequencerPartitionContainerImpl_.model), containerModel));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(sequencer.get(InstrumentImpl_.instrumentModel), instrumentModel));
    return builder.getCount();
  }

  @Override
  public List<SequencingContainerModel> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(SequencingContainerModel_.SEQUENCING_CONTAINER_MODEL_ID, idList);
  }

}
