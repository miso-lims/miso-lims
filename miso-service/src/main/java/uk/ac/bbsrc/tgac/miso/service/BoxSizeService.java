package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface BoxSizeService extends DeleterService<BoxSize>, SaveService<BoxSize> {

  public List<BoxSize> list() throws IOException;

}
