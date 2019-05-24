package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface StainCategoryService extends DeleterService<StainCategory>, SaveService<StainCategory> {

  List<StainCategory> list() throws IOException;

}
