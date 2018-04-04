package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName.LOAD_SEQUENCER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Sets;

import junit.framework.AssertionFailedError;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;

public class TestWorkflowTest {
  private static final int INT_1 = 3;
  private static final int INT_2 = 4;
  private static final String POOL_ALIAS = "pool_alias";
  private static final String POOL_NAME = "pool_name";

  private static final WorkflowName WORKFLOW_NAME = null;

  private Workflow workflow;

  @Test
  public void testCreateNewWorkflow() {
    assertNoInput(makeNewWorkflow());
  }

  @Test
  public void testSetNullProgress() {
    workflow = new TestWorkflow();
    assertThrows(IllegalArgumentException.class, () -> workflow.setProgress(null));
  }

  private void assertNoInput(Workflow workflow) {
    assertThrows(IllegalArgumentException.class, () -> workflow.processInput(2, makePoolStep(POOL_ALIAS, POOL_NAME)));

    assertEquivalent(makeProgress(WORKFLOW_NAME, new ProgressStep[] {}), workflow.getProgress());
    assertIntegerPrompt(workflow.getNextStep());
    assertIntegerPrompt(workflow.getStep(0));
    assertThrows(IllegalArgumentException.class, () -> workflow.getStep(1));
    assertFalse(workflow.isComplete());
    assertEquals(Collections.emptyList(), workflow.getLog());
  }

  private <T extends Throwable> void assertThrows(Class<T> expectedType, Runnable runnable) {
    try {
      runnable.run();
      throw new AssertionFailedError(String.format("Expected exception %s was not thrown", expectedType.toString()));
    } catch (Throwable actualException) {
      if (!expectedType.isInstance(actualException)) {
        throw new AssertionFailedError(String.format("Expected exception %s was not thrown", expectedType.toString()));
      }
    }
  }

  @Test
  public void testSetProgressTwiceThrowsError() {
    workflow = new TestWorkflow();
    workflow.setProgress(makeProgress(WORKFLOW_NAME));

    assertThrows(IllegalStateException.class, () -> workflow.setProgress(makeProgress(WORKFLOW_NAME)));
  }

  @Test
  public void testCreateNewWorkflowWithIncorrectWorkflowName() {
    workflow = new TestWorkflow();
    assertThrows(IllegalArgumentException.class, () -> workflow.setProgress(makeProgress(LOAD_SEQUENCER)));
  }

  @Test
  public void testSetProgressWithoutSteps() {
    workflow = new TestWorkflow();
    workflow.setProgress(makeProgress(WORKFLOW_NAME));
    assertNoInput(workflow);
  }

  @Test
  public void testProcessInvalidInput() {
    workflow = makeNewWorkflow();
    assertThrows(IllegalArgumentException.class, () -> workflow.processInput(makePoolStep(POOL_ALIAS, POOL_NAME)));
  }

  @Test
  public void testProcessValidInput() {
    workflow = makeNewWorkflow();
    workflow.processInput(makeIntegerStep(INT_1));
    assertReceivedOneInput(workflow, INT_1);
  }

  private void assertReceivedOneInput(Workflow workflow, int input) {
    assertThrows(IllegalArgumentException.class, () -> workflow.processInput(2, makePoolStep(POOL_ALIAS, POOL_NAME)));

    assertEquivalent(makeProgress(WORKFLOW_NAME, makeIntegerStep(input, 0)), workflow.getProgress());
    assertPoolPrompt(workflow.getNextStep());
    assertIntegerPrompt(workflow.getStep(0));
    assertPoolPrompt(workflow.getStep(1));
    assertThrows(IllegalArgumentException.class, () -> workflow.getStep(2));
    assertFalse(workflow.isComplete());
    assertEquals(Collections.singletonList(String.format("Entered concentration value: %d", input)), workflow.getLog());
  }

  @Test
  public void testProcessInputAtFirstStep() {
    workflow = makeNewWorkflow();
    workflow.processInput(0, makeIntegerStep(INT_1));
    assertReceivedOneInput(workflow, INT_1);
  }

  @Test
  public void testCancelInputWithoutInput() {
    makeNewWorkflow().cancelInput();
  }

  @Test
  public void loadExistingWorkflow() {
    assertReceivedOneInput(makeExistingWorkflow(makeIntegerStep(INT_1)), INT_1);
  }

  private Workflow makeExistingWorkflow(ProgressStep... steps) {
    Workflow workflow = new TestWorkflow();
    workflow.setProgress(makeProgress(WORKFLOW_NAME, steps));
    return workflow;
  }

  @Test
  public void testProcessInputAfterLoadingInput() {
    workflow = makeExistingWorkflow(makeIntegerStep(INT_1));
    workflow.processInput(makePoolStep(POOL_ALIAS, POOL_NAME));
    assertReceivedTwoInputs(workflow, INT_1, POOL_ALIAS, POOL_NAME);
  }

