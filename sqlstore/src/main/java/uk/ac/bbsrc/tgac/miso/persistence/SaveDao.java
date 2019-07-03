package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

public interface SaveDao<T> extends ProviderDao<T> {

  public long create(T object) throws IOException;

  public long update(T object) throws IOException;

}
