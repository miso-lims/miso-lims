package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * This interface simply describes an object that can be placed into a box. i.e. Sample, Library
 * 
 */
@JsonIgnoreProperties({ "boxId", "boxAlias", "boxLocation" })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = SampleImpl.class, name = "SampleImpl"),
    @JsonSubTypes.Type(value = LibraryImpl.class, name = "LibraryImpl"), @JsonSubTypes.Type(value = PoolImpl.class, name = "PoolImpl") })
public interface Boxable extends Nameable, Barcodable, SecurableByProfile, Serializable {

  public enum EntityType {
    SAMPLE(SampleImpl.class),
    LIBRARY(LibraryImpl.class),
    DILUTION(LibraryDilution.class),
    POOL(PoolImpl.class);

    private final Class<? extends Boxable> persistClass;

    private EntityType(Class<? extends Boxable> persistClass) {
      this.persistClass = persistClass;
    }

    public Class<? extends Boxable> getPersistClass() {
      return persistClass;
    }
  }

  public EntityType getEntityType();

  /**
   * Returns the alias of this Sample object.
   *
   * @return String alias.
   */
  public String getAlias();

  public Box getBox();

  public String getBoxPosition();

  public Date getLastModified();

  public String getLocationBarcode();

  /**
   * Returns the volume of the Implementor
   * 
   * @return volume
   */
  public Double getVolume();

  /**
   * Returns whether or not the Implementor has been emptied
   * 
   * @return emptied
   */
  public boolean isDiscarded();

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
   * Sets the volume of the Implementor
   * 
   * @param double
   *          volume
   */
  public void setVolume(Double volume);

  public Long getPreMigrationId();

}
