package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractWorkflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;

/**
 * Consists of a step that requires an Integer and a second step that requires a Pool. If previous steps are reprocessed, following steps
 * remain valid.
 */
public class TestWorkflow extends AbstractWorkflow {

  private final IntegerWorkflowStep step0 = new IntegerWorkflowStep("Input a concentration as an integer.");
  private final PoolWorkflowStep step1 = new PoolWorkflowStep("Scan a Pool to modify its concentration.");
  private final List<WorkflowStep> steps = Arrays.asList(step0, step1);

  private int nextStepNumber = 0;

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
    return steps.stream().map(WorkflowStep::getProgressStep).noneMatch(Objects::isNull);
  }

  @Override
  public List<String> processInput(int stepNumber, ProgressStep progressStep) {
    if (!validStepNumber(stepNumber)) throw new IllegalArgumentException("Invalid step number");

    progressStep.accept(steps.get(stepNumber));
    if (stepNumber == nextStepNumber) nextStepNumber++;

    return Collections.emptyList();
  }

  @Override
  public void cancelInput() {
    if (currentStepNumber() >= 0) {
      steps.get(currentStepNumber()).cancelInput();
      nextStepNumber--;
    }
  }

  @Override
  public String getConfirmMessage() {
    return String.format("Pool %s (%s) will be modified to have concentration %d.", step1.getInput().getAlias(), step1.getInput().getName(),
        step0.getInput());
  }

  @Override
  public void execute(WorkflowExecutor workflowExecutor) throws IOException {
    if (!isComplete()) throw new IllegalStateException("Workflow is not complete");

    Pool pool = step1.getInput();
    pool.setConcentration(new BigDecimal(step0.getInput()));

    workflowExecutor.update(pool);
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
    return null;
  }

  private static class PoolWorkflowStep implements WorkflowStep {
    private final String message;
    private PoolProgressStep poolProgressStep;

    PoolWorkflowStep(String message) {
      this.message = message;
    }

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Collections.singleton(InputType.POOL), message);
    }

    @Override
    public String getLogMessage() {
      Pool pool = poolProgressStep.getInput();
      return String.format("Selected Pool %s (%s)", pool.getAlias(), pool.getName());
    }

    @Override
    public ProgressStep getProgressStep() {
      return poolProgressStep;
    }

    Pool getInput() {
      return poolProgressStep.getInput();
    }

    @Override
    public void processInput(PoolProgressStep step) {
      this.poolProgressStep = step;
    }

    @Override
    public void cancelInput() {
      this.poolProgressStep = null;
    }
  }

  private static class IntegerWorkflowStep implements WorkflowStep {
    private final String message;
    private IntegerProgressStep integerProgressStep;

    IntegerWorkflowStep(String message) {
      this.message = message;
    }

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.INTEGER), message);
    }

    @Override
    public String getLogMessage() {
      return String.format("Entered concentration value: %d", integerProgressStep.getInput());
    }

    @Override
    public ProgressStep getProgressStep() {
      return integerProgressStep;
    }

    int getInput() {
      return integerProgressStep.getInput();
    }

    @Override
    public void processInput(IntegerProgressStep step) {
      this.integerProgressStep = step;
    }

    @Override
    public void cancelInput() {
      this.integerProgressStep = null;
    }
  }
}
