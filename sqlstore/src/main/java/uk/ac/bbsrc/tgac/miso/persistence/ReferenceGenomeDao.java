package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;

public interface ReferenceGenomeDao extends BulkSaveDao<ReferenceGenome> {

  ReferenceGenome getByAlias(String alias) throws IOException;

  long getUsage(ReferenceGenome reference) throws IOException;

}
