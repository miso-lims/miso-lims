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
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;

public class LoadSequencerWorkflowTest {
  private static final WorkflowName WORKFLOW_NAME = LOAD_SEQUENCER;
  private static final String SPC_BARCODE = "spc_barcode";

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
    workflow.processInput(0, makeSpcStep(SPC_BARCODE));

    assertThrows(IllegalArgumentException.class, () -> workflow.processInput(2, new PoolProgressStep()));
    assertEquivalent(makeProgress(WORKFLOW_NAME, makeSpcStep(SPC_BARCODE, 0)), workflow.getProgress());
//    assertSpcPrompt(workflow.getStep(0));
  }

  private void assertNoInput(Workflow workflow) {
    assertThrows(IllegalArgumentException.class, () -> workflow.processInput(2, makeSpcStep(SPC_BARCODE)));

    assertEquivalent(makeProgress(WORKFLOW_NAME), workflow.getProgress());
    assertSpcPrompt(workflow.getStep(0));
    assertThrows(IllegalArgumentException.class, () -> workflow.getStep(1));
    assertFalse(workflow.isComplete());
    assertEquals(Collections.emptyList(), workflow.getLog());
  }

  private void assertSpcPrompt(WorkflowStepPrompt prompt) {
    assertEquals(Sets.newHashSet(InputType.SEQUENCER_PARTITION_CONTAINER, InputType.STRING), prompt.getInputTypes());
    assertEquals("Scan a flow cell serial number", prompt.getMessage());
  }

  private SequencerPartitionContainerProgressStep makeSpcStep(String barcode, int stepNumber) {
    SequencerPartitionContainerProgressStep step = makeSpcStep(barcode);
    step.setStepNumber(stepNumber);
    return step;
  }

  private SequencerPartitionContainerProgressStep makeSpcStep(String barcode) {
    SequencerPartitionContainerProgressStep step = new SequencerPartitionContainerProgressStep();
    SequencerPartitionContainer spc = new SequencerPartitionContainerImpl();
    spc.setIdentificationBarcode(barcode);
    step.setInput(spc);
    return step;
  }
}