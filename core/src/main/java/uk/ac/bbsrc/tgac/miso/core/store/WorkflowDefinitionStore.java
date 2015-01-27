package uk.ac.bbsrc.tgac.miso.core.store;

import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowDefinition;

import java.io.IOException;
import java.util.Collection;

/**
 */
public interface WorkflowDefinitionStore extends Store<WorkflowDefinition> {
  Collection<WorkflowDefinition> listAllByCreator(long userId) throws IOException;
  Collection<WorkflowDefinition> listBySearch(String searchStr) throws IOException;
}
