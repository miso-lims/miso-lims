package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;

public class WorkflowStepPrompt {
  private Set<InputType> inputTypes;
  private String message;

  public WorkflowStepPrompt(Set<InputType> inputTypes, String message) {
    this.inputTypes = inputTypes;
    this.message = message;
  }

  public Set<InputType> getInputTypes() {
    return inputTypes;
  }

  public void setInputTypes(Set<InputType> inputTypes) {
    this.inputTypes = inputTypes;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
