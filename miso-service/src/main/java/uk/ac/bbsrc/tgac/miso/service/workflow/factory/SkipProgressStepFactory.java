package uk.ac.bbsrc.tgac.miso.service.workflow.factory;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType.SKIP;

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
    return input.equals(InputType.SKIP.toString()) ? new SkipProgressStep() : null;
  }

  @Override
  public ProgressStep.FactoryType getFactoryType() {
    return SKIP;
  }
}
