package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * This interface describes a Box which is a n by m container which contains tubes which contain Samples/Libraries.
 * 
 * A Box typically is labeled using a combination of letters and numbers. For example, the first position in a box would be "A01".
 * 
 * A Box usually has dimensions 8 by 12. (A-H, 1-12, A01 through H12)
 */
public interface Box extends SecurableByProfile, Barcodable, Locatable, Deletable, ChangeLoggable, Serializable {

  public static final String PREFIX = "BOX";

  /**
   * Sets the Id of this Box object.
   * 
   * @param long
   *          id.
   */
  public void setId(long id);

  /**
   * Sets the name of this Box object.
   * 
   * @param String
   *          name.
   */
  public void setName(String name);

  /**
   * Returns the Alias of this Box object.
   * 
   * @return String alias.
   */
  public String getAlias();

  /**
   * Sets the alias of this Box object.
   * 
   * @param String
   *          alias.
   */
  public void setAlias(String alias);

  /**
   * Returns the description of this Box object.
   * 
   * @return String description
   */
  public String getDescription();

  /**
   * Sets the description of this Box object.
   * 
   * @param String
   *          alias
   */
  public void setDescription(String description);

  /**
   * Returns the Map representing the Boxables (Samples, Libraries, Pools) of this Box object.
   * 
   * @return items
   */
  public Map<String, BoxableView> getBoxables();

  /**
   * Sets the Map of BoxItems of this Box object.
   * 
   * @param items
   * @throws InvalidBoxPositionException
   */
  public void setBoxables(Map<String, BoxableView> items);

  /**
   * Adds a BoxItem to the Box object at the given position.
   * 
   * Note: this method is not responsible for any Boxable stored at the given position, it will be replaced by the given item.
   * 
   * @param BoxItem
   *          item, String position.
   * @throws IllegalArgumentException
   *           if the given position is not in the correct format IndexOutOfBoundsException if the given Row letter or column value is too
   *           big for the Box
   */
  public void setBoxable(String position, BoxableView item);

  /**
   * Returns the Boxable at position given.
   * 
   * @param String
   *          position
   * @return BoxItem at position
   * @throws IllegalArgumentException
   *           if the given position is not in the correct format IndexOutOfBoundsException if the given Row letter or column value is too
   *           big for the Box
   */
  public BoxableView getBoxable(String position);

  /**
   * Removes a Boxable item from the given position
   * 
   * @param String
   *          position
   * @throws IllegalArgumentException
   *           if the given position is not in the correct format IndexOutOfBoundsException if the given Row letter or column value is too
   *           big for the Box
   */
  public void removeBoxable(String position);

  /**
   * Removes ALL Boxable items from the Box
   * 
   */
  public void removeAllBoxables();
  
  /**
   * @return the maximum number of tubes that can be stored in this box
   */
  public int getPositionCount();

  /**
   * Returns the number of free positions left in the Box.
   * 
   * @return int free positions
   */
  public int getFreeCount();
  
  /**
   * @return the number of tubes that are stored in this box
   */
  public int getTubeCount();

  /**
   * Returns true/false is the position is free or not
   * 
   * @return true/false if position is taken by another Boxable item or not
   * @throws IllegalArgumentException
   *           if the given position is not in the correct format
   * @throws IndexOutOfBoundsException if the given Row letter or column value is too
   *           big for the Box
   */
  public boolean isFreePosition(String position);

  /**
   * Returns whether or not the given String is a valid position or not
   * 
   * @return validity of the position string
   */
  public boolean isValidPosition(String position);

  /**
   * Returns the BoxUse for this Box item.
   * 
   * @return BoxUse box use
   */
  public BoxUse getUse();

  /**
   * Sets the BoxUse for this Box item.
   * 
   * @param type
   *          of type BoxUse
   */
  public void setUse(BoxUse type);

  /**
   * Returns the BoxSize for this Box item.
   * 
   * @return BoxSize box size
   */
  public BoxSize getSize();

  /**
   * Sets the BoxSize for this Box item.
   * 
   * @param size
   *          of type BoxSize
   */
  public void setSize(BoxSize size);

  @Override
  public String getLocationBarcode();

  @Override
  public void setLocationBarcode(String barcode);

  Date getLastModified();

  /**
   * Returns the change logs of this Box object.
   * 
   * @return Collection<ChangeLog> change logs.
   */
  @Override
  public Collection<ChangeLog> getChangeLog();

  /**
   * Returns the user who last modified this item.
   */
  public User getLastModifier();

  /**
   * Sets the user who last modified this item. It should always be set to the current user on save.
   */
  public void setLastModifier(User user);
}
