package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;

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
        assertEquals(expectedSteps.get(i).getStepNumber(), actualSteps.get(i).getStepNumber());
        if (expectedSteps.get(i) instanceof IntegerProgressStep) {
          assertEquals(((IntegerProgressStep) expectedSteps.get(i)).getInput(), ((IntegerProgressStep) actualSteps.get(i)).getInput());
        } else {
          assertEquals(((PoolProgressStep) expectedSteps.get(i)).getInput(), ((PoolProgressStep) actualSteps.get(i)).getInput());
        }
      }
    }
  }
}
