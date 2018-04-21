package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName.LOAD_SEQUENCER;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;

public class LoadSequencerWorkflowTest {
  private static final WorkflowName WORKFLOW_NAME = LOAD_SEQUENCER;

  @Test
  public void testNoInput() {
    assertNoInput(new LoadSequencerWorkflow());
  }

  private void assertNoInput(Workflow workflow) {

  }
}