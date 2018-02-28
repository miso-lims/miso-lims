package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;

public interface ProviderService<T> {

  public T get(long id) throws IOException;

}
