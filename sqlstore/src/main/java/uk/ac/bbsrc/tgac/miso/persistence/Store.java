package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

/**
 * Defines a DAO interface
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Store<T> {
  /**
   * Save a persistable object of a given type T
   * 
   * @param t of type T
   * @return long
   * @throws IOException when the object cannot be saved
   */
  public long save(T t) throws IOException;

  /**
   * Get a persisted object of a given type T
   * 
   * @param id of type long
   * @return T
   * @throws IOException when the object cannot be retrieved
   */
  public T get(long id) throws IOException;

  /**
   * List all persisted objects of a given type T
   * 
   * @return Collection<T>
   * @throws IOException when the objects cannot be retrieved
   */
  public List<T> listAll() throws IOException;

}
