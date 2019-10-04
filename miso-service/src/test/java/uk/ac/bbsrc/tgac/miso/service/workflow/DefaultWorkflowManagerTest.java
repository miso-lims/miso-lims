package uk.ac.bbsrc.tgac.miso.service.workflow;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.FactoryType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.IntegerProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.PoolProgressStep;
import uk.ac.bbsrc.tgac.miso.core.manager.ProgressStepFactory;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.workflow.factory.BarcodableProgressStepFactory;
import uk.ac.bbsrc.tgac.miso.service.workflow.factory.IntegerProgressStepFactory;

public class DefaultWorkflowManagerTest {
  private static final String INTEGER_INPUT = "1";
  private static final int INTEGER_INPUT_EXPECTED = Integer.parseInt(INTEGER_INPUT);

  private static final String POOL_BARCODE = "123456789";
  private static final long POOL_ID = 1;

  private static final String INVALID_INPUT = "invalid";

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Mock
  private BarcodableProgressStepFactory barcodableProgressStepFactory;

  @Mock
  private IntegerProgressStepFactory integerProgressStepFactory;

  @Mock
  private Map<FactoryType, ProgressStepFactory> progressStepFactoryMap;

  @InjectMocks
  private DefaultWorkflowManager sut;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    when(progressStepFactoryMap.get(FactoryType.INTEGER)).thenReturn(integerProgressStepFactory);
    when(progressStepFactoryMap.get(FactoryType.BARCODABLE)).thenReturn(barcodableProgressStepFactory);
  }

  @Test
  public void testMakeProgressStepWithInteger() throws IOException {
    Set<InputType> inputTypes = Sets.newHashSet(InputType.INTEGER);
    when(integerProgressStepFactory.create(INTEGER_INPUT, inputTypes)).thenReturn(makeIntegerProgressStep(INTEGER_INPUT_EXPECTED));

    assertEquals(INTEGER_INPUT_EXPECTED, ((IntegerProgressStep) sut.makeProgressStep(INTEGER_INPUT, inputTypes)).getInput());
  }

  private IntegerProgressStep makeIntegerProgressStep(int input) {
    IntegerProgressStep step = new IntegerProgressStep();
    step.setInput(input);
    return step;
  }

  @Test
  public void testMakeProgresssStepWithPool() throws IOException {
    Set<InputType> inputTypes = Sets.newHashSet(InputType.POOL);
    when(barcodableProgressStepFactory.create(POOL_BARCODE, inputTypes)).thenReturn(makePoolProgressStep(POOL_ID));

    assertEquals(POOL_ID, ((PoolProgressStep) sut.makeProgressStep(POOL_BARCODE, inputTypes)).getInput().getId());
  }

  private PoolProgressStep makePoolProgressStep(long id) {
    Pool pool = new PoolImpl();
    pool.setId(id);

    PoolProgressStep step = new PoolProgressStep();
    step.setInput(pool);

    return step;
  }

  @Test
  public void testMakeProgressStepInvalidInput() throws IOException {
    Set<InputType> inputTypes = Sets.newHashSet(InputType.INTEGER, InputType.POOL);
    when(barcodableProgressStepFactory.create(INVALID_INPUT, inputTypes)).thenReturn(null);
    when(integerProgressStepFactory.create(INVALID_INPUT, inputTypes)).thenReturn(null);

    try {
      sut.makeProgressStep(INVALID_INPUT, inputTypes);
      fail("ValidationException not thrown");
    } catch (ValidationException e) {
      assertEquals(String.format("No Pool or Integer found matching '%s'", INVALID_INPUT), e.getErrors().get(0).getMessage());
    }
  }

  @Test
  public void testMakeProgressStepFallsBackToInteger() throws IOException {
    Set<InputType> inputTypes = Sets.newHashSet(InputType.INTEGER, InputType.POOL);
    when(barcodableProgressStepFactory.create(INTEGER_INPUT, inputTypes)).thenReturn(null);
    when(integerProgressStepFactory.create(INTEGER_INPUT, inputTypes)).thenReturn(makeIntegerProgressStep(INTEGER_INPUT_EXPECTED));

    assertEquals(INTEGER_INPUT_EXPECTED, ((IntegerProgressStep) sut.makeProgressStep(INTEGER_INPUT, inputTypes)).getInput());
  }
}
