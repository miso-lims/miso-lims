package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName.LOAD_SEQUENCER;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.WorkflowTestUtils.assertEquivalent;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.WorkflowTestUtils.assertThrows;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.WorkflowTestUtils.makeProgress;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.WorkflowTestUtils.makeWorkflow;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;

public class LoadSequencerWorkflowTest {
  private static final WorkflowName WORKFLOW_NAME = LOAD_SEQUENCER;
  private static final String SPC_BARCODE = "spc_barcode";
  private static final String UNKNOWN_SPC_BARCODE = "unknown_barcode";
  private static final String POOL_ALIAS_1 = "Pool_alias_1";
  private static final String POOL_NAME_1 = "Pool_name_1";
  private static final String POOL_ALIAS_2 = "Pool_alias_2";
  private static final String POOL_NAME_2 = "Pool_name_2";
  private static final String MODEL_ALILAS = "Model Alias";
  private static final SequencingContainerModel MODEL = makeModel(MODEL_ALILAS, 2);

  @Test
  public void testNoInput() {
    assertNoInput(makeWorkflow(WORKFLOW_NAME));
  }

  @Test
  public void testProcessInvalidInput() {
    assertThrows(IllegalArgumentException.class, () -> makeWorkflow(WORKFLOW_NAME).processInput(0, new PoolProgressStep()));
  }

