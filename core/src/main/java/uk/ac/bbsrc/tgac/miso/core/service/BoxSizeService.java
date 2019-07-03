package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;

public interface BoxSizeService extends DeleterService<BoxSize>, SaveService<BoxSize> {

  public List<BoxSize> list() throws IOException;

}
