package uk.ac.bbsrc.tgac.miso.service.workflow.factory;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType.STOCK;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.SampleProgressStep;

/**
 * Attempt to create an StockProgressStep by checking if the stock barcode exists and points to a stock
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StockProgressStepFactory extends BarcodableProgressStepFactory {

  @Override
  public ProgressStep create(String input, Set<InputType> inputTypes) throws IOException {
    SampleProgressStep step = ((SampleProgressStep) super.create(input, inputTypes));
    if (step == null) {
      return null;
    }
    if (((DetailedSample) step.getInput()).getSampleClass().getSampleCategory().equals("Stock")) {
      return step;
    }
    return null;
  }

  @Override
  public FactoryType getFactoryType() {
    return STOCK;
  }
}
