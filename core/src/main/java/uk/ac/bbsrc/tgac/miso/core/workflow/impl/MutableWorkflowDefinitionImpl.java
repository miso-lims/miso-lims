package uk.ac.bbsrc.tgac.miso.core.workflow.impl;

import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;

import java.util.SortedMap;

/**
 * uk.ac.bbsrc.tgac.miso.core.workflow.impl
 *
 * @author Rob Davey
 * @date 13/08/14
 * @since 0.2.1-SNAPSHOT
 */
public class MutableWorkflowDefinitionImpl extends AbstractWorkflowDefinition {
  public MutableWorkflowDefinitionImpl(WorkflowDefinition workflowDefinition) {
    super(workflowDefinition.getWorkflowProcessDefinitions());
    setId(workflowDefinition.getId());
    setName(workflowDefinition.getName());
    setDescription(workflowDefinition.getDescription());
    setCreator(workflowDefinition.getCreator());
    setCreationDate(workflowDefinition.getCreationDate());
    setStateFields(workflowDefinition.getStateFields());
  }

  @Override
  public void setWorkflowProcessDefinitions(SortedMap<Integer, WorkflowProcessDefinition> processMap) {
    workflowProcessDefinitions = processMap;
  }
}