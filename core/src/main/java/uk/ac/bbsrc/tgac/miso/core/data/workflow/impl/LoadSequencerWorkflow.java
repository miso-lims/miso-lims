package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName.LOAD_SEQUENCER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractWorkflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;

public class LoadSequencerWorkflow extends AbstractWorkflow {
  private ScanSpcStep scanSpcStep = new ScanSpcStep();
  private ScanModelStep scanModelStep = new ScanModelStep();
  private List<LaneStep> laneSteps = new ArrayList<>();

  @Override
  protected List<WorkflowStep> getCompletedSteps() {
    return Collections.emptyList();
  }

  @Override
  protected WorkflowName getWorkflowName() {
    return LOAD_SEQUENCER;
  }

  @Override
  public WorkflowStepPrompt getStep(int stepNumber) {
    if (stepNumber == 0) return new WorkflowStepPrompt(Sets.newHashSet(InputType.SEQUENCER_PARTITION_CONTAINER, InputType.STRING),
        "Scan a flow cell serial number");
    throw new IllegalArgumentException("Invalid step number");
  }

  @Override
  public boolean isComplete() {
    return false;
  }

  @Override
  public void processInput(int stepNumber, ProgressStep step) {
    throw new IllegalArgumentException();
  }

  @Override
  public void cancelInput() {
  }

  @Override
  public String getConfirmMessage() {
    return null;
  }

  @Override
  public void execute(WorkflowExecutor workflowExecutor) throws IOException {
  }

  @Override
  public String getName() {
    return "Load Sequencer Workflow";
  }

  private enum State {
    START, RECEIVED_SPC, RECEIVED_SC_MODEL
  }

  private class LaneStep implements WorkflowStep {
    private final int partitionIndex;
    private PoolProgressStep poolStep;
    private EmptyProgressStep skipStep;

    public LaneStep(int partitionIndex) {
      this.partitionIndex = partitionIndex;
    }

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.POOL, InputType.EMPTY),
          String.format("Scan a Pool to assign to partition %d, or enter no input to skip this partition", partitionIndex));
    }

    @Override
    public ProgressStep getProgressStep() {
      return poolStep != null ? poolStep : skipStep;
    }

    @Override
    public void cancelInput() {
      poolStep = null;
      skipStep = null;
    }

    @Override
    public String getLogMessage() {
      if (poolStep != null) {
        Pool pool = poolStep.getInput();
        return String.format("Selected Pool %s (%s) for partition %d", pool.getAlias(), pool.getName(), partitionIndex);
      }

      return String.format("Skipped partition %d", partitionIndex);
    }

    @Override
    public void processInput(PoolProgressStep step) {
      this.poolStep = step;
    }

    @Override
    public void processInput(EmptyProgressStep step) {
      this.skipStep = step;
    }
  }

  private class ScanSpcStep implements WorkflowStep {
    private SequencerPartitionContainerProgressStep spcStep;
    private StringProgressStep stringStep;

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.SEQUENCER_PARTITION_CONTAINER, InputType.STRING),
          "Scan a flow cell serial number");
    }

    @Override
    public ProgressStep getProgressStep() {
      return spcStep != null ? spcStep : stringStep;
    }

    @Override
    public void cancelInput() {
      spcStep = null;
      stringStep = null;
    }

    @Override
    public String getLogMessage() {
      return spcStep != null ? String.format("Scanned existing Sequencing Container %s", spcStep.getInput().getIdentificationBarcode())
          : String.format("Scanned new Sequencing Container %s", stringStep.getInput());
    }

    @Override
    public void processInput(SequencerPartitionContainerProgressStep step) {
      spcStep = step;
    }

    @Override
    public void processInput(StringProgressStep step) {
      stringStep = step;
    }
  }

  private class ScanModelStep implements WorkflowStep {
    private SequencingContainerModelProgressStep modelStep;
    private StringProgressStep stringStep;

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.SEQUENCING_CONTAINER_MODEL, InputType.STRING),
          "Scan the REF number of the Sequencing Container");
    }

    @Override
    public ProgressStep getProgressStep() {
      return modelStep != null ? modelStep : stringStep;
    }

    @Override
    public void cancelInput() {
      modelStep = null;
      stringStep = null;
    }

    @Override
    public String getLogMessage() {
      return modelStep != null ? String.format("Selected Sequencing Container Model %s", modelStep.getInput().getIdentificationBarcode())
          : String.format("Selected unknown Sequencing Container Model %s", stringStep.getInput());
    }

    @Override
    public void processInput(SequencingContainerModelProgressStep step) {
      this.modelStep = step;
    }

    @Override
    public void processInput(StringProgressStep step) {
      this.stringStep = step;
    }
  }
}
