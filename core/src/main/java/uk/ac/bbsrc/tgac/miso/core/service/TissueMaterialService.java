package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;

public interface TissueMaterialService extends DeleterService<TissueMaterial>, ListService<TissueMaterial> {

  Long create(TissueMaterial tissueMaterial) throws IOException;

  void update(TissueMaterial tissueMaterial) throws IOException;

}