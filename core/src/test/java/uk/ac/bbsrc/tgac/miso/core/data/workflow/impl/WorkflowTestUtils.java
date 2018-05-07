package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.AssertionFailedError;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;

class WorkflowTestUtils {
  static <T extends Throwable> void assertThrows(Class<T> expectedType, Runnable runnable) {
    try {
      runnable.run();
      throw new AssertionFailedError(String.format("Expected exception %s was not thrown", expectedType.toString()));
    } catch (Throwable actualException) {
      if (!expectedType.isInstance(actualException)) {
        throw new AssertionFailedError(String.format("Expected exception %s was not thrown", expectedType.toString()));
      }
    }
  }

  /**
   * Match Progress objects based on workflowName, stepNumber, and input fields
   */
  static void assertEquivalent(Progress expectedProgress, Progress actualProgress) {
    assertEquals(expectedProgress.getWorkflowName(), actualProgress.getWorkflowName());

    if (expectedProgress.getSteps() == null) {
      assertNull(actualProgress.getSteps());
    } else {
      List<ProgressStep> expectedSteps = new ArrayList<>(expectedProgress.getSteps());
      List<ProgressStep> actualSteps = new ArrayList<>(actualProgress.getSteps());
      assertEquals(expectedSteps.size(), actualSteps.size());
      for (int i = 0; i < expectedSteps.size(); ++i) {
        ProgressStep expectedStep = expectedSteps.get(i);
        ProgressStep actualStep = actualSteps.get(i);
        assertEquals(expectedStep.getStepNumber(), actualStep.getStepNumber());
        if (expectedStep instanceof IntegerProgressStep) {
          assertEquals(((IntegerProgressStep) expectedStep).getInput(), ((IntegerProgressStep) actualStep).getInput());
        } else if (expectedStep instanceof PoolProgressStep) {
          assertEquals(((PoolProgressStep) expectedStep).getInput(), ((PoolProgressStep) actualStep).getInput());
        } else if (expectedStep instanceof SequencerPartitionContainerProgressStep) {
          assertEquals(((SequencerPartitionContainerProgressStep) expectedStep).getInput(),
              ((SequencerPartitionContainerProgressStep) actualStep).getInput());
        } else if (expectedStep instanceof StringProgressStep) {
          assertEquals(((StringProgressStep) expectedStep).getInput(), ((StringProgressStep) actualStep).getInput());
        } else if (expectedStep instanceof SkipProgressStep) {
          // All SkipProgressSteps are equivalent
        } else if (expectedStep instanceof SequencingContainerModelProgressStep) {
          assertEquals(((SequencingContainerModelProgressStep) expectedStep).getInput(),
              ((SequencingContainerModelProgressStep) actualStep).getInput());
        } else {
          fail("Unexpected ProgressStep type");
        }
      }
    }
  }

  static Progress makeProgress(WorkflowName workflowName, ProgressStep... steps) {
    Progress progress = new ProgressImpl();
    progress.setWorkflowName(workflowName);
    progress.setSteps(Arrays.asList(steps));
    return progress;
  }

}
