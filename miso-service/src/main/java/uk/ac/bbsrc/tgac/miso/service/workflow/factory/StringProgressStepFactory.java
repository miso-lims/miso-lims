package uk.ac.bbsrc.tgac.miso.service.workflow.factory;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType.STRING;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.StringProgressStep;
import uk.ac.bbsrc.tgac.miso.core.manager.ProgressStepFactory;

@Service
@Transactional(rollbackFor = Exception.class)
public class StringProgressStepFactory implements ProgressStepFactory {
  @Override
  public ProgressStep create(String input, Set<InputType> inputTypes) throws IOException {
    StringProgressStep step = new StringProgressStep();
    step.setInput(input);
    return step;
  }

  @Override
  public FactoryType getFactoryType() {
    return STRING;
  }
}
