package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;

/**
 * Responsible for supporting all of the necessary methods a Workflow will need to execute itself.
 * Delegates all execution to appropriate services.
 */
public interface WorkflowExecutor {
  /**
   * Create or update a pool in the database
   */
  Pool save(Pool pool) throws IOException;
}
