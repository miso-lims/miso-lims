package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayMetric;

public interface AssayDao extends SaveDao<Assay> {

  Assay getByAliasAndVersion(String alias, String version) throws IOException;

  long getUsage(Assay assay) throws IOException;

  List<Assay> listByIdList(Collection<Long> ids) throws IOException;

  void deleteAssayMetric(AssayMetric metric) throws IOException;

}
