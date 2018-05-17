package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;

/**
 * Represents a user's position in a Workflow
 */
public class WorkflowStateDto {
  private String name;
  private long workflowId;
  private Integer stepNumber;
  private boolean complete;
  private String message;
  private Set<InputType> inputTypes;
  private List<String> log;
  private String lastModified;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }
}
