package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractWorkflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;

/**
 * Consists of a step that requires an Integer and a second step that requires a Pool. If previous steps are reprocessed, following steps
 * remain valid.
 */
public class TestWorkflow extends AbstractWorkflow {
  // Use null for WorkflowName since we can't create an Enum value for a test workflow
  private static final WorkflowName WORKFLOW_NAME = null;

  private List<WorkflowStep> steps = Arrays.asList(new IntegerWorkflowStep("Input an integer."), new PoolWorkflowStep("Input a pool."));
  private int nextStepNumber = 0;

  @Override
  public WorkflowStepPrompt getNextStep() {
    return getStep(nextStepNumber);
  }

  @Override
  public WorkflowStepPrompt getStep(int stepNumber) {
    if (!validStepNumber(stepNumber)) throw new IllegalArgumentException("Invalid step number");

    return steps.get(stepNumber).getPrompt();
  }

  private boolean validStepNumber(int stepNumber) {
    return isExistingStepNumber(stepNumber) || (stepNumber == nextStepNumber && stepNumber <= 1);
  }

  private boolean isExistingStepNumber(int stepNumber) {
    return 0 <= stepNumber && stepNumber <= currentStepNumber();
  }

  @Override
  public boolean isComplete() {
    return steps.get(0).getProgressStep() != null && steps.get(1).getProgressStep() != null;
  }

  @Override
  public void processInput(ProgressStep step) {
    processInput(nextStepNumber, step);
  }

  @Override
  public void processInput(int stepNumber, ProgressStep step) {
    if (!validStepNumber(stepNumber)) throw new IllegalArgumentException("Invalid step number");

    step.accept(steps.get(stepNumber));
    if (stepNumber == nextStepNumber) nextStepNumber++;
  }

  @Override
  public void cancelInput() {
    if (currentStepNumber() >= 0) {
      steps.get(currentStepNumber()).cancelInput();
      nextStepNumber--;
    }
  }

  private int currentStepNumber() {
    return nextStepNumber - 1;
  }

  @Override
  protected List<WorkflowStep> getCompletedSteps() {
    return steps.subList(0, nextStepNumber);
  }

  @Override
  protected WorkflowName getWorkflowName() {
    return WORKFLOW_NAME;
  }

  private static class PoolWorkflowStep implements WorkflowStep {
    private final String message;
    private PoolProgressStep progressStep;

    PoolWorkflowStep(String message) {
      this.message = message;
    }

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Collections.singleton(InputType.POOL), message);
    }

    @Override
    public String getLogMessage() {
      return String.format("Processed Pool with id %d", progressStep.getInput().getId());
    }

    @Override
    public ProgressStep getProgressStep() {
      return progressStep;
    }

    @Override
    public void processInput(PoolProgressStep step) {
      this.progressStep = step;
    }

    @Override
    public void cancelInput() {
      this.progressStep = null;
    }
  }

  private static class IntegerWorkflowStep implements WorkflowStep {
    private final String message;
    private IntegerProgressStep progressStep;

    IntegerWorkflowStep(String message) {
      this.message = message;
    }

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.INTEGER), message);
    }

    @Override
    public String getLogMessage() {
      return String.format("Processed integer %d", progressStep.getInput());
    }

    @Override
    public ProgressStep getProgressStep() {
      return progressStep;
    }

    @Override
    public void processInput(IntegerProgressStep step) {
      this.progressStep = step;
    }

    @Override
    public void cancelInput() {
      this.progressStep = null;
    }
  }
}
