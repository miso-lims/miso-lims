package uk.ac.bbsrc.tgac.miso.dto;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;

public class WorkflowNameDto {
  private WorkflowName workflowName;
  private String description;

  public WorkflowName getWorkflowName() {
    return workflowName;
  }

  public void setWorkflowName(WorkflowName workflowName) {
    this.workflowName = workflowName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
