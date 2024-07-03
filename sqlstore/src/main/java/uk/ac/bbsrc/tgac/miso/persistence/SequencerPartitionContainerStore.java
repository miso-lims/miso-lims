package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoreVersion;

/**
 * Defines a DAO interface for storing SequencerPartitionContainers
 * 
 * @author Rob Davey
 * @since 0.1.6
 */
public interface SequencerPartitionContainerStore extends SaveDao<SequencerPartitionContainer> {

  /**
   * List all SequencerPartitionContainers given a parent Run ID
   * 
   * @param runId of type long
   * @return List<SequencerPartitionContainer>
   * @throws java.io.IOException
   */
  List<SequencerPartitionContainer> listAllSequencerPartitionContainersByRunId(long runId) throws IOException;

  /**
   * List all SequencerPartitionContainers given a parent Pool ID
   * 
   * @param poolId of type long
   * @return List<SequencerPartitionContainer>
   * @throws java.io.IOException
   */
  List<Partition> listAllPartitionsByPoolId(long poolId) throws IOException;

  /**
   * List all SequencerPartitionContainers given an ID barcode
   * 
   * @param barcode of type String
   * @return List<SequencerPartitionContainer>
   * @throws java.io.IOException
   */
  List<SequencerPartitionContainer> listSequencerPartitionContainersByBarcode(String barcode) throws IOException;

  Partition getPartitionById(long partitionId);

  public PoreVersion getPoreVersion(long id);

  public List<PoreVersion> listPoreVersions();

  public Long getPartitionIdByRunIdAndPartitionNumber(long runId, int partitionNumber) throws IOException;

}
