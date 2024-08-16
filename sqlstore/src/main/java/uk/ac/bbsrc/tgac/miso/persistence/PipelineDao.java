package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;

public interface PipelineDao extends BulkSaveDao<Pipeline> {

  Pipeline getByAlias(String alias) throws IOException;

  long getUsage(Pipeline pipeline) throws IOException;

}
