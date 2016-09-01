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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Lane;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * A SequencingContainer describes a collection of {@link Lane} objects that can be used as part of a sequencer {@link Run}.
 * 
 * @author Rob Davey
 * @date 14/05/12
 * @since 0.1.6
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties({ "securityProfile", "run" })
public interface SequencingContainer<T extends Lane> extends SecurableByProfile, Deletable, Comparable, Barcodable, Locatable {
  public void setId(long id);

  /**
   * Sets the name of this Container object.
   * 
   * @param name name.
   */
  public void setName(String name);

  /**
   * Returns the run of this Container object.
   * 
   * @return Run run.
   */
  Run getRun();

  /**
   * Sets the run of this Container object.
   * 
   * @param run The run of which this Container is a part.
   * 
   */
  void setRun(Run run);

  /**
   * Get the list of {@link Lane} objects comprising this container
   * 
   * @return List<Lane> lanes
   */
  List<T> getLanes();

  /**
   * Set the list of {@link Lane} objects comprising this container
   * 
   * @param lanes List<Lane>
   */
  void setLanes(List<T> lanes);

  /**
   * Get a {@link Lane} at a given relative lane number index (base-1)
   * 
   * @param laneNumber
   * @return the {@link Lane} at the given index
   */
  T getLaneAt(int laneNumber);

  /**
   * Set the number of lanes that this container can hold
   * 
   * @param laneLimit
   */
  void setLaneLimit(int laneLimit);

  /**
   * Initialise this container with empty {@link Lane} objects of type T up to the specified lane limit
   */
  void initEmptyLanes();

  /**
   * Returns the platform of this Container object.
   * 
   * @return Platform platform.
   */
  public Platform getPlatform();

  /**
   * Sets the platform of this Container object.
   * 
   * @param platform Platform.
   */
  public void setPlatform(Platform platform);

  /**
   * If this container has been validated by an external piece of equipment, retrieve this barcode string
   * 
   * @return String validationBarcode
   */
  public String getValidationBarcode();

  /**
   * If this container has been validated by an external piece of equipment, set the barcode string
   * 
   * @param validationBarcode
   */
  public void setValidationBarcode(String validationBarcode);

  /**
   * Add new lane
   * 
   * 
   */
  public void addNewLane();

  public User getLastModifier();

  public void setLastModifier(User lastModifier);

  public Collection<ChangeLog> getChangeLog();

  public Date getLastModified();

  public void setLastModified(Date lastModified);

}