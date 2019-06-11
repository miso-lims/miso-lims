package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;

public interface ProviderDao<T> {

  public T get(long id) throws IOException;

  public List<T> list() throws IOException;

}
