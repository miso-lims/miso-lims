package uk.ac.bbsrc.tgac.miso.service.workflow.factory;

import static uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import static uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType.BARCODABLE;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.PoolProgressStep;
import uk.ac.bbsrc.tgac.miso.service.BarcodableViewService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;

@Service
@Transactional(rollbackFor = Exception.class)
public class BarcodableProgressStepFactory implements ProgressStepFactory {
  @Autowired
  BarcodableViewService barcodableViewService;

  @Override
  public ProgressStep create(String input, Set<InputType> inputTypes) throws IOException {
    List<BarcodableView> views = barcodableViewService.searchByBarcode(input, getEntityTypes(inputTypes));

    if (views.isEmpty()) {
      return null;
    } else if (views.size() == 1) {
      return makeProgressStep(views.get(0));
    } else {
      throw new ValidationException(Collections.singletonList(new ValidationError("Duplicate barcodes found")));
    }
  }

  @Override
  public ProgressStep.FactoryType getFactoryType() {
    return BARCODABLE;
  }

  private Collection<EntityType> getEntityTypes(Collection<InputType> inputTypes) {
    return inputTypes.stream().map(InputType::getEntityType).filter(Objects::nonNull).collect(Collectors.toSet());
  }

  private ProgressStep makeProgressStep(BarcodableView view) throws IOException {
    if (view.getId().getTargetType() == EntityType.POOL) {
      PoolProgressStep step = new PoolProgressStep();
      step.setInput(barcodableViewService.getEntity(view));
      return step;
    } else {
      throw new UnsupportedOperationException("Unsupported entity type");
    }
  }
}
