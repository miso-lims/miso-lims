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

//import com.fasterxml.jackson.annotation.*;
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlateMaterialType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * A Plate represents a collection of sequenceable material, typed by that material object, usually a List of {@link Library}
 * elements of a given size. Plates can be described further by a {@link PlateMaterialType}
 *
 * @author Rob Davey
 * @date 25-Jul-2011
 * @since 0.0.3
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
@JsonIgnoreProperties({"securityProfile"})
@PrintableBarcode
public interface Plate<T extends List<S>, S> extends SecurableByProfile, Barcodable, Locatable, Comparable, Deletable, Poolable<Plate<LinkedList<S>, S>, S> {
  /**
   * Gets the current plateId
   *
   * @return Long
   */
  @Deprecated
  public Long getPlateId();

  /**
   * Sets the plateId of this Plate object
   *
   * @param plateId Long.
   *
   */
  @Deprecated
  public void setPlateId(Long plateId);

  /**
   * Sets the ID of this Plate object.
   *
   * @param id long.
   */
  public void setId(long id);

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
   * Returns the Plate size
   *
   * @return int size.
   */
  public int getSize();

  public void setSize(int size) throws Exception;

  public Class getElementType();

  /**
   * Returns the list of Elements present on this Plate object
   *
   * @return T element.
   */
  //@JsonManagedReference
  public T getElements();

  public void setElements(T elements);

  /**
   * Adds an Element to this Plate object
   *
   * @param element S.
   */
  public void addElement(S element);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);
}
