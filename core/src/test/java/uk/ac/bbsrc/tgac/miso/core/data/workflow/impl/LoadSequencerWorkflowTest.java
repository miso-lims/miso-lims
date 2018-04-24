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
  private static SequencingContainerModel MODEL;
  static {
    MODEL = new SequencingContainerModel();
    MODEL.setPartitionCount(2);
  }

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
  public void testProcessSequencingPartitionContainer() {
    Workflow workflow = makeWorkflow();
    SequencingContainerModel model1 = new SequencingContainerModel();
    model1.setPartitionCount(1);
    workflow.processInput(0, makeSpcStep(MODEL, SPC_BARCODE));

    assertEquivalent(makeProgress(WORKFLOW_NAME, makeSpcStep(MODEL, SPC_BARCODE, 0)), workflow.getProgress());
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