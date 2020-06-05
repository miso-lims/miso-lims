package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.ScientificName;

public interface ScientificNameService extends DeleterService<ScientificName>, ListService<ScientificName>, SaveService<ScientificName> {

  public ScientificName getByAlias(String alias) throws IOException;

}
