package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * This interface describes a Box which is a n by m container which contains tubes which contain Samples/Libraries.
 * 
 * A Box typically is labeled using a combination of letters and numbers. For example, the first position in a box would be "A01".
 * 
 * A Box usually has dimensions 8 by 12. (A-H, 1-12, A01 through H12)
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties({ "securityProfile", "free", "2DArray", "lastModifier" })
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface Box extends SecurableByProfile, Barcodable, Locatable, Deletable {
  public static class BoxablesSerializer extends JsonSerializer<Map<String, Boxable>> {
    @Override
    public void serialize(Map<String, Boxable> map, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
      ObjectMapper mapper = new ObjectMapper();
      ObjectWriter writer = mapper.writerWithType(Boxable.class);
      jgen.writeStartObject();
      for (String key : map.keySet()) {
        jgen.writeFieldName(key);
        writer.writeValue(jgen, map.get(key));
      }
      jgen.writeEndObject();
    }
  }

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
   * @return Map<String, Boxable> items
   */
  @JsonSerialize(using = BoxablesSerializer.class)
  public Map<String, Boxable> getBoxables();

  /**
   * Sets the Map of BoxItems of this Box object.
   * 
   * @param Map
   *          <String, Boxable> items
   * @throws InvalidBoxPositionException
   */
  public void setBoxables(Map<String, Boxable> items);

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
  public void setBoxable(String position, Boxable item);

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
  public Boxable getBoxable(String position);

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
   * Removes a given Boxable item from the Box
   * 
   * @param Boxable
   *          boxable
   */
  public void removeBoxable(Boxable boxable);

  /**
   * Removes ALL Boxable items from the Box
   * 
   */
  public void removeAllBoxables();

  /**
   * Returns the number of free positions left in the Box.
   * 
   * @return int free positions
   */
  public int getFree();

  /**
   * Returns true/false is the position is free or not
   * 
   * @return true/false if position is taken by another Boxable item or not
   * @throws IllegalArgumentException
   *           if the given position is not in the correct format IndexOutOfBoundsException if the given Row letter or column value is too
   *           big for the Box
   */
  public boolean isFreePosition(String position);

  /**
   * Returns whether or not the given String is a valid position or not
   * 
   * @return validity of the position string
   * @throws IllegalArgumentException
   *           if the given position is not in the correct format IndexOutOfBoundsException if the given Row letter or column value is too
   *           big for the Box
   */
  public boolean isValidPosition(String position);

  /**
   * Returns whether or not the given Boxable item exists in the Box
   * 
   * @return existence of boxable in Box
   */
  public boolean boxableExists(Boxable boxable);

  /**
   * Get the Boxable items in 2-D array form
   * 
   * @return 2D Boxable array
   */
  public Boxable[][] get2DArray();

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

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  /**
   * Returns the change logs of this Box object.
   * 
   * @return Collection<ChangeLog> change logs.
   */
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
