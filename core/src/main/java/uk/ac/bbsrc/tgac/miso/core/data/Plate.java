/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlateMaterialType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

import java.util.Date;
import java.util.LinkedList;

/**
 * A Plate represents a collection of sequenceable material, typed by that material object, usually a List of {@link Library}
 * elements of a given size. Plates can be described further by a {@link PlateMaterialType} and a plate-specific {@link TagBarcode}
 *
 * @author Rob Davey
 * @date 25-Jul-2011
 * @since 0.0.3
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
@JsonWriteNullProperties(false)
@JsonIgnoreProperties({"securityProfile"})
@PrintableBarcode
public interface Plate<T extends LinkedList<S>, S> extends SecurableByProfile, Barcodable, Locatable, Comparable, Deletable, Poolable {
  /**
   * Gets the current plateId
   *
   * @return Long
   */
  public Long getPlateId();

  /**
   * Sets the plateId of this Plate object
   *
   * @param plateId Long.
   *
   */
  public void setPlateId(Long plateId);

  /**
   * Returns the name of this Plate object.
   *
   * @return String name.
   */
  public String getName();

  /**
   * Sets the name of this Plate object.
   *
   * @param name name.
   */
  public void setName(String name);

  /**
   * Returns the description of this Plate object.
   *
   * @return String description.
   */
  public String getDescription();

  /**
   * Sets the description of this Plate object.
   *
   * @param description description.
   */
  public void setDescription(String description);

  /**
   * Returns the creationDate of this Plate object.
   *
   * @return Date creationDate.
   */
  public Date getCreationDate();

  /**
   * Sets the creationDate of this Plate object.
   *
   * @param date creationDate.
   */
  public void setCreationDate(Date date);

  /**
   * Returns the plateMaterialType of this Plate object.
   *
   * @return PlateMaterialType plateMaterialType.
   */
  public PlateMaterialType getPlateMaterialType();

  /**
   * Sets the plateMaterialType of this Plate object.
   *
   * @param plateMaterialType PlateMaterialType.
   */
  public void setPlateMaterialType(PlateMaterialType plateMaterialType);

  /**
   * Returns the TagBarcode of this Plate object
   *
   * @return TagBarcode tagBarcode.
   */
  public TagBarcode getTagBarcode();

  /**
   * Sets the TagBarcode of this Plate object.
   *
   * @param tagBarcode TagBarcode.
   */
  public void setTagBarcode(TagBarcode tagBarcode);

  /**
   * Returns the Plate size
   *
   * @return int size.
   */
  public int getSize();

  public Class getElementType();

  /**
   * Returns the list of Elements present on this Plate object
   *
   * @return T element.
   */
  public T getElements();

  /**
   * Adds an Element to this Plate object
   *
   * @param element S.
   */
  public void addElement(S element);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);
}
