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
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * A Partition represents a compartment on a {@link SequencerPartitionContainer} on a sequencing platform, e.g. a lane on Illumina, a
 * chamber on 454, or a SMRT cell on a PacBio.
 * 
 * A Partition has a unique ID as well as a number describing its relative position in a SequencerPartitionContainer.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties({ "securityProfile", "submissionDocument" })
public interface Partition extends SecurableByProfile, Comparable {
  /**
   * Returns the sequencerPartitionContainer of this Partition object.
   * 
   * @return SequencerPartitionContainer sequencerPartitionContainer.
   */
  SequencerPartitionContainer getSequencerPartitionContainer();

  /**
   * Sets the sequencerPartitionContainer of this Partition object.
   * 
   * @param sequencerPartitionContainer
   *          sequencerPartitionContainer.
   */
  void setSequencerPartitionContainer(SequencerPartitionContainer sequencerPartitionContainer);

  /**
   * Returns the id of this Partition object.
   * 
   * @return Long id.
   */
  long getId();

  /**
   * Sets the id of this Partition object.
   * 
   * @param id
   *          id.
   */
  void setId(long id);

  /**
   * Returns the partitionNumber, relative to the parent SequencerPartitionContainer, of this Partition object.
   * 
   * @return Integer partitionNumber.
   */
  Integer getPartitionNumber();

  /**
   * Sets the partitionNumber, relative to the parent SequencerPartitionContainer, of this Partition object.
   * 
   * @param partitionNumber
   *          partitionNumber.
   */
  void setPartitionNumber(Integer partitionNumber);
}
