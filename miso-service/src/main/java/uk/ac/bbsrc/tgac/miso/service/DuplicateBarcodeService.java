package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.DuplicateBarcodes;

public interface DuplicateBarcodeService {
  public Collection<DuplicateBarcodes> getAll() throws IOException;
}
