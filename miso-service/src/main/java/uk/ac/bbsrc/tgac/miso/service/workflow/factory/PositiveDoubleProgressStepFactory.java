package uk.ac.bbsrc.tgac.miso.service.workflow.factory;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType.POSITIVE_DOUBLE;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.PositiveDoubleProgressStep;

/**
 * Attempt to create an PositiveDoubleProgressStep by casting the user's input to an integer
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PositiveDoubleProgressStepFactory implements ProgressStepFactory {
  @Override
  public ProgressStep create(String input, Set<InputType> inputTypes) throws IOException {
    PositiveDoubleProgressStep step = new PositiveDoubleProgressStep();
    try {
      step.setInput(Double.parseDouble(input));
      if (step.getInput() < 0) {
        return null;
      }
      return step;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  @Override
  public FactoryType getFactoryType() {
    return POSITIVE_DOUBLE;
  }
}
