package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;

public interface StainService extends DeleterService<Stain>, SaveService<Stain> {

  public List<Stain> list() throws IOException;

}
