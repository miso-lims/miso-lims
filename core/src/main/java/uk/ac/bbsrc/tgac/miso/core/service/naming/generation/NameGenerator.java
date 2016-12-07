package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

/**
 * Interface for generating values for one of an entity's identifying String fields. This is not always a "name" field
 *
 * @param <T> the type of entity to generate names for
 */
public interface NameGenerator<T> {

  public String generate(T object) throws MisoNamingException;

}
