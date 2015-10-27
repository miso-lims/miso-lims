package uk.ac.bbsrc.tgac.miso.core.workflow;

import java.util.List;

/**
 * uk.ac.bbsrc.tgac.miso.core.workflow
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 15/03/13
 * @since 0.2.0
 */
public interface WorkflowDefinition {
  public String getName();

  public List<WorkflowProcess> getWorkflowProcesses();
}
