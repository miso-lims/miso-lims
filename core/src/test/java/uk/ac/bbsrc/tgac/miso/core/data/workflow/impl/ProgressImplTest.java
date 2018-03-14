package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;

public class ProgressImplTest {
  private Progress progress;

  @Before
  public void setUp() {
    progress = new ProgressImpl();
    progress.setSteps(Collections.emptyList());
  }

  @Test
  public void testSetUnorderedStepsThroughGetter() {
    ProgressStep step1 = makePoolProgressStep(1);
    ProgressStep step2 = makePoolProgressStep(2);

    progress.getSteps().add(step2);
    progress.getSteps().add(step1);

    List<ProgressStep> steps = new ArrayList<>(progress.getSteps());
    assertEquals(2, steps.size());
    assertEquals(1, steps.get(0).getStepNumber());
    assertEquals(2, steps.get(1).getStepNumber());
  }

  @Test
  public void testSetUnorderedSteps() {
    ProgressStep step1  = makePoolProgressStep(1);
    ProgressStep step2  = makePoolProgressStep(2);

    progress.setSteps(Arrays.asList(step2, step1));

    List<ProgressStep> steps = new ArrayList<>(progress.getSteps());
    assertEquals(2, steps.size());
    assertEquals(1, steps.get(0).getStepNumber());
    assertEquals(2, steps.get(1).getStepNumber());
  }

  private PoolProgressStep makePoolProgressStep(int stepNumber) {
    PoolProgressStep step = new PoolProgressStep();

    step.setStepNumber(stepNumber);

    return step;
  }
}
