package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;

/**
 * Represents a user's position in a Workflow
 */
public class WorkflowStateDto {
  private long workflowId;
  private Integer stepNumber;
  private boolean complete;
  private String message;
  private Set<InputType> inputTypes;
  private List<String> log;

  /**
   * If the workflow is complete, represent a completed workflow.  Otherwise, set the user's position to the next stepNumber
   */
  public WorkflowStateDto(Workflow workflow) {
    this.workflowId = workflow.getProgress().getId();
    this.log = workflow.getLog();
    this.complete = workflow.isComplete();
    if (workflow.isComplete()) {
      this.message = workflow.getConfirmMessage();
    } else {
      this.stepNumber = workflow.getNextStepNumber();
      WorkflowStepPrompt prompt = workflow.getStep(stepNumber);
      this.message = prompt.getMessage();
      this.inputTypes = prompt.getInputTypes();
    }
  }

  /**
   * Represent a user's position in a Workflow at the specified stepNumber
   * @param stepNumber must refer to a previously completed step, or the next step
   */
  public WorkflowStateDto(Workflow workflow, int stepNumber) {
    this.workflowId = workflow.getProgress().getId();
    this.log = workflow.getLog();
    this.complete = workflow.isComplete();

    if (stepNumber >= log.size()) {
      if (workflow.isComplete()) {
        this.message = workflow.getConfirmMessage();
      } else {
        this.stepNumber = workflow.getNextStepNumber();
      }
    } else {
      this.stepNumber = stepNumber;
    }
    if (this.stepNumber != null) {
      WorkflowStepPrompt prompt = workflow.getStep(stepNumber);
      this.message = prompt.getMessage();
      this.inputTypes = prompt.getInputTypes();
    }
  }

  /**
   * Describes a message to display to the user. May be a user input prompt or workflow execution confirmation
   */
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * If null, indicates that the workflow is complete
   */
  public Set<InputType> getInputTypes() {
    return inputTypes;
  }

  public void setInputTypes(Set<InputType> inputTypes) {
    this.inputTypes = inputTypes;
  }

  public List<String> getLog() {
    return log;
  }

  public void setLog(List<String> log) {
    this.log = log;
  }

  public long getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(long workflowId) {
    this.workflowId = workflowId;
  }

  /**
   * 0-based index into completed steps. If null, indicates that the workflow is complete, and the user is not currently editing any steps
   */
  public Integer getStepNumber() {
    return stepNumber;
  }

  public void setStepNumber(Integer stepNumber) {
    this.stepNumber = stepNumber;
  }

  /**
   * @return whether the workflow is ready to execute
   */
  public boolean isComplete() {
    return complete;
  }

  public void setComplete(boolean complete) {
    this.complete = complete;
  }
}
