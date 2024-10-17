package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;

public interface TissueMaterialDao extends BulkSaveDao<TissueMaterial> {

  TissueMaterial getByAlias(String alias) throws IOException;

  long getUsage(TissueMaterial tissueMaterial) throws IOException;

}
