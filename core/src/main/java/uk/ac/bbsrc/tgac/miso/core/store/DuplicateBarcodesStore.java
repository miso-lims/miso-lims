package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.DuplicateBarcodes;

public interface DuplicateBarcodesStore {
  public Collection<DuplicateBarcodes> listAll() throws IOException;
}
