package uk.ac.bbsrc.tgac.miso.core.workflow.impl;

import com.eaglegenomics.simlims.core.User;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.event.AttachmentNotificationCallback;
import uk.ac.bbsrc.tgac.miso.core.exception.AttachmentException;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcess;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * Immutable WorkflowProcess, for use in large-scale listing methods
 *
 * @author Rob Davey
 * @date 18/08/14
 * @since 0.2.1-SNAPSHOT
 */
public class ImmutableWorkflowProcessImpl implements WorkflowProcess {
  WorkflowProcess process;

  public ImmutableWorkflowProcessImpl(WorkflowProcess workflowProcess) {
    this.process = workflowProcess;
  }

  @Override
  public final long getId() {
    return process.getId();
  }

  @Override
  public final void setId(long workflowProcessId) {
  }

  @Override
  public String getName() {
    return process.getName();
  }

  @Override
  public final void setName(String name) {
  }

  @Override
  public final Date getStartDate() {
    return new Date(process.getStartDate().getTime());
  }

  @Override
  public final void setStartDate(Date startDate) {
  }

  @Override
  public final Date getCompletionDate() {
    return new Date(process.getCompletionDate().getTime());
  }

  @Override
  public final void setCompletionDate(Date completionDate) {
  }

  @Override
  public final JSONObject getState() {
    return JSONObject.fromObject(process.getState());
  }

  @Override
  public final void setState(JSONObject state) {
  }

  @Override
  public final HealthType getStatus() {
    return process.getStatus();
  }

  @Override
  public final void setStatus(HealthType status) {
  }

  @Override
  public final WorkflowProcessDefinition getDefinition() {
    return process.getDefinition();
  }

  @Override
  public final boolean isStarted() {
    return process.isStarted();
  }

  @Override
  public final boolean isPaused() {
    return process.isPaused();
  }

  @Override
  public final boolean isCompleted() {
    return process.isCompleted();
  }

  @Override
  public final boolean isFailed() {
    return process.isFailed();
  }

  @Override
  public final User getAssignee() {
    return process.getAssignee();
  }

  @Override
  public final void setAssignee(User assignee) {
  }

  @Override
  public final String getAssignableIdentifier() {
    return process.getAssignableIdentifier();
  }

  @Override
  public final void attach(Nameable obj) {
  }

  @Override
  public final Set<Nameable> getAttached() {
    return Collections.unmodifiableSet(process.getAttached());
  }

  @Override
  public final void notifyAttached(AttachmentNotificationCallback<Nameable> anc) throws AttachmentException {
    for (Nameable n : process.getAttached()) {
      anc.callback(this, n);
    }
  }
}