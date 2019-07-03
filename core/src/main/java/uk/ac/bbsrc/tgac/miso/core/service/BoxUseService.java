package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;

public interface BoxUseService extends DeleterService<BoxUse>, SaveService<BoxUse> {

  public List<BoxUse> list() throws IOException;

}
