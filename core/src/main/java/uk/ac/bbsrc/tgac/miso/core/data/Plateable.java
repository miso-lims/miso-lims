package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.LinkedList;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * uk.ac.bbsrc.tgac.miso.core.data
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 14/12/12
 * @since 0.1.9
 */
public interface Plateable extends Nameable {
  /**
   * Returns the plates that this Plateable object is a part of.
   * 
   * @return Set<Plate>
   */
  @JsonIgnore
  public <T> Set<Plate<LinkedList<T>, T>> getPlates() throws Exception;
}