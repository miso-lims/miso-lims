package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public interface QualityControlTypeStore {
  Collection<QcType> list() throws IOException;

  QcType get(long id) throws IOException;
}
