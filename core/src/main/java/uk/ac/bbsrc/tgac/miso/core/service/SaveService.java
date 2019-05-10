package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;

public interface SaveService<T extends Identifiable> extends ProviderService<T> {

  public long create(T object) throws IOException;

  public long update(T object) throws IOException;

}
