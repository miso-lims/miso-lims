package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;

public interface StainDao extends BulkSaveDao<Stain> {

  Stain getByName(String name) throws IOException;

  long getUsage(Stain stain) throws IOException;

}
