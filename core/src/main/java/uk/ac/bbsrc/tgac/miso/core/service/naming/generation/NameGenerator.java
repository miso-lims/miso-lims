package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

public interface NameGenerator<T> {

  public String generate(T object) throws MisoNamingException;

}