  private void assertReceivedTwoInputs(Workflow workflow, int intInput, String poolAlias, String poolName) {
    assertThrows(IllegalArgumentException.class, () -> workflow.processInput(2, makePoolStep(POOL_ALIAS, POOL_NAME)));

    assertEquivalent(makeProgress(WORKFLOW_NAME, makeIntegerStep(intInput, 0), makePoolStep(poolAlias, poolName, 1)), workflow.getProgress());
    assertThrows(IllegalArgumentException.class, workflow::getNextStep);
    assertIntegerPrompt(workflow.getStep(0));
    assertPoolPrompt(workflow.getStep(1));
    assertThrows(IllegalArgumentException.class, () -> workflow.getStep(2));
    assertTrue(workflow.isComplete());
    assertEquals(
        Arrays.asList(String.format("Entered concentration value: %d", intInput), String.format("Selected Pool %s (%s)", poolAlias, poolName)),
        workflow.getLog());
  }

  @Test
  public void testReprocessInputAfterLoadingInput() {
    workflow = makeExistingWorkflow(makeIntegerStep(INT_1));
    workflow.processInput(0, makeIntegerStep(INT_2));
    assertReceivedOneInput(workflow, INT_2);
  }

  @Test
  public void testCancelInputAfterLoadingInput() {
    workflow = makeExistingWorkflow(makeIntegerStep(INT_1));
    workflow.cancelInput();
    assertNoInput(workflow);
  }

  @Test
  public void testProcessInputAtPreviousStep() {
    workflow = makeExistingWorkflow(makeIntegerStep(INT_1), makePoolStep(POOL_ALIAS, POOL_NAME));
    workflow.processInput(0, makeIntegerStep(INT_2));
    assertReceivedOneInput(workflow, INT_2);
  }

  @Test
  public void testProcessFailedInputDoesNotChangeProgress() {
    workflow = makeExistingWorkflow(makeIntegerStep(INT_1));
    try {
      workflow.processInput(makeIntegerStep(INT_2));
    } catch (Exception ignored) {
    }
    assertReceivedOneInput(workflow, INT_1);
  }

  private Workflow makeNewWorkflow() {
    Workflow workflow = new TestWorkflow();
    workflow.setProgress(makeProgress(WORKFLOW_NAME));
    return workflow;
  }

  private PoolProgressStep makePoolStep(String alias, String name, int stepNumber) {
    PoolProgressStep step = makePoolStep(alias, name);
    step.setStepNumber(stepNumber);
    return step;
  }

  private PoolProgressStep makePoolStep(String alias, String name) {
    PoolProgressStep step = new PoolProgressStep();
    Pool pool = new PoolImpl();
    pool.setAlias(alias);
    pool.setName(name);
    step.setInput(pool);
    return step;
  }

  private IntegerProgressStep makeIntegerStep(int input, int stepNumber) {
    IntegerProgressStep step = makeIntegerStep(input);
    step.setStepNumber(stepNumber);
    return step;
  }

  private IntegerProgressStep makeIntegerStep(int input) {
    IntegerProgressStep step = new IntegerProgressStep();
    step.setInput(input);
    return step;
  }

  private void assertIntegerPrompt(WorkflowStepPrompt prompt) {
    assertEquals(Sets.newHashSet(InputType.INTEGER), prompt.getInputTypes());
    assertEquals("Input a concentration as an integer.", prompt.getMessage());
  }

  private void assertPoolPrompt(WorkflowStepPrompt prompt) {
    assertEquals(Sets.newHashSet(InputType.POOL), prompt.getInputTypes());
    assertEquals("Scan a Pool to modify its concentration.", prompt.getMessage());
  }

  /**
   * Match Progress object based on workflowName, stepNumber, and input fields
   */
  private void assertEquivalent(Progress expectedProgress, Progress actualProgress) {
    assertEquals(expectedProgress.getWorkflowName(), actualProgress.getWorkflowName());

    if (expectedProgress.getSteps() == null) {
      assertNull(actualProgress.getSteps());
    } else {
      List<ProgressStep> expectedSteps = new ArrayList<>(expectedProgress.getSteps());
      List<ProgressStep> actualSteps = new ArrayList<>(actualProgress.getSteps());
      assertEquals(expectedSteps.size(), actualSteps.size());
      for (int i = 0; i < expectedSteps.size(); ++i) {
        assertEquals(expectedSteps.get(i).getStepNumber(), actualSteps.get(i).getStepNumber());
        if (expectedSteps.get(i) instanceof IntegerProgressStep) {
          assertEquals(((IntegerProgressStep) expectedSteps.get(i)).getInput(), ((IntegerProgressStep) actualSteps.get(i)).getInput());
        } else {
          assertEquals(((PoolProgressStep) expectedSteps.get(i)).getInput(), ((PoolProgressStep) actualSteps.get(i)).getInput());
        }
      }
    }
  }

  private Progress makeProgress(WorkflowName workflowName, ProgressStep... steps) {
    Progress progress = makeProgress(workflowName);
    progress.setSteps(Arrays.asList(steps));

    return progress;
  }

  private Progress makeProgress(WorkflowName workflowName) {
    Progress progress = new ProgressImpl();
    progress.setWorkflowName(workflowName);
    return progress;
  }
}
