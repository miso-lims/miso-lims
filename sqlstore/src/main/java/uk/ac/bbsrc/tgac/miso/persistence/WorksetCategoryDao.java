package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetCategory;

public interface WorksetCategoryDao extends BulkSaveDao<WorksetCategory> {

  WorksetCategory getByAlias(String alias) throws IOException;

  long getUsage(WorksetCategory category) throws IOException;

}
