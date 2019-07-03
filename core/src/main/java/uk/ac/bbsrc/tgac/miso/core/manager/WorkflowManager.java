package uk.ac.bbsrc.tgac.miso.core.manager;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;

public interface WorkflowManager {
  /**
   * Create an empty workflow and persist it.
   * @param workflowNameString String representation of a WorkflowName
   */
  Workflow beginWorkflow(String workflowNameString) throws IOException;

  /**
   * Interpret input as part of the step identified by stepNumber.
   * Delegate input processing to Workflow.
   * Persist the updated Workflow.
   * @param input user input
   * @param stepNumber 0-based index into workflow steps
   * @return updated Workflow
   */
  Workflow processInput(Workflow workflow, int stepNumber, String input) throws IOException;

  /**
   * Remove the latest step from workflow and any effects it caused.
   * Persist the updated Workflow.
   * @return updated Workflow
   */
  Workflow cancelInput(Workflow workflow) throws IOException;

  /**
   * @return null if Workflow does not exist
   */
  Workflow loadWorkflow(long id) throws IOException;

  /**
   * @return Workflows owned by the current User
   */
  List<Workflow> listUserWorkflows() throws IOException;

  /**
   * Make all changes associated with workflow.
   * After completion, this Workflow will be deleted.
   */
  void execute(Workflow workflow) throws IOException;
}
