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

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;

/**
 * Defines a DAO interface for storing Partitions
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface PartitionStore extends Store<SequencerPoolPartition>, Cascadable, Remover<SequencerPoolPartition> {
  /**
   * List all Partitions that are part of a Run given a Run ID
   * 
   * @param runId
   *          of type long
   * @return Collection<Partition>
   * @throws java.io.IOException
   *           when
   */
  Collection<SequencerPoolPartition> listByRunId(long runId) throws IOException;

  /**
   * List all Partitions on a parent SequencerPartitionContainer given a SequencerPartitionContainer ID
   * 
   * @param sequencerPartitionContainerId
   *          of type long
   * @return Collection<Partition>
   * @throws java.io.IOException
   *           when
   */
  Collection<SequencerPoolPartition> listBySequencerPartitionContainerId(long sequencerPartitionContainerId) throws IOException;

  /**
   * List all Partitions that hold an Pool given a Pool ID
   * 
   * @param poolId
   *          of type long
   * @return Collection<Partition>
   * @throws java.io.IOException
   *           when
   */
  Collection<SequencerPoolPartition> listByPoolId(long poolId) throws IOException;

  /**
   * List all Partitions that are part of a Submission given a Submission ID
   * 
   * @param submissionId
   *          of type long
   * @return Collection<Partition>
   * @throws java.io.IOException
   *           when
   */
  Collection<SequencerPoolPartition> listBySubmissionId(long submissionId) throws IOException;
}
