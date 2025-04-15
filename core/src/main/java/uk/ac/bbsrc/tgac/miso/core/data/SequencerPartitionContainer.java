package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControllable;

/**
 * A SequencerPartitionContainer describes a collection of {@link Partition} objects that can be
 * used as part of a sequencer {@link Run}.
 * 
 * @author Rob Davey
 * @date 14/05/12
 * @since 0.1.6
 */
public interface SequencerPartitionContainer
    extends Comparable<SequencerPartitionContainer>, Barcodable, Serializable, QualityControllable<ContainerQC>,
    Deletable {

  public SequencingParameters getSequencingParameters();

  public void setSequencingParameters(SequencingParameters sequencingParameters);

  public String getDescription();

  public void setDescription(String description);

  public Set<RunPosition> getRunPositions();

  /**
   * Returns the {@link Run} with a) the latest start date (of the runs which have a known status), or
   * b) the last modified date
   * 
   * @return Run run
   */
  public Run getLastRun();

  /**
   * Get the list of {@link Partition} objects comprising this container
   * 
   * @return List<Partition> partitions
   */
  public List<Partition> getPartitions();

  /**
   * Set the list of {@link Partition} objects comprising this container
   * 
   * @param partitions List<Partition>
   */
  public void setPartitions(List<Partition> partitions);

  /**
   * Get a {@link Partition} at a given relative partition number index (base-1)
   * 
   * @param partitionNumber
   * @return the {@link Partition} at the given index
   */
  public Partition getPartitionAt(int partitionNumber);

  /**
   * Set the number of partitions that this container can hold
   * 
   * @param partitionLimit
   */
  public void setPartitionLimit(int partitionLimit);

  public void setClusteringKit(KitDescriptor clusteringKit);

  public KitDescriptor getClusteringKit();

  public String getClusteringKitLot();

  public void setClusteringKitLot(String clusteringKitLot);

  public KitDescriptor getMultiplexingKit();

  public void setMultiplexingKit(KitDescriptor multiplexingKit);

  public String getMultiplexingKitLot();

  public void setMultiplexingKitLot(String multiplexingKitLot);

  public SequencingContainerModel getModel();

  public void setModel(SequencingContainerModel model);

  @Override
  public int hashCode();

  @Override
  public boolean equals(Object obj);

}
