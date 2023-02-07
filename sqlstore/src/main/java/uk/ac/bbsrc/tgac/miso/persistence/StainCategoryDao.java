package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;

public interface StainCategoryDao extends BulkSaveDao<StainCategory> {

  StainCategory getByName(String name) throws IOException;

  long getUsage(StainCategory stainCategory) throws IOException;

}
