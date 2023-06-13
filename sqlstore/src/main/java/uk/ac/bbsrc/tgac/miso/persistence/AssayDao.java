package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayMetric;

public interface AssayDao extends BulkSaveDao<Assay> {

  Assay getByAliasAndVersion(String alias, String version) throws IOException;

  long getUsage(Assay assay) throws IOException;

  void deleteAssayMetric(AssayMetric metric) throws IOException;

}
