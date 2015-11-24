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

package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

/**
 * Defines a DAO interface for storing SequencerPartitionContainers
 * 
 * @author Rob Davey
 * @since 0.1.6
 */
public interface SequencerPartitionContainerStore extends Store<SequencerPartitionContainer<SequencerPoolPartition>>,
    NamingSchemeAware<SequencerPartitionContainer<SequencerPoolPartition>>, Remover<SequencerPartitionContainer<SequencerPoolPartition>> {
  /**
   * Get the SequencerPartitionContainer that contains a given {@link SequencerPoolPartition}
   * 
   * @param partitionId
   *          of type long
   * @return SequencerPartitionContainer
   * @throws java.io.IOException
   *           when
   */
  SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainerByPartitionId(long partitionId) throws IOException;

  /**
   * List all SequencerPartitionContainers given a parent Run ID
   * 
   * @param runId
   *          of type long
   * @return List<SequencerPartitionContainer>
   * @throws java.io.IOException
   *           when
   */
  List<SequencerPartitionContainer<SequencerPoolPartition>> listAllSequencerPartitionContainersByRunId(long runId) throws IOException;

  /**
   * List all SequencerPartitionContainers given an ID barcode
   * 
   * @param barcode
   *          of type String
   * @return List<SequencerPartitionContainer>
   * @throws java.io.IOException
   *           when
   */
  List<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByBarcode(String barcode) throws IOException;

  /**
   * List all SequencerPoolPartitions that are contained by a given {@link SequencerPartitionContainer}
   * 
   * @param sequencerPartitionContainerId
   *          of type long
   * @return Collection<? extends SequencerPoolPartition>
   * @throws java.io.IOException
   *           when
   */
  Collection<? extends SequencerPoolPartition> listPartitionsByContainerId(long sequencerPartitionContainerId) throws IOException;
}