  @Test
  public void testProcessKnownSpc() {
    Workflow workflow = makeWorkflow(WORKFLOW_NAME, makeSpcStep(MODEL, SPC_BARCODE));

    assertEquivalent(makeProgress(WORKFLOW_NAME, makeSpcStep(MODEL, SPC_BARCODE, 0)), workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertPoolPrompt(workflow.getStep(1), 1);
    assertFalse(workflow.isComplete());
    assertEquals(Collections.singletonList(String.format("Scanned existing Sequencing Container %s", SPC_BARCODE)), workflow.getLog());
  }

  @Test
  public void testProcessUnknownSpc() {
    Workflow workflow = makeWorkflow(WORKFLOW_NAME, makeStringProgressStep(UNKNOWN_SPC_BARCODE));

    assertEquivalent(makeProgress(WORKFLOW_NAME, makeStringProgressStep(UNKNOWN_SPC_BARCODE, 0)), workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertModelPrompt(workflow.getStep(1));
    assertFalse(workflow.isComplete());
    assertEquals(Collections.singletonList(String.format("Scanned new Sequencing Container %s", UNKNOWN_SPC_BARCODE)), workflow.getLog());
  }

  private static SequencingContainerModel makeModel(String alias, int partitionCount) {
    SequencingContainerModel model = makeModel(partitionCount);
    model.setAlias(alias);
    return model;
  }

  private static SequencingContainerModel makeModel(int partitionCount) {
    SequencingContainerModel model = new SequencingContainerModel();
    model.setPartitionCount(partitionCount);
    return model;
  }

  @Test
  public void testProcessModel() {
    Workflow workflow = makeWorkflow(WORKFLOW_NAME, makeStringProgressStep(UNKNOWN_SPC_BARCODE, 0), makeModelStep(MODEL, 1));

    assertEquivalent(makeProgress(WORKFLOW_NAME, makeStringProgressStep(UNKNOWN_SPC_BARCODE, 0), makeModelStep(MODEL, 1)),
        workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertModelPrompt(workflow.getStep(1));
    assertPoolPrompt(workflow.getStep(2), 1);
    assertFalse(workflow.isComplete());
    assertEquals(Arrays.asList(String.format("Scanned new Sequencing Container %s", UNKNOWN_SPC_BARCODE),
        String.format("Selected Sequencing Container Model %s", MODEL.getAlias())), workflow.getLog());
  }

  @Test
  public void testProcessKnownSpcAndSkipPartitions() {
    Workflow workflow = makeWorkflow(WORKFLOW_NAME, makeSpcStep(MODEL, SPC_BARCODE, 0), makeSkipStep(1), makeSkipStep(2));

    assertEquivalent(makeProgress(WORKFLOW_NAME, makeSpcStep(MODEL, SPC_BARCODE, 0), makeSkipStep(1), makeSkipStep(2)),
        workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertPoolPrompt(workflow.getStep(1), 1);
    assertPoolPrompt(workflow.getStep(2), 2);
    assertTrue(workflow.isComplete());
    assertEquals(
        Arrays.asList(String.format("Scanned existing Sequencing Container %s", SPC_BARCODE), "Skipped partition 1", "Skipped partition 2"),
        workflow.getLog());
    assertEquals(String.format("All Pools will be removed from Sequencing Container %s", SPC_BARCODE), workflow.getConfirmMessage());
  }

  @Test
  public void testProcessKnownSpcAndPools() {
    SequencerPartitionContainerProgressStep spcStep = makeSpcStep(MODEL, SPC_BARCODE, 0);
    PoolProgressStep poolStep1 = makePoolStep(makePool(POOL_ALIAS_1, POOL_NAME_1), 1);
    PoolProgressStep poolStep2 = makePoolStep(makePool(POOL_ALIAS_2, POOL_NAME_2), 2);

    Workflow workflow = makeWorkflow(WORKFLOW_NAME, spcStep, poolStep1, poolStep2);

    assertEquivalent(makeProgress(WORKFLOW_NAME, spcStep, poolStep1, poolStep2), workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertPoolPrompt(workflow.getStep(1), 1);
    assertPoolPrompt(workflow.getStep(2), 2);
    assertTrue(workflow.isComplete());
    assertEquals(
        Arrays.asList(String.format("Scanned existing Sequencing Container %s", SPC_BARCODE),
            String.format("Selected Pool %s (%s) for partition %d", poolStep1.getInput().getAlias(), poolStep1.getInput().getName(), 1),
            String.format("Selected Pool %s (%s) for partition %d", poolStep2.getInput().getAlias(), poolStep2.getInput().getName(), 2)),
        workflow.getLog());
    assertEquals(
        String.format("Sequencing Container %s will be modified to contain the following Pools: %s and %s",
            spcStep.getInput().getIdentificationBarcode(), poolStep1.getInput().getAlias(), poolStep2.getInput().getAlias()),
        workflow.getConfirmMessage());
  }

  @Test
  public void testProcessModelAndSkipPartitions() {
    StringProgressStep stringStep = makeStringProgressStep(UNKNOWN_SPC_BARCODE, 0);
    SequencingContainerModelProgressStep modelStep = makeModelStep(MODEL, 1);
    SkipProgressStep skipStep1 = makeSkipStep(2);
    SkipProgressStep skipStep2 = makeSkipStep(3);
    Workflow workflow = makeWorkflow(WORKFLOW_NAME, stringStep, modelStep, skipStep1, skipStep2);

    assertEquivalent(makeProgress(WORKFLOW_NAME, stringStep, modelStep, skipStep1, skipStep2), workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertModelPrompt(workflow.getStep(1));
    assertPoolPrompt(workflow.getStep(2), 1);
    assertPoolPrompt(workflow.getStep(3), 2);
    assertTrue(workflow.isComplete());
    assertEquals(
        Arrays.asList(String.format("Scanned new Sequencing Container %s", UNKNOWN_SPC_BARCODE),
            String.format("Selected Sequencing Container Model %s", MODEL.getAlias()), "Skipped partition 1", "Skipped partition 2"),
        workflow.getLog());
  }

  @Test
  public void testProcessModelAndPools() {
    StringProgressStep stringStep = makeStringProgressStep(UNKNOWN_SPC_BARCODE, 0);
    SequencingContainerModelProgressStep modelStep = makeModelStep(MODEL, 1);
    PoolProgressStep poolStep1 = makePoolStep(makePool(POOL_ALIAS_1, POOL_NAME_1), 2);
    PoolProgressStep poolStep2 = makePoolStep(makePool(POOL_ALIAS_2, POOL_NAME_2), 3);
    Workflow workflow = makeWorkflow(WORKFLOW_NAME, stringStep, modelStep, poolStep1, poolStep2);

    assertEquivalent(makeProgress(WORKFLOW_NAME, stringStep, modelStep, poolStep1, poolStep2), workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertModelPrompt(workflow.getStep(1));
    assertPoolPrompt(workflow.getStep(2), 1);
    assertPoolPrompt(workflow.getStep(3), 2);
    assertTrue(workflow.isComplete());
    assertEquals(Arrays.asList(String.format("Scanned new Sequencing Container %s", UNKNOWN_SPC_BARCODE),
        String.format("Selected Sequencing Container Model %s", MODEL.getAlias()),
        String.format("Selected Pool %s (%s) for partition 1", POOL_ALIAS_1, POOL_NAME_1),
        String.format("Selected Pool %s (%s) for partition 2", POOL_ALIAS_2, POOL_NAME_2)), workflow.getLog());
  }

  @Test
  public void testCancelInput() {
    SequencerPartitionContainerProgressStep spcsStep = makeSpcStep(MODEL, SPC_BARCODE, 0);
    Workflow workflow = makeWorkflow(WORKFLOW_NAME, spcsStep);
    workflow.cancelInput();

    assertEquivalent(makeProgress(WORKFLOW_NAME), workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertFalse(workflow.isComplete());
    assertEquals(Collections.emptyList(), workflow.getLog());
    assertEquals(new Integer(0), workflow.getNextStepNumber());
  }

  private PoolProgressStep makePoolStep(Pool pool, int stepNumber) {
    PoolProgressStep step = new PoolProgressStep();
    step.setInput(pool);
    step.setStepNumber(stepNumber);
    return step;
  }

  private Pool makePool(String alias, String name) {
    Pool pool = new PoolImpl();
    pool.setAlias(alias);
    pool.setName(name);
    return pool;
  }

  private SkipProgressStep makeSkipStep(int stepNumber) {
    SkipProgressStep step = new SkipProgressStep();
    step.setStepNumber(stepNumber);
    return step;
  }

  private SequencingContainerModelProgressStep makeModelStep(SequencingContainerModel model, int stepNumber) {
    SequencingContainerModelProgressStep step = makeModelStep(model);
    step.setStepNumber(stepNumber);
    return step;
  }

  private SequencingContainerModelProgressStep makeModelStep(SequencingContainerModel model) {
    SequencingContainerModelProgressStep step = new SequencingContainerModelProgressStep();
    step.setInput(model);
    return step;
  }

  private void assertModelPrompt(WorkflowStepPrompt prompt) {
    assertEquals(Sets.newHashSet(InputType.SEQUENCING_CONTAINER_MODEL), prompt.getInputTypes());
    assertEquals("Scan the REF number of the Sequencing Container", prompt.getMessage());
  }

  private StringProgressStep makeStringProgressStep(String input) {
    StringProgressStep step = new StringProgressStep();
    step.setInput(input);
    return step;
  }

  private StringProgressStep makeStringProgressStep(String input, int stepNumber) {
    StringProgressStep step = makeStringProgressStep(input);
    step.setStepNumber(stepNumber);
    return step;
  }

  private void assertPoolPrompt(WorkflowStepPrompt prompt, int partitionNumber) {
    assertEquals(Sets.newHashSet(InputType.POOL, InputType.SKIP), prompt.getInputTypes());
    assertEquals(String.format("Scan a Pool to assign to partition %d, or enter no input to skip this partition", partitionNumber),
        prompt.getMessage());
  }

  private void assertNoInput(Workflow workflow) {
    assertEquivalent(makeProgress(WORKFLOW_NAME), workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertFalse(workflow.isComplete());
    assertEquals(Collections.emptyList(), workflow.getLog());
  }

  private void assertSpcPrompt(WorkflowStepPrompt prompt) {
    assertEquals(Sets.newHashSet(InputType.SEQUENCER_PARTITION_CONTAINER, InputType.STRING), prompt.getInputTypes());
    assertEquals("Scan a flow cell serial number", prompt.getMessage());
  }

  private SequencerPartitionContainerProgressStep makeSpcStep(SequencingContainerModel model, String barcode, int stepNumber) {
    SequencerPartitionContainerProgressStep step = makeSpcStep(model, barcode);
    step.setStepNumber(stepNumber);
    return step;
  }

  private SequencerPartitionContainerProgressStep makeSpcStep(SequencingContainerModel model, String barcode) {
    SequencerPartitionContainerProgressStep step = new SequencerPartitionContainerProgressStep();
    SequencerPartitionContainer spc = new SequencerPartitionContainerImpl();
    spc.setIdentificationBarcode(barcode);
    spc.setModel(model);
    step.setInput(spc);
    return step;
  }
}