package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

/**
 * This interface simply describes an object that can be placed into a box. i.e. Sample, Library
 * 
 */
@JsonIgnoreProperties({ "boxId", "boxAlias", "boxLocation" })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = SampleImpl.class, name = "SampleImpl"),
    @JsonSubTypes.Type(value = LibraryImpl.class, name = "LibraryImpl"), @JsonSubTypes.Type(value = PoolImpl.class, name = "PoolImpl") })

public interface Boxable extends Nameable, Barcodable {
  /**
   * Set the BoxId of this Boxable item
   * 
   * @param Box
   *          box to add
   */

  public void setBoxId(Long boxId);

  /**
   * Return the current BoxId of this Boxable item
   * 
   * @return Box current box
   */
  public Long getBoxId();

  public void setBoxAlias(String alias);

  public String getBoxAlias();

  /**
   * Returns the alias of this Sample object.
   *
   * @return String alias.
   */
  public String getAlias();

  /**
   * Sets the alias of this Sample object.
   *
   * @param alias
   *          alias.
   */
  public void setAlias(String alias);

  /**
   * Sets the 'emptied' attribute for the Implementor
   * 
   * @param boolean
   *          emptied
   */
  public void setDiscarded(boolean emptied);

  /**
   * Returns whether or not the Implementor has been emptied
   * 
   * @return emptied
   */
  public boolean isDiscarded();

  /**
   * Returns the volume of the Implementor
   * 
   * @return volume
   */
  public Double getVolume();

  /**
   * Sets the volume of the Implementor
   * 
   * @param double
   *          volume
   */
  public void setVolume(Double volume);

  public Long getBoxPositionId();

  public void setBoxPositionId(Long id);

  public String getBoxPosition();

  public void setBoxPosition(String id);

  public String getBoxLocation();

  public void setBoxLocation(String boxLocation);

  public Date getLastModified();

  public String getLocationBarcode();

}
