package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.service.ListService;

public interface TissueMaterialService extends DeleterService<TissueMaterial>, ListService<TissueMaterial> {

  Long create(TissueMaterial tissueMaterial) throws IOException;

  void update(TissueMaterial tissueMaterial) throws IOException;

}