/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * A SequencerPartitionContainer describes a collection of {@link Partition} objects that can be used as part of a sequencer {@link Run}.
 * 
 * @author Rob Davey
 * @date 14/05/12
 * @since 0.1.6
 */
public interface SequencerPartitionContainer
    extends SecurableByProfile, Comparable<SequencerPartitionContainer>, Barcodable, ChangeLoggable, Serializable,
    QualityControllable<ContainerQC>, Deletable {

  public void setId(long id);

  String getDescription();

  void setDescription(String description);

  /**
   * Returns the runs of this Container object.
   * 
   * @return Collection<Run> run.
   */
  Collection<Run> getRuns();

  /**
   * Returns the {@link Run} with
   * a) the latest start date (of the runs which have a known status), or
   * b) the last modified date
   * 
   * @return Run run
   */
  Run getLastRun();

  /**
   * Get the list of {@link Partition} objects comprising this container
   * 
   * @return List<Partition> partitions
   */
  List<Partition> getPartitions();

  /**
   * Set the list of {@link Partition} objects comprising this container
   * 
   * @param partitions List<Partition>
   */
  void setPartitions(List<Partition> partitions);

  /**
   * Get a {@link Partition} at a given relative partition number index (base-1)
   * 
   * @param partitionNumber
   * @return the {@link Partition} at the given index
   */
  Partition getPartitionAt(int partitionNumber);

  /**
   * Set the number of partitions that this container can hold
   * 
   * @param partitionLimit
   */
  void setPartitionLimit(int partitionLimit);

  void setClusteringKit(KitDescriptor clusteringKit);

  KitDescriptor getClusteringKit();

  KitDescriptor getMultiplexingKit();

  void setMultiplexingKit(KitDescriptor multiplexingKit);

  public SequencingContainerModel getModel();

  public void setModel(SequencingContainerModel model);

}