package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;

public class WorkflowStepPrompt {
  private Set<InputType> dataTypes;
  private String message;

  public WorkflowStepPrompt(Set<InputType> dataTypes, String message) {
    this.dataTypes = dataTypes;
    this.message = message;
  }

  public Set<InputType> getDataTypes() {
    return dataTypes;
  }

  public void setDataTypes(Set<InputType> dataTypes) {
    this.dataTypes = dataTypes;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
