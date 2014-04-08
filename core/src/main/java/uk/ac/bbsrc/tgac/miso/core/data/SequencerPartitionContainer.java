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
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

import java.util.List;

/**
 * A SequencerPartitionContainer describes a collection of {@link Partition} objects that can be used as part of a
 * sequencer {@link Run}.
 *
 * @author Rob Davey
 * @date 14/05/12
 * @since 0.1.6
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
@JsonIgnoreProperties({"securityProfile", "run"})
public interface SequencerPartitionContainer<T extends Partition> extends SecurableByProfile, Deletable, Comparable, Barcodable, Locatable {
  /**
   * Returns the containerId of this Container object.
   *
   * @return Long containerId.
   */
  @Deprecated
  Long getContainerId();

  /**
   * Sets the containerId of this Container object.
   *
   * @param containerId the id of this Container object
   *
   */
  @Deprecated
  void setContainerId(Long containerId);

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
   * Get the list of {@link Partition} objects comprising this container
   *
   * @return List<Partition> partitions
   */
  List<T> getPartitions();

  /**
   * Set the list of {@link Partition} objects comprising this container
   *
   * @param partitions List<Partition>
   */
  void setPartitions(List<T> partitions);

  /**
   * Get a {@link Partition} at a given relative partition number index (base-1)
   *
   * @param partitionNumber
   * @return the {@link Partition} at the given index
   */
  T getPartitionAt(int partitionNumber);

  /**
   * Set the number of partitions that this container can hold
   *
   * @param partitionLimit
   */
  void setPartitionLimit(int partitionLimit);

  /**
   * Initialise this container with empty {@link Partition} objects of type T up to the specified partition limit
   */
  void initEmptyPartitions();

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
}
