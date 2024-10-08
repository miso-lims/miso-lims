package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.ScientificName;

public interface ScientificNameDao extends BulkSaveDao<ScientificName> {

  public ScientificName getByAlias(String alias) throws IOException;

  public long getUsageBySamples(ScientificName scientificName) throws IOException;

  public long getUsageByReferenceGenomes(ScientificName scientificName) throws IOException;

}
