package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface StainService extends DeleterService<Stain>, SaveService<Stain> {

  public List<Stain> list() throws IOException;

}
