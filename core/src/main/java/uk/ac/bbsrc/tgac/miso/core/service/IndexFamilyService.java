package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;

public interface IndexFamilyService extends DeleterService<IndexFamily>, ListService<IndexFamily>, SaveService<IndexFamily> {

  public IndexFamily getByName(String name) throws IOException;

}
