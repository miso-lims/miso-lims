package uk.ac.bbsrc.tgac.miso.service.impl;

public class ServiceUtils {

  /**
   * Throw an IllegalArgumentException if the object (o), the result of a lookup, is null. The exception message will contain the name
   * (name) and id (id) of the failed lookup.
   * 
   * @param o
   *          The object being retrieved
   * @param name
   *          The name of the object
   * @param id
   *          The id of the object
   */
  public static void throwIfNull(Object o, String name, Long id) {
    if (o == null) {
      throw new IllegalArgumentException(String.format("The %s %d could not be found.", name, id));
    }
  }
}
