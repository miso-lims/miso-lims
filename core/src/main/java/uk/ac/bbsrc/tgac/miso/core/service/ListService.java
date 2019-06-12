package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;

public interface ListService<T extends Identifiable> extends ProviderService<T> {

  public List<T> list() throws IOException;

}
