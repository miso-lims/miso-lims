package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;

public interface StainCategoryService extends DeleterService<StainCategory>, SaveService<StainCategory> {

  List<StainCategory> list() throws IOException;

}
