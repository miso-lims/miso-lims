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

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * A Platform describes metadata about potentially any hardware item, but is usully linked to a sequencer implementation.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface Platform extends Comparable {
  /**
   * Returns the platformId of this Platform object.
   * 
   * @return Long platformId.
   */
  public Long getPlatformId();

  /**
   * Sets the platformId of this Platform object.
   * 
   * @param platformId
   *          platformId.
   */
  public void setPlatformId(Long platformId);

  /**
   * Returns the platformType of this Platform object.
   * 
   * @return PlatformType platformType.
   */
  public PlatformType getPlatformType();

  /**
   * Sets the platformType of this Platform object.
   * 
   * @param name
   *          platformType.
   */
  public void setPlatformType(PlatformType name);

  /**
   * Returns the description of this Platform object.
   * 
   * @return String description.
   */
  public String getDescription();

  /**
   * Sets the description of this Platform object.
   * 
   * @param description
   *          description.
   */
  public void setDescription(String description);

  /**
   * Returns the instrumentModel of this Platform object.
   * 
   * @return String instrumentModel.
   */
  public String getInstrumentModel();

  /**
   * Sets the instrumentModel of this Platform object.
   * 
   * @param instrumentModel
   *          instrumentModel.
   */
  public void setInstrumentModel(String instrumentModel);

  /**
   * Returns the concatenation of the name and instrument model of this Platform object.
   * 
   * @return String nameAndModel.
   */
  public String getNameAndModel();

  /**
   * Returns the number of sequencer partition containers of this Platform object.
   * 
   * @return Integer numContainers.
   */
  public Integer getNumContainers();

  /**
   * Sets the number of sequencer partition containers of this Platform object.
   * 
   * @param numContainers
   *          numContainers.
   * 
   */
  public void setNumContainers(Integer numContainers);
}
