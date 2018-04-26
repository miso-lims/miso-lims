package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName.LOAD_SEQUENCER;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.WorkflowTestUtils.assertEquivalent;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.WorkflowTestUtils.assertThrows;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.WorkflowTestUtils.makeProgress;

import java.util.Collections;

import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;

public class LoadSequencerWorkflowTest {
  private static final WorkflowName WORKFLOW_NAME = LOAD_SEQUENCER;
  private static final String SPC_BARCODE = "spc_barcode";
  private static final String UNKNOWN_SPC_BARCODE = "unknown_barcode";
  private static final String MODEL_ALIAS = "Model Alias";
  private static SequencingContainerModel MODEL = makeModel(MODEL_ALIAS, 2);

  @Test
  public void testNoInput() {
    Workflow workflow = new LoadSequencerWorkflow();
    workflow.setProgress(makeProgress(WORKFLOW_NAME));
    assertNoInput(workflow);
  }

  @Test
  public void testProcessInvalidInput() {
    assertThrows(IllegalArgumentException.class, () -> makeWorkflow().processInput(0, new PoolProgressStep()));
  }

  private Workflow makeWorkflow() {
    Workflow workflow = new LoadSequencerWorkflow();
    workflow.setProgress(makeProgress(WORKFLOW_NAME));
    return workflow;
  }

  @Test
  public void testProcessKnownSpc() {
    Workflow workflow = makeWorkflow();
    workflow.processInput(0, makeSpcStep(MODEL, SPC_BARCODE));

    assertEquivalent(makeProgress(WORKFLOW_NAME, makeSpcStep(MODEL, SPC_BARCODE, 0)), workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertPoolPrompt(workflow.getStep(1), 1);
    assertFalse(workflow.isComplete());
    assertEquals(Collections.singletonList(String.format("Scanned existing Sequencing Container %s", SPC_BARCODE)), workflow.getLog());
  }

  @Test
  public void testProcessUnknownSpc() {
    Workflow workflow = makeWorkflow();
    workflow.processInput(0, makeStringProgressStep(UNKNOWN_SPC_BARCODE));

    assertEquivalent(makeProgress(WORKFLOW_NAME, makeStringProgressStep(UNKNOWN_SPC_BARCODE, 0)), workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertModelPrompt(workflow.getStep(1));
    assertFalse(workflow.isComplete());
    assertEquals(Collections.singletonList(String.format("Scanned new Sequencing Container %s", UNKNOWN_SPC_BARCODE)), workflow.getLog());
  }

  private static SequencingContainerModel makeModel(String alias, int partitionCount) {
    SequencingContainerModel model = new SequencingContainerModel();
    model.setPartitionCount(partitionCount);
    return model;
  }

  @Test
  public void testProcessModel() {
    Workflow workflow = makeWorkflow();
    workflow.processInput(0, makeStringProgressStep(UNKNOWN_SPC_BARCODE));
    workflow.processInput(1, makeModelStep(MODEL));

    assertEquivalent(makeProgress(WORKFLOW_NAME, makeStringProgressStep(UNKNOWN_SPC_BARCODE, 0), makeModelStep(MODEL, 1)),
        workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertModelPrompt(workflow.getStep(1));
    assertPoolPrompt(workflow.getStep(2), 1);
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