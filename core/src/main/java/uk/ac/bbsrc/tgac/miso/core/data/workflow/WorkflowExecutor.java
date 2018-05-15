package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;

/**
 * Responsible for supporting all of the necessary methods a Workflow will need to execute itself.
 * Delegates all execution to appropriate services.
 */
public interface WorkflowExecutor {
  /**
   * Creates or updates a Pool
   * 
   * @param pool
   * @return the updated Pool
   * @throws IOException
   */
  Pool save(Pool pool) throws IOException;

  /**
   * Creates or updates a SequencerPartitionContainer
   * 
   * @param spc
   * @return the updated SequencerPartitionContainer
   * @throws IOException
   */
  SequencerPartitionContainer save(SequencerPartitionContainer spc) throws IOException;
}
