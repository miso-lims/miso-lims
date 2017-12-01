package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

/**
 * Interface for generating values for one of an entity's identifying String fields. This is not always a "name" field
 *
 * @param <T> the type of entity to generate names for
 */
public interface NameGenerator<T> {

  /**
   * Generates the field value for object
   * 
   * @param object the entity to generate a field value for
   * @return the generated value
   * @throws MisoNamingException if any <b>user-preventable</b> error occurs
   * @throws IOException if database access is required and fails
   */
  public String generate(T object) throws MisoNamingException, IOException;

}
