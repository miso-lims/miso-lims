package uk.ac.bbsrc.tgac.miso.service.workflow.factory;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType.BOX_POSITION;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.BoxPositionProgressStep;
import uk.ac.bbsrc.tgac.miso.core.manager.ProgressStepFactory;

@Service
@Transactional(rollbackFor = Exception.class)
public class BoxPositionProgressStepFactory implements ProgressStepFactory {
  @Override
  public BoxPositionProgressStep create(String input, Set<InputType> inputTypes) throws IOException {
    if (!input.matches("[A-Z][0-9][0-9]")) return null;
    BoxPositionProgressStep step = new BoxPositionProgressStep();
    step.setInput(input);
    return step;
  }

  @Override
  public FactoryType getFactoryType() {
    return BOX_POSITION;
  }
}
