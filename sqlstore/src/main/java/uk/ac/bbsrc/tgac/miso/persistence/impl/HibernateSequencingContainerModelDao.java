package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
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
    Criteria criteria = currentSession().createCriteria(SequencingContainerModel.class);
    criteria.createAlias("instrumentModels", "instrumentModel");
    criteria.add(Restrictions.eq("instrumentModel.id", instrumentModel.getId()));
    criteria.add(Restrictions.eq("partitionCount", partitionCount));
    if (LimsUtils.isStringEmptyOrNull(search)) {
      criteria.add(Restrictions.eq("fallback", true));
      model = (SequencingContainerModel) criteria.uniqueResult();
    } else {
      criteria.add(Restrictions.or(Restrictions.eq("alias", search), Restrictions.eq("identificationBarcode", search)));
      model = (SequencingContainerModel) criteria.uniqueResult();
      if (model == null) {
        // remove search restriction and get fallback option if search did not retrieve anything
        Criteria fallback = currentSession().createCriteria(SequencingContainerModel.class);
        fallback.createAlias("instrumentModels", "instrumentModel");
        fallback.add(Restrictions.eq("instrumentModel.id", instrumentModel.getId()));
        fallback.add(Restrictions.eq("partitionCount", partitionCount));
        fallback.add(Restrictions.eq("fallback", true));
        model = (SequencingContainerModel) fallback.uniqueResult();
      }
    }
    return model;
  }

  @Override
  public List<SequencingContainerModel> find(PlatformType platform, String search) throws IOException {
    @SuppressWarnings("unchecked")
    List<SequencingContainerModel> results = currentSession().createCriteria(SequencingContainerModel.class)
        .add(Restrictions.eq("platformType", platform))
        .add(Restrictions.or(Restrictions.ilike("alias", search, MatchMode.START), Restrictions.eq("identificationBarcode", search)))
        .list();
    return results;
  }

  @Override
  public SequencingContainerModel getByPlatformAndAlias(PlatformType platform, String alias) throws IOException {
    return (SequencingContainerModel) currentSession().createCriteria(SequencingContainerModel.class)
        .add(Restrictions.eq("platformType", platform))
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

  @Override
  public SequencingContainerModel getByPlatformAndBarcode(PlatformType platform, String identificationBarcode) throws IOException {
    return (SequencingContainerModel) currentSession().createCriteria(SequencingContainerModel.class)
        .add(Restrictions.eq("platformType", platform))
        .add(Restrictions.eq("identificationBarcode", identificationBarcode))
        .uniqueResult();
  }

  @Override
  public long getUsage(SequencingContainerModel model) throws IOException {
    return getUsageBy(SequencerPartitionContainerImpl.class, "model", model);
  }

  @Override
  public long getUsage(SequencingContainerModel containerModel, InstrumentModel instrumentModel) throws IOException {
    return (long) currentSession().createCriteria(SequencerPartitionContainerImpl.class)
        .add(Restrictions.eq("model", containerModel))
        .createAlias("runPositions", "runPosition")
        .createAlias("runPosition.run", "run")
        .createAlias("run.sequencer", "sequencer")
        .add(Restrictions.eq("sequencer.instrumentModel", instrumentModel))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

  @Override
  public List<SequencingContainerModel> listByIdList(List<Long> idList) throws IOException {
    return listByIdList("sequencingContainerModelId", idList);
  }

}
