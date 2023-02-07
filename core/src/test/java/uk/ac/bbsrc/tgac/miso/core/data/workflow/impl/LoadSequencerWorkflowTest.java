package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;

public class LoadSequencerWorkflowTest {

  private static final String SERIAL_NUMBER = "ABCDEFGH";
  private static final String MODEL_ALIAS = "Test Model";
  private static final String POOL_1 = "Pool 1";
  private static final String POOL_2 = "Pool2";

  @Mock
  private WorkflowExecutor workflowExecutor;

  private Workflow sut;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    Progress progress = new ProgressImpl();
    progress.setWorkflowName(WorkflowName.LOAD_SEQUENCER);
    sut = WorkflowName.LOAD_SEQUENCER.createWorkflow(progress);
  }

  @Test
  public void testNewFlowcell() throws IOException {
    enterNewFlowCell();
    SequencerPartitionContainer container = executeWorkflow();

    assertEquals(SERIAL_NUMBER, container.getIdentificationBarcode());
    assertEquals(MODEL_ALIAS, container.getModel().getAlias());
    assertEquals(2, container.getPartitions().size());
    assertEquals(POOL_1, container.getPartitions().get(0).getPool().getAlias());
    assertEquals(POOL_2, container.getPartitions().get(1).getPool().getAlias());
  }

  @Test
  public void testExistingFlowcell() throws IOException {
    enterExistingFlowCell();
    SequencerPartitionContainer container = executeWorkflow();

    assertEquals(SERIAL_NUMBER, container.getIdentificationBarcode());
    assertEquals(MODEL_ALIAS, container.getModel().getAlias());
    assertEquals(2, container.getPartitions().size());
    assertEquals(POOL_1, container.getPartitions().get(0).getPool().getAlias());
    assertEquals(POOL_2, container.getPartitions().get(1).getPool().getAlias());
  }

  @Test
  public void testCancelInput() {
    enterNewFlowCell();

    sut.cancelInput();
    assertState(false, 3, InputType.POOL, InputType.SKIP);
    sut.cancelInput();
    assertState(false, 2, InputType.POOL, InputType.SKIP);
    sut.cancelInput();
    assertState(false, 1, InputType.SEQUENCING_CONTAINER_MODEL);
    sut.cancelInput();
    assertState(false, 0, InputType.SEQUENCER_PARTITION_CONTAINER, InputType.STRING);
    sut.cancelInput();
  }

  @Test
  public void testChangeToExistingContainer() {
    enterNewFlowCell();
    assertState(true, null);

    // changing to an existing container should clear the pool steps and make the model step unneccessary
    sut.processInput(0, makeContainerStep(SERIAL_NUMBER, MODEL_ALIAS, 2, PlatformType.ILLUMINA));
    assertState(false, 1, InputType.POOL, InputType.SKIP);
  }

  @Test
  public void testChangeToNewContainer() {
    enterExistingFlowCell();
    assertState(true, null);

    // changing to a new container should clear the pool steps and make the model step neccessary
    sut.processInput(0, makeStringProgressStep(SERIAL_NUMBER));
    assertState(false, 1, InputType.SEQUENCING_CONTAINER_MODEL);
  }

  @Test
  public void testChangeModel() {
    enterNewFlowCell();
    assertState(true, null);

    // changing the model should clear the pool steps
    sut.processInput(1, makeModelStep(MODEL_ALIAS, 2, PlatformType.ILLUMINA));
    assertState(false, 2, InputType.POOL, InputType.SKIP);
  }

  @Test
  public void testChangePools() throws IOException {
    final String pool1 = "alternate pool 1";
    final String pool2 = "alternate pool 2";

    enterNewFlowCell();
    sut.processInput(2, makePoolStep(pool1, "IPO3"));
    assertState(true, null);

    sut.processInput(3, makePoolStep(pool2, "IPO4"));
    assertState(true, null);

    SequencerPartitionContainer container = executeWorkflow();
    assertEquals(pool1, container.getPartitions().get(0).getPool().getAlias());
    assertEquals(pool2, container.getPartitions().get(1).getPool().getAlias());
  }

  @Test
  public void testThrowUnexpectedInput() {
    try {
      // for step 1, a container step is not expected and should be rejected with an IllegalArgumentException
      sut.processInput(1, makeContainerStep(SERIAL_NUMBER, MODEL_ALIAS, 2, PlatformType.ILLUMINA));
      fail("testThrowUnexpectedInput should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertEquals("Unexpected input", expected.getMessage());
    }
  }

  private void assertState(boolean isComplete, Integer nextStep, InputType... inputTypes) {
    assertEquals(isComplete, sut.isComplete());
    assertEquals(nextStep, sut.getNextStepNumber());
    if (nextStep != null) {
      for (InputType inputType : inputTypes) {
        assertTrue(String.format("Step %d should accept input type %s", nextStep, inputType.getName()),
            sut.getStep(nextStep).getInputTypes().contains(inputType));
      }
    }
  }

  private void enterNewFlowCell() {
    assertState(false, 0, InputType.SEQUENCER_PARTITION_CONTAINER, InputType.STRING);
    sut.processInput(0, makeStringProgressStep(SERIAL_NUMBER));
    assertState(false, 1, InputType.SEQUENCING_CONTAINER_MODEL);
    sut.processInput(1, makeModelStep(MODEL_ALIAS, 2, PlatformType.ILLUMINA));
    assertState(false, 2, InputType.POOL, InputType.SKIP);
    sut.processInput(2, makePoolStep(POOL_1, "IPO1"));
    assertState(false, 3, InputType.POOL, InputType.SKIP);
    sut.processInput(3, makePoolStep(POOL_2, "IPO2"));
    assertState(true, null);
  }

  private void enterExistingFlowCell() {
    assertState(false, 0, InputType.SEQUENCER_PARTITION_CONTAINER, InputType.STRING);
    sut.processInput(0, makeContainerStep(SERIAL_NUMBER, MODEL_ALIAS, 2, PlatformType.ILLUMINA));
    assertState(false, 1, InputType.POOL, InputType.SKIP);
    sut.processInput(1, makePoolStep(POOL_1, "IPO1"));
    assertState(false, 2, InputType.POOL, InputType.SKIP);
    sut.processInput(2, makePoolStep(POOL_2, "IPO2"));
    assertState(true, null);
  }

  private SequencerPartitionContainer executeWorkflow() throws IOException {
    sut.execute(workflowExecutor);
    ArgumentCaptor<SequencerPartitionContainer> containerCaptor = ArgumentCaptor.forClass(SequencerPartitionContainer.class);
    Mockito.verify(workflowExecutor).save(containerCaptor.capture());
    return containerCaptor.getValue();
  }

  private StringProgressStep makeStringProgressStep(String input) {
    StringProgressStep step = new StringProgressStep();
    step.setInput(input);
    return step;
  }

  private SequencerPartitionContainerProgressStep makeContainerStep(String serialNumber, String modelAlias, int partitionCount,
      PlatformType platformType) {
    SequencerPartitionContainer container = new SequencerPartitionContainerImpl();
    container.setIdentificationBarcode(serialNumber);
    container.setModel(makeModel(modelAlias, partitionCount, platformType));
    container.setPartitionLimit(partitionCount);

    SequencerPartitionContainerProgressStep step = new SequencerPartitionContainerProgressStep();
    step.setInput(container);
    return step;
  }

  private PoolProgressStep makePoolStep(String alias, String name) {
    Pool pool = new PoolImpl();
    pool.setAlias(alias);
    pool.setName(name);

    PoolProgressStep step = new PoolProgressStep();
    step.setInput(pool);
    return step;
  }

  private SequencingContainerModelProgressStep makeModelStep(String alias, int partitionCount, PlatformType platformType) {
    SequencingContainerModelProgressStep step = new SequencingContainerModelProgressStep();
    step.setInput(makeModel(alias, partitionCount, platformType));
    return step;
  }

  private SequencingContainerModel makeModel(String alias, int partitionCount, PlatformType platformType) {
    SequencingContainerModel model = new SequencingContainerModel();
    model.setPartitionCount(partitionCount);
    model.setAlias(alias);
    model.setPlatformType(platformType);
    return model;
  }

}