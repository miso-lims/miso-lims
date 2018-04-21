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

import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;

public class LoadSequencerWorkflowTest {
  private static final WorkflowName WORKFLOW_NAME = LOAD_SEQUENCER;

  @Test
  public void testNoInput() {
    Workflow workflow = new LoadSequencerWorkflow();
    workflow.setProgress(makeProgress(WORKFLOW_NAME));
    assertNoInput(workflow);
  }

  @Test
  public void testProcessInvalidInput() {
    Workflow workflow = new LoadSequencerWorkflow();
    assertThrows(IllegalArgumentException.class, () -> workflow.setProgress(makeProgress(WORKFLOW_NAME, new PoolProgressStep())));
  }

  private void assertNoInput(Workflow workflow) {
    assertThrows(IllegalArgumentException.class, () -> workflow.processInput(2, makeSpcStep()));

    assertEquivalent(makeProgress(WORKFLOW_NAME), workflow.getProgress());
    WorkflowStepPrompt prompt = workflow.getStep(0);
    assertEquals(Sets.newHashSet(InputType.SEQUENCER_PARTITION_CONTAINER, InputType.STRING), prompt.getInputTypes());
    assertEquals("Scan a flow cell serial number", prompt.getMessage());
    assertThrows(IllegalArgumentException.class, () -> workflow.getStep(1));
    assertFalse(workflow.isComplete());
    assertEquals(Collections.emptyList(), workflow.getLog());
  }

  private SequencerPartitionContainerProgressStep makeSpcStep() {
    SequencerPartitionContainerProgressStep step = new SequencerPartitionContainerProgressStep();
    return step;
  }
}