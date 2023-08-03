package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.persistence.PipelineDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePipelineDao extends HibernateSaveDao<Pipeline> implements PipelineDao {

  public HibernatePipelineDao() {
    super(Pipeline.class);
  }

  @Override
  public Pipeline getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public long getUsage(Pipeline pipeline) throws IOException {
    return getUsageBy(ProjectImpl.class, "pipeline", pipeline);
  }

  @Override
  public List<Pipeline> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(Pipeline_.PIPELINE_ID, idList);
  }

}
