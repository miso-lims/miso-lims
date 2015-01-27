package uk.ac.bbsrc.tgac.miso.core.store;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.workflow.Workflow;

import java.io.IOException;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: bianx
 * Date: 05/12/2013
 * Time: 15:15
 */
public interface WorkflowStore extends Store<Workflow> {
  Collection<Workflow> listAllByAssignee(long userId) throws IOException;
  Collection<Workflow> listAllIncomplete() throws IOException;
  Collection<Workflow> listAllByStatus(HealthType healthType) throws IOException;
}
