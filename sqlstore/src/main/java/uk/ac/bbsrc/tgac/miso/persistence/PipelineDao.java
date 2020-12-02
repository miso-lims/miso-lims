package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;

public interface PipelineDao extends SaveDao<Pipeline> {

  Pipeline getByAlias(String alias) throws IOException;

  long getUsage(Pipeline pipeline) throws IOException;

  List<Pipeline> listByIdList(List<Long> idList) throws IOException;

}
