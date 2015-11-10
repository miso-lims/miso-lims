package uk.ac.bbsrc.tgac.miso.core.store;

import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcess;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: davey
 * Date: 12/05/2014
 */
public interface WorkflowProcessStore extends Store<WorkflowProcess> {
  List<WorkflowProcess> getWorkflowProcessesByWorkflowId(long workflowId);
  Collection<WorkflowProcess> listAllByAssignee(long userId) throws IOException;
}
