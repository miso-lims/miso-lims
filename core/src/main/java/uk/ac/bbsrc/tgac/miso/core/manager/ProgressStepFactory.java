package uk.ac.bbsrc.tgac.miso.core.manager;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;

/**
 * Responsible for attempting to interpret user input and constructing an appropriate ProgressStep
 */
public interface ProgressStepFactory {
  /**
   * @return new ProgressStep or null if a ProgressStep cannot be constructed from the given input
   */
  ProgressStep create(String input, Set<InputType> inputTypes) throws IOException;

  FactoryType getFactoryType();
}
