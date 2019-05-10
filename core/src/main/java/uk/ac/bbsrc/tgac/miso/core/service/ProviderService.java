package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;

public interface ProviderService<T extends Identifiable> {

  public T get(long id) throws IOException;

}
