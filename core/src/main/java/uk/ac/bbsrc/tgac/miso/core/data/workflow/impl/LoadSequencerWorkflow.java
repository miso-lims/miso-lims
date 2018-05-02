package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName.LOAD_SEQUENCER;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractWorkflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;

public class LoadSequencerWorkflow extends AbstractWorkflow {
  @Override
  protected List<WorkflowStep> getCompletedSteps() {
    return Collections.emptyList();
  }

  @Override
  protected WorkflowName getWorkflowName() {
    return LOAD_SEQUENCER;
  }

  @Override
  public WorkflowStepPrompt getNextStep() {
    return null;
  }

  @Override
  public WorkflowStepPrompt getStep(int stepNumber) {
    return null;
  }

  @Override
  public boolean isComplete() {
    return false;
  }

  @Override
  public void processInput(ProgressStep step) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void processInput(int stepNumber, ProgressStep step) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void cancelInput() {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public String getConfirmMessage() {
    return null;
  }

  @Override
  public void execute(WorkflowExecutor workflowExecutor) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public String getName() {
    return "Load Sequencer Workflow";
  }
}
