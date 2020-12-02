package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;

public interface PipelineService extends BulkSaveService<Pipeline>, DeleterService<Pipeline> {

  List<Pipeline> list() throws IOException;

}
