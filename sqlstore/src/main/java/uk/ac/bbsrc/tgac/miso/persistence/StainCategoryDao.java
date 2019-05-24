package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;

public interface StainCategoryDao {

  StainCategory get(long id) throws IOException;

  StainCategory getByName(String name) throws IOException;

  List<StainCategory> list() throws IOException;

  long create(StainCategory stainCategory) throws IOException;

  long update(StainCategory stainCategory) throws IOException;

  long getUsage(StainCategory stainCategory) throws IOException;

}
