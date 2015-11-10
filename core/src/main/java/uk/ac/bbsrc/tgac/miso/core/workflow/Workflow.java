package uk.ac.bbsrc.tgac.miso.core.workflow;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Assignable;
import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

import java.util.Date;
import java.util.List;

/**
 * Interface defining the concept of a workflow, comprising an ordered number of WorkflowProcess objects, with
 * definitions for both the workflow and processes representing the "class" and implementations of these representing actual
 * instances.
 *
 * @author Rob Davey
 * @date 15/03/13
 * @since 0.2.0
 */
public interface Workflow extends Assignable, Nameable, Attachable<Nameable> {
  public void setId(long workflowId);
  public void setName(String name);

  public String getAlias();
  public void setAlias(String alias);

  public Date getStartDate();
  public void setStartDate(Date startDate);

  public Date getCompletionDate();
  public void setCompletionDate(Date completionDate);

  public JSONObject getState();
  public void setState(JSONObject state);

  public boolean isStarted();
  public boolean isPaused();
  public boolean isCompleted();
  public boolean isFailed();

  public HealthType getStatus();
  public void setStatus(HealthType status);

  public WorkflowDefinition getWorkflowDefinition();

  public List<WorkflowProcess> getWorkflowProcesses();
  public void setWorkflowProcesses(List<WorkflowProcess> workflowProcesses);

  public WorkflowProcessDefinition peekPreviousProcess();
  public WorkflowProcessDefinition peekNextProcess();

  public WorkflowProcess getCurrentProcess();
  public WorkflowProcess advanceWorkflow();
  public WorkflowProcess retractWorkflow(String reason);
}