package uk.ac.bbsrc.tgac.miso.core.workflow;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Assignable;
import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

import java.util.Date;

/**
 * Interface representing a process that is part of a parent linear workflow. Non-linearity can be achieved through the
 * Attachable interface. Workflow processes and workflows are Attachable, which means workflows can be attached to other
 * workflow processes.
 *

 *
 * @author Rob Davey
 * @date 15/03/13
 * @since 0.2.0
 */
public interface WorkflowProcess extends Assignable, Attachable<Nameable> {
  public void setId(long workflowProcessId);
  public void setName(String name);

  public Date getStartDate();
  public void setStartDate(Date startDate);

  public Date getCompletionDate();
  public void setCompletionDate(Date completionDate);

  public JSONObject getState();
  public void setState(JSONObject state);

  public HealthType getStatus();
  public void setStatus(HealthType status);

  public WorkflowProcessDefinition getDefinition();

  public boolean isStarted();
  public boolean isPaused();
  public boolean isCompleted();
  public boolean isFailed();
}
