package uk.ac.bbsrc.tgac.miso.core.workflow.impl;

import com.eaglegenomics.simlims.core.User;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.event.AttachmentNotificationCallback;
import uk.ac.bbsrc.tgac.miso.core.exception.AttachmentException;
import uk.ac.bbsrc.tgac.miso.core.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcess;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.*;

/**
 * Default implementation of a Workflow
 *
 * @author Rob Davey
 * @date 26/02/14
 * @since 0.2.1
 */
public class WorkflowImpl implements Workflow {
  private WorkflowDefinition workflowDefinition;
  private long workflowId;
  private String alias;
  private Date startDate;
  private Date completionDate;
  private JSONObject state = new JSONObject();

  private User assignee;

  private int processOrdinal = -1;

  @Enumerated(EnumType.STRING)
  private HealthType health = HealthType.Unknown;

  private List<WorkflowProcess> workflowProcesses = new ArrayList<>();
  private Set<Nameable> attachedEntities = new HashSet<>();

  public static final Long UNSAVED_ID = 0L;

  protected static final Logger log = LoggerFactory.getLogger(WorkflowImpl.class);

  public WorkflowImpl(WorkflowDefinition workflowDefinition) {
    this.workflowDefinition = workflowDefinition;
  }

  public WorkflowImpl(WorkflowDefinition workflowDefinition, JSONObject state) {
    this.workflowDefinition = workflowDefinition;
    if (state != null && state.has("currentProcess")) {
      setState(state);
      processOrdinal = getState().getInt("currentProcess");
    }
    else {
      setState(state);
    }
  }

  @Override
  public WorkflowDefinition getWorkflowDefinition() {
    return workflowDefinition;
  }

  @Override
  public long getId() {
    return workflowId;
  }

  @Override
  public void setId(long workflowId) {
    this.workflowId = workflowId;
  }

  @Override
  public void setName(String name) {
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public String getName() {
    return getAssignableIdentifier();
  }

  @Override
  public Date getStartDate() {
    return startDate;
  }

  @Override
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  @Override
  public Date getCompletionDate() {
    return completionDate;
  }

  @Override
  public void setCompletionDate(Date completionDate) {
    this.completionDate = completionDate;
  }

  @Override
  public JSONObject getState() {
    return state;
  }

  @Override
  public void setState(JSONObject state) {
    if (state == null || (!state.has("state") && state.size() != 1)) {
      JSONObject j = new JSONObject();
      j.put("state", state);
      this.state = j;
    }
    else {
      this.state = state;
    }
  }

  @Override
  public boolean isStarted() {
    return HealthType.Started.equals(health);
  }

  @Override
  public boolean isPaused() {
    return HealthType.Stopped.equals(health);
  }

  @Override
  public boolean isCompleted() {
    return HealthType.Completed.equals(health);
  }

  @Override
  public boolean isFailed() {
    return HealthType.Failed.equals(health);
  }

  @Override
  public HealthType getStatus() {
    return health;
  }

  @Override
  public void setStatus(HealthType health) {
    this.health = health;
    if (isCompleted()) {
      setCompletionDate(new Date());
    }
  }

  @Override
  public List<WorkflowProcess> getWorkflowProcesses() {
    List<WorkflowProcess> ips = new ArrayList<>();
    for (WorkflowProcess p : workflowProcesses) {
      ips.add(new ImmutableWorkflowProcessImpl(p));
    }
    return ips;
  }

  @Override
  public void setWorkflowProcesses(List<WorkflowProcess> workflowProcesses) {
    this.workflowProcesses= workflowProcesses;
  }

  @Override
  public WorkflowProcessDefinition peekPreviousProcess() {
    if (processOrdinal > 0) {
      return getWorkflowDefinition().getWorkflowProcessDefinitions().get(processOrdinal-1);
    }
    else {
      return null;
    }
  }

  @Override
  public WorkflowProcess getCurrentProcess() {
    if (processOrdinal != -1) {
      return workflowProcesses.get(processOrdinal);
    }
    else {
      return null;
    }
  }

  @Override
  public WorkflowProcessDefinition peekNextProcess() {
    if (workflowProcesses.isEmpty() && !workflowDefinition.getWorkflowProcessDefinitions().isEmpty()) {
      log.debug("No processes available but definitions present - building initial processes...");
      //build processes if none exist
      for (Integer i : workflowDefinition.getWorkflowProcessDefinitions().keySet()) {
        WorkflowProcessDefinition wpd = workflowDefinition.getWorkflowProcessDefinitions().get(i);
        //workflowProcesses.put(i, wpd.getInstance());
        workflowProcesses.add(wpd.getInstance());
        log.debug("Added process " + i + " [ " + wpd.getName() + " ]");
      }
    }

    if (processOrdinal+1 <= getWorkflowDefinition().getWorkflowProcessDefinitions().keySet().size()) {
      return getWorkflowDefinition().getWorkflowProcessDefinitions().get(processOrdinal+1);
    }
    else {
      return null;
    }
  }

  @Override
  public WorkflowProcess advanceWorkflow() {
    if (peekNextProcess() != null && !isPaused() && !isFailed()) {
      //if the next process is the first stage
      if (getWorkflowDefinition().getProcessStage(peekNextProcess()) == 1) {
        setStatus(HealthType.Started);
        setStartDate(new Date());
      }

      processOrdinal++;
      getState().put("currentProcess", processOrdinal);
      return workflowProcesses.get(processOrdinal);
    }
    else {
      return null;
    }
  }

  @Override
  public WorkflowProcess retractWorkflow(String reason) {
    if (peekPreviousProcess() != null && !isPaused()) {
      processOrdinal--;
      getState().put("currentProcess", processOrdinal);
      //TODO retraction object should be an array of reasons w/ users, the actual reason, timestamp, etc
      getState().put("retraction", reason);
      return workflowProcesses.get(processOrdinal);
    }
    else {
      reason = "Cannot revert workflow back a step.";
      return null;
    }
  }

  @Override
  public User getAssignee() {
    return assignee;
  }

  @Override
  public void setAssignee(User assignee) {
    this.assignee = assignee;
  }

  @Override
  public String getAssignableIdentifier() {
    return "WKF"+getId();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getId())
      .append(" : ")
      .append(this.getName())
      .append(" : ")
      .append(this.getStartDate())
      .append(" : ")
      .append(this.getStatus().getKey())
      .append(" : ");

    if (getCurrentProcess() != null) {
      sb.append(getWorkflowDefinition().getProcessStage(getCurrentProcess().getDefinition())+"/"+getWorkflowDefinition().getWorkflowProcessDefinitions().size());
    }
    else {
      sb.append(processOrdinal+"/"+getWorkflowDefinition().getWorkflowProcessDefinitions().size());
    }

    sb.append(" : definition [")
      .append(this.getWorkflowDefinition().toString())
      .append("] : ")
      .append(this.getAssignee().getFullName())
      .append(" : state [")
      .append(this.getState().toString())
      .append("]");
    return sb.toString();
  }

  @Override
  public void detach(Nameable obj) {
    attachedEntities.remove(obj);
  }

  @Override
  public void attach(Nameable obj) {
    attachedEntities.add(obj);
  }

  @Override
  public Set<Nameable> getAttached() {
    return attachedEntities;
  }

  @Override
  public void notifyAttached(AttachmentNotificationCallback<Nameable> anc) throws AttachmentException {
    for (Nameable n : attachedEntities) {
      anc.callback(this, n);
    }
  }
}
