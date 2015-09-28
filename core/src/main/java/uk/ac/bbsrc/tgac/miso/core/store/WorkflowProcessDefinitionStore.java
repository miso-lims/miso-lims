package uk.ac.bbsrc.tgac.miso.core.store;

import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

/**
 */
public interface WorkflowProcessDefinitionStore extends Store<WorkflowProcessDefinition> {
  Collection<WorkflowProcessDefinition> listAllByCreator(long userId) throws IOException;
  Collection<WorkflowProcessDefinition> listBySearch(String searchStr) throws IOException;
  SortedMap<Integer, WorkflowProcessDefinition> getWorkflowProcessDefinitionsByWorkflowDefinition(long workflowDefinitionId) throws IOException;
}
