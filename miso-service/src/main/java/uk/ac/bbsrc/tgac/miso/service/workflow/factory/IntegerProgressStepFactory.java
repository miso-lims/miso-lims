package uk.ac.bbsrc.tgac.miso.service.workflow.factory;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType.INTEGER;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.IntegerProgressStep;
import uk.ac.bbsrc.tgac.miso.core.manager.ProgressStepFactory;

/**
 * Attempt to create an IntegerProgressStep by casting the user's input to an int
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IntegerProgressStepFactory implements ProgressStepFactory {
  @Override
  public ProgressStep create(String input, Set<InputType> inputTypes) throws IOException {
    IntegerProgressStep step = new IntegerProgressStep();
    try {
      step.setInput(Integer.parseInt(input));
      return step;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  @Override
  public FactoryType getFactoryType() {
    return INTEGER;
  }
}
