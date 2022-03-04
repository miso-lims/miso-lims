package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface AssayTestDao extends SaveDao<AssayTest> {

  AssayTest getByAlias(String alias) throws IOException;

  long getUsage(AssayTest test) throws IOException;

  List<AssayTest> listByIdList(Collection<Long> ids) throws IOException;

}
