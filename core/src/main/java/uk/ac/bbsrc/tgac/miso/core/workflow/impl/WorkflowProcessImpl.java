package uk.ac.bbsrc.tgac.miso.core.workflow.impl;

import com.eaglegenomics.simlims.core.User;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.event.AttachmentNotificationCallback;
import uk.ac.bbsrc.tgac.miso.core.exception.AttachmentException;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcess;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation of a WorkflowProcess
 *
 * @author Rob Davey
 * @date 26/02/14
 * @since 0.2.1
 */
public class WorkflowProcessImpl implements WorkflowProcess {
  private long workflowProcessId;
  private Date startDate;
  private Date completionDate;
  private JSONObject state = new JSONObject();

  private WorkflowProcessDefinition workflowProcessDefinition;
  private User assignee;

  private Set<Nameable> attachedEntities = new HashSet<>();

  @Enumerated(EnumType.STRING)
  private HealthType health = HealthType.Unknown;

  public static final Long UNSAVED_ID = 0L;

  public WorkflowProcessImpl(WorkflowProcessDefinition workflowProcessDefinition) {
    this.workflowProcessDefinition = workflowProcessDefinition;
    for (String field : workflowProcessDefinition.getStateFields()) {
      state.put(field, "");
    }
  }

  public WorkflowProcessImpl(WorkflowProcessDefinition workflowProcessDefinition, JSONObject state) {
    this.workflowProcessDefinition = workflowProcessDefinition;
    this.state = state;
  }

  @Override
  public long getId() {
    return workflowProcessId;
  }

  @Override
  public void setId(long workflowProcessId) {
    this.workflowProcessId = workflowProcessId;
  }

  @Override
  public String getName() {
    return getAssignableIdentifier();
  }

  @Override
  public void setName(String name) {
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
    this.state = state;
  }

  @Override
  public HealthType getStatus() {
    return health;
  }

  @Override
  public void setStatus(HealthType status) {
    this.health = status;
  }

  @Override
  public WorkflowProcessDefinition getDefinition() {
    return workflowProcessDefinition;
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
  public User getAssignee() {
    return assignee;
  }

  @Override
  public void setAssignee(User assignee) {
    this.assignee = assignee;
  }

  @Override
  public String getAssignableIdentifier() {
    return "WKP"+getId();
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
