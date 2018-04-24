package uk.ac.bbsrc.tgac.miso.service.workflow.factory;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType.EMPTY;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.EmptyProgressStep;

@Service
@Transactional(rollbackFor = Exception.class)
public class EmptyProgressStepFactory implements ProgressStepFactory {
  /**
   * If the user supplies empty input, then construct a EmptyProgressStep
   */
  @Override
  public ProgressStep create(String input, Set<InputType> inputTypes) throws IOException {
    if (input.equals("")) return new EmptyProgressStep();
    return null;
  }

  @Override
  public ProgressStep.FactoryType getFactoryType() {
    return EMPTY;
  }
}
