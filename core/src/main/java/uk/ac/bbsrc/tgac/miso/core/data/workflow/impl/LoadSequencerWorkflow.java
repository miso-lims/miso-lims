package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName.LOAD_SEQUENCER;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractWorkflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;

public class LoadSequencerWorkflow extends AbstractWorkflow {
  private StartStep startStep = new StartStep();

  @Override
  protected List<WorkflowStep> getCompletedSteps() {
    return Collections.emptyList();
  }

  @Override
  protected WorkflowName getWorkflowName() {
    return LOAD_SEQUENCER;
  }

  @Override
  public WorkflowStepPrompt getStep(int stepNumber) {
    if (stepNumber == 0) return new WorkflowStepPrompt(Sets.newHashSet(InputType.SEQUENCER_PARTITION_CONTAINER, InputType.STRING),
        "Scan a flow cell serial number");
    throw new IllegalArgumentException("Invalid step number");
  }

  @Override
  public boolean isComplete() {
    return false;
  }

  @Override
  public void processInput(int stepNumber, ProgressStep step) {
    throw new IllegalArgumentException();
  }

  @Override
  public void cancelInput() {
  }

  @Override
  public String getConfirmMessage() {
    return null;
  }

  @Override
  public void execute(WorkflowExecutor workflowExecutor) throws IOException {
  }

  @Override
  public String getName() {
    return "Load Sequencer Workflow";
  }

  private enum State {
    START, RECEIVED_SPC, RECEIVED_SC_MODEL
  }

  private class StartStep implements WorkflowStep {
    private StringProgressStep stringStep;
    private SequencerPartitionContainerProgressStep spcStep;

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.SEQUENCER_PARTITION_CONTAINER, InputType.STRING),
          "Scan a flow cell serial number");
    }

    @Override
    public ProgressStep getProgressStep() {
      return spcStep != null ? spcStep : stringStep;
    }

    @Override
    public void cancelInput() {
      spcStep = null;
      stringStep = null;
    }

    @Override
    public String getLogMessage() {
      return spcStep != null ? String.format("Scanned existing Sequencing Container %s", spcStep.getInput().getIdentificationBarcode())
          : String.format("Scanned new Sequencing Container %s", stringStep.getInput());
    }

    @Override
    public void processInput(SequencerPartitionContainerProgressStep step) {
      spcStep = step;
    }

    @Override
    public void processInput(StringProgressStep step) {
      stringStep = step;
    }
  }
}
