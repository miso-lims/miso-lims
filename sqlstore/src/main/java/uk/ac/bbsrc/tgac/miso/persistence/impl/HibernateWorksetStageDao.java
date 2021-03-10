package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetStage;
import uk.ac.bbsrc.tgac.miso.persistence.WorksetStageDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateWorksetStageDao extends HibernateSaveDao<WorksetStage> implements WorksetStageDao {

  public HibernateWorksetStageDao() {
    super(WorksetStage.class);
  }

  @Override
  public WorksetStage getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public long getUsage(WorksetStage stage) throws IOException {
    return getUsageBy(Workset.class, "stage", stage);
  }

  @Override
  public List<WorksetStage> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("stageId", ids);
  }

}
