package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface TissueMaterialDao extends BulkSaveDao<TissueMaterial> {

  TissueMaterial getByAlias(String alias) throws IOException;

  long getUsage(TissueMaterial tissueMaterial) throws IOException;

}