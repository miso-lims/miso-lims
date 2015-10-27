package uk.ac.bbsrc.tgac.miso.core.data;

/**
 * uk.ac.bbsrc.tgac.miso.core.data
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 13/09/12
 * @since 0.1.8
 */
public interface Nameable {
  /**
   * Returns the name of this Nameable object.
   * 
   * @return String name.
   */
  public String getName();

  /**
   * Returns the unique ID of this Nameable object.
   * 
   * @return long id.
   */
  public long getId();
}
