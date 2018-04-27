package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName.LOAD_SEQUENCER;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.WorkflowTestUtils.assertEquivalent;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.WorkflowTestUtils.assertThrows;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.WorkflowTestUtils.makeProgress;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
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
    assertIntegerPrompt(workflow.getStep(0));
    assertThrows(IllegalArgumentException.class, () -> workflow.getStep(1));
    assertFalse(workflow.isComplete());
    assertEquals(Collections.emptyList(), workflow.getLog());
    assertEquals(new Integer(0), workflow.getNextStepNumber());
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
    assertThrows(IllegalArgumentException.class, () -> workflow.processInput(0, makePoolStep(POOL_ALIAS, POOL_NAME)));
  }

  @Test
  public void testProcessValidInput() {
    workflow = makeNewWorkflow();
    workflow.processInput(0, makeIntegerStep(INT_1));
    assertReceivedOneInput(workflow, INT_1);
  }

  private void assertReceivedOneInput(Workflow workflow, int input) {
    assertThrows(IllegalArgumentException.class, () -> workflow.processInput(2, makePoolStep(POOL_ALIAS, POOL_NAME)));

    assertEquivalent(makeProgress(WORKFLOW_NAME, makeIntegerStep(input, 0)), workflow.getProgress());
    assertIntegerPrompt(workflow.getStep(0));
    assertPoolPrompt(workflow.getStep(1));
    assertThrows(IllegalArgumentException.class, () -> workflow.getStep(2));
    assertFalse(workflow.isComplete());
    assertEquals(Collections.singletonList(String.format("Entered concentration value: %d", input)), workflow.getLog());
    assertEquals(new Integer(1), workflow.getNextStepNumber());
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
    workflow.processInput(1, makePoolStep(POOL_ALIAS, POOL_NAME));
    assertReceivedTwoInputs(workflow, INT_1, POOL_ALIAS, POOL_NAME);
  }

  private void assertReceivedTwoInputs(Workflow workflow, int intInput, String poolAlias, String poolName) {
    assertThrows(IllegalArgumentException.class, () -> workflow.processInput(2, makePoolStep(POOL_ALIAS, POOL_NAME)));

    assertEquivalent(makeProgress(WORKFLOW_NAME, makeIntegerStep(intInput, 0), makePoolStep(poolAlias, poolName, 1)), workflow.getProgress());
    assertIntegerPrompt(workflow.getStep(0));
    assertPoolPrompt(workflow.getStep(1));
    assertThrows(IllegalArgumentException.class, () -> workflow.getStep(2));
    assertTrue(workflow.isComplete());
    assertEquals(
        Arrays.asList(String.format("Entered concentration value: %d", intInput), String.format("Selected Pool %s (%s)", poolAlias, poolName)),
        workflow.getLog());
    assertNull(workflow.getNextStepNumber());
    assertEquals(String.format("Pool %s (%s) will be modified to have concentration %d.", poolAlias, poolName, intInput),
        workflow.getConfirmMessage());
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
      workflow.processInput(1, makeIntegerStep(INT_2));
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
}
