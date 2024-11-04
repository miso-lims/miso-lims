package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest;

public interface AssayTestDao extends BulkSaveDao<AssayTest> {

  AssayTest getByAlias(String alias) throws IOException;

  long getUsage(AssayTest test) throws IOException;

}
