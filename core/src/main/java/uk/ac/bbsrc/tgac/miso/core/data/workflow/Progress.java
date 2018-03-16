package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.SortedSet;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;

/**
 * Models an in-progress workflow
 */
public interface Progress extends Identifiable, Serializable {
  void setId(long id);

  WorkflowName getWorkflowName();

  void setWorkflowName(WorkflowName workflowName);

  User getUser();

  void setUser(User user);

  Date getCreationTime();

  void setCreationTime(Date creationTime);

  Date getLastModified();

  void setLastModified(Date lastModified);

  SortedSet<ProgressStep> getSteps();

  void setSteps(Collection<ProgressStep> steps);
}
