package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface BoxUseService extends DeleterService<BoxUse>, SaveService<BoxUse> {

  public List<BoxUse> list() throws IOException;

}
