package uk.ac.bbsrc.tgac.miso.service.workflow.factory;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType.EMPTY;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.SkipProgressStep;

@Service
@Transactional(rollbackFor = Exception.class)
public class SkipProgressStepFactory implements ProgressStepFactory {
  /**
   * If the user supplies empty input, then construct a SkipProgressStep
   */
  @Override
  public ProgressStep create(String input, Set<InputType> inputTypes) throws IOException {
    if (input.equals("")) return new SkipProgressStep();
    return null;
  }

  @Override
  public ProgressStep.FactoryType getFactoryType() {
    return EMPTY;
  }
}
