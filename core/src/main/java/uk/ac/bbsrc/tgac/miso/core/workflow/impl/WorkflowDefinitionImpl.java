package uk.ac.bbsrc.tgac.miso.core.workflow.impl;

import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;

import java.util.*;

/**
 * Default implementation of a WorkflowDefinition
 *
 * @author Rob Davey
 * @date 26/02/14
 * @since 0.2.1
 */
public class WorkflowDefinitionImpl extends AbstractWorkflowDefinition {
  public WorkflowDefinitionImpl(SortedMap<Integer, WorkflowProcessDefinition> workflowProcessDefinitions) {
    super(workflowProcessDefinitions);
  }

  @Override
  public void setWorkflowProcessDefinitions(SortedMap<Integer, WorkflowProcessDefinition> processMap) {
    throw new UnsupportedOperationException("Cannot modify existing process definitions");
  }
}