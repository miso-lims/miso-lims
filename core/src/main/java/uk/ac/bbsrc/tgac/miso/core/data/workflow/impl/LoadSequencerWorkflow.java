package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName.LOAD_SEQUENCER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractWorkflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class LoadSequencerWorkflow extends AbstractWorkflow {
  private ScanSpcStep spcStep = new ScanSpcStep();
  private ScanModelStep modelStep = new ScanModelStep();
  private List<PartitionStep> partitionSteps = Collections.emptyList();

  @Override
  protected List<WorkflowStep> getCompletedSteps() {
    return Stream.concat(Stream.of(spcStep, modelStep), partitionSteps.stream()).filter(this::hasInput).collect(Collectors.toList());
  }

  private boolean hasInput(WorkflowStep step) {
    return step.getProgressStep() != null;
  }

  @Override
  protected WorkflowName getWorkflowName() {
    return LOAD_SEQUENCER;
  }

  @Override
  public WorkflowStepPrompt getStep(int stepNumber) {
    switch (stepNumber) {
    case 0:
      return spcStep.getPrompt();
    case 1:
      return spcStep.isKnown() ? partitionSteps.get(0).getPrompt() : modelStep.getPrompt();
    default:
      return partitionSteps.get(asPartitionIndex(stepNumber)).getPrompt();
    }
  }

  private int asPartitionIndex(int stepNumber) {
    return stepNumber - (spcStep.isKnown() ? 1 : 2);
  }

  @Override
  public boolean isComplete() {
    return partitionSteps.size() > 0 && Stream.concat(Stream.of(spcStep), partitionSteps.stream()).allMatch(this::hasInput);
  }

  @Override
  public void processInput(int stepNumber, ProgressStep step) {
    if (stepNumber == 0) {
      step.accept(spcStep);
      modelStep.cancelInput();
      for (PartitionStep partitionStep : partitionSteps) {
        partitionStep.cancelInput();
      }

      if (spcStep.isKnown()) {
        clearPartitionSteps(spcStep.getSpc().getModel().getPartitionCount());
      }
    } else if (stepNumber == 1) {
      if (spcStep.isKnown()) {
        step.accept(partitionSteps.get(0));
      } else {
        step.accept(modelStep);
        clearPartitionSteps(modelStep.getModel().getPartitionCount());
      }
    } else {
      int partitionIndex = asPartitionIndex(stepNumber);
      step.accept(partitionSteps.get(partitionIndex));
    }
  }

  private void clearPartitionSteps(int partitionCount) {
    partitionSteps = new ArrayList<>();
    for (int i = 0; i < partitionCount; ++i) {
      partitionSteps.add(new PartitionStep(i));
    }
  }

  @Override
  public void cancelInput() {
    if (partitionSteps.stream().anyMatch(this::hasInput)) {
      getLastCompletedPartitionStep().cancelInput();
    } else if (hasInput(modelStep)) {
      modelStep.cancelInput();
    } else {
      spcStep.cancelInput();
    }
  }

  private PartitionStep getLastCompletedPartitionStep() {
    return partitionSteps.stream().filter(this::hasInput).reduce((first, second) -> second).orElse(null);
  }

  @Override
  public String getConfirmMessage() {
    List<Pool> poolsScanned = partitionSteps.stream().map(PartitionStep::getPool).filter(Objects::nonNull).collect(Collectors.toList());
    if (poolsScanned.isEmpty()) {
      if (spcStep.isKnown()) return "No modifications to make";
      return String.format("Sequencing Container %s will be saved", spcStep.getBarcode());
    } else {
      String poolStrings = LimsUtils.joinWithConjunction(poolsScanned.stream().map(Pool::getAlias).collect(Collectors.toList()), "and");
      if (spcStep.isKnown()) {
        return String.format("Sequencing Container %s will be modified to contain the following Pools: %s",
            spcStep.getSpc().getIdentificationBarcode(), poolStrings);
      }

      return String.format("Sequencing Container %s will be saved and the following Pools will be added to it: %s", spcStep.getBarcode(),
          poolStrings);
    }
  }

  @Override
  public void execute(WorkflowExecutor workflowExecutor) throws IOException {
    // todo
  }

  @Override
  public String getName() {
    return "Load Sequencer Workflow";
  }

  private class PartitionStep implements WorkflowStep {
    private final int partitionIndex;
    private PoolProgressStep poolStep;
    private SkipProgressStep skipStep;

    PartitionStep(int partitionIndex) {
      this.partitionIndex = partitionIndex;
    }

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.POOL, InputType.SKIP),
          String.format("Scan a Pool to assign to partition %d, or enter no input to skip this partition", partitionIndex + 1));
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
      if (poolStep == null) return String.format("Skipped partition %d", partitionIndex + 1);

      Pool pool = poolStep.getInput();
      return String.format("Selected Pool %s (%s) for partition %d", pool.getAlias(), pool.getName(), partitionIndex + 1);
    }

    @Override
    public void processInput(PoolProgressStep step) {
      this.poolStep = step;
    }

    @Override
    public void processInput(SkipProgressStep step) {
      this.skipStep = step;
    }

    /**
     * Return Pool received or null
     */
    public Pool getPool() {
      return poolStep == null ? null : poolStep.getInput();
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

    public boolean isKnown() {
      return spcStep != null;
    }

    public SequencerPartitionContainer getSpc() {
      return spcStep.getInput();
    }

    public String getBarcode() {
      return spcStep == null ? stringStep.getInput() : spcStep.getInput().getIdentificationBarcode();
    }
  }

  private class ScanModelStep implements WorkflowStep {
    private SequencingContainerModelProgressStep modelStep;

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.SEQUENCING_CONTAINER_MODEL),
          "Scan the REF number of the Sequencing Container");
    }

    @Override
    public ProgressStep getProgressStep() {
      return modelStep;
    }

    @Override
    public void cancelInput() {
      modelStep = null;
    }

    @Override
    public String getLogMessage() {
      return String.format("Selected Sequencing Container Model %s", modelStep.getInput().getIdentificationBarcode());
    }

    @Override
    public void processInput(SequencingContainerModelProgressStep step) {
      this.modelStep = step;
    }

    public SequencingContainerModel getModel() {
      return modelStep.getInput();
    }
  }
}
