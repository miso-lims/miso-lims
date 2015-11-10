package uk.ac.bbsrc.tgac.miso.core.workflow;

import java.util.Set;

/**
 * Interface to allow objects to be assigned to a workflow instance
 *
 * @author Rob Davey
 * @date 15/08/14
 * @since 0.2.1-SNAPSHOT
 */
public interface WorkflowAware {
  public void linkToWorkflow(Workflow workflow);
  public Set<Workflow> getWorkflows();
}
