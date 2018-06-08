package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

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
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractWorkflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;

public class LoadSequencerWorkflow extends AbstractWorkflow {
  private final ContainerStep containerStep = new ContainerStep();
  private final ContainerModelStep containerModelStep = new ContainerModelStep();
  private List<PartitionStep> partitionSteps = Collections.emptyList();

  @Override
  protected List<WorkflowStep> getCompletedSteps() {
    return Stream.concat(Stream.of(containerStep, containerModelStep), partitionSteps.stream()).filter(this::hasInput).collect(Collectors.toList());
  }

  private boolean hasInput(WorkflowStep step) {
    return step.getProgressStep() != null;
  }

  @Override
  protected WorkflowName getWorkflowName() {
    return WorkflowName.LOAD_SEQUENCER;
  }

  @Override
  public WorkflowStepPrompt getStep(int stepNumber) {
    switch (stepNumber) {
    case 0:
      return containerStep.getPrompt();
    case 1:
      return containerStep.isExistingContainer() ? partitionSteps.get(0).getPrompt() : containerModelStep.getPrompt();
    default:
      return partitionSteps.get(asPartitionIndex(stepNumber)).getPrompt();
    }
  }

  private int asPartitionIndex(int stepNumber) {
    return stepNumber - (containerStep.isExistingContainer() ? 1 : 2);
  }

  @Override
  public boolean isComplete() {
    return partitionSteps.size() > 0 && Stream.concat(Stream.of(containerStep), partitionSteps.stream()).allMatch(this::hasInput);
  }

  @Override
  public void processInput(int stepNumber, ProgressStep step) {
    if (stepNumber == 0) {
      step.accept(containerStep);
      containerModelStep.cancelInput();
      for (PartitionStep partitionStep : partitionSteps) {
        partitionStep.cancelInput();
      }

      if (containerStep.isExistingContainer()) {
        resetPartitionSteps(containerStep.getContainer().getModel());
      }
    } else if (stepNumber == 1) {
      if (containerStep.isExistingContainer()) {
        step.accept(partitionSteps.get(0));
      } else {
        step.accept(containerModelStep);
        resetPartitionSteps(containerModelStep.getModel());
      }
    } else {
      int partitionIndex = asPartitionIndex(stepNumber);
      step.accept(partitionSteps.get(partitionIndex));
    }
  }

  private void resetPartitionSteps(SequencingContainerModel model) {
    partitionSteps = new ArrayList<>();
    for (int i = 0; i < model.getPartitionCount(); ++i) {
      partitionSteps.add(new PartitionStep(i, model.getPlatformType()));
    }
  }

  @Override
  public void cancelInput() {
    if (partitionSteps.stream().anyMatch(this::hasInput)) {
      getLastCompletedPartitionStep().cancelInput();
    } else if (hasInput(containerModelStep)) {
      containerModelStep.cancelInput();
    } else {
      containerStep.cancelInput();
    }
  }

  private PartitionStep getLastCompletedPartitionStep() {
    return partitionSteps.stream().filter(this::hasInput).reduce((first, second) -> second).orElse(null);
  }

  @Override
  public String getConfirmMessage() {
    List<Pool> poolsScanned = partitionSteps.stream().map(PartitionStep::getPool).filter(Objects::nonNull).collect(Collectors.toList());
    if (poolsScanned.isEmpty()) {
      if (containerStep.isExistingContainer()) {
        return String.format("Remove all pools from %s '%s'?", getContainerName(), containerStep.getContainer().getIdentificationBarcode());
      } else {
        return String.format("Create empty %s '%s'?", getContainerName(), containerStep.getBarcode());
      }
    } else {
      if (containerStep.isExistingContainer()) {
        return String.format("Add pools to existing %s '%s'?", getContainerName(), containerStep.getContainer().getIdentificationBarcode());
      } else {
        return String.format("Create %s '%s'?", getContainerName(), containerStep.getBarcode());
      }
    }
  }

  private String getContainerName() {
    if (containerModelStep.getProgressStep() != null) {
      return containerModelStep.getModel().getPlatformType().getContainerName();
    } else if (containerStep.getProgressStep() != null && containerStep.isExistingContainer()) {
      return containerStep.getContainer().getModel().getPlatformType().getContainerName();
    } else {
      return "Sequencing Container";
    }
  }

  @Override
  public void execute(WorkflowExecutor workflowExecutor) throws IOException {
    if (!isComplete()) throw new IllegalStateException("Workflow is not complete");

    SequencerPartitionContainer spc;
    if (containerStep.isExistingContainer()) {
      spc  = containerStep.getContainer();
    } else {
      spc = new SequencerPartitionContainerImpl();
      SequencingContainerModel model = containerModelStep.getModel();
      spc.setModel(model);
      spc.setIdentificationBarcode(containerStep.getBarcode());
      spc.setPartitionLimit(model.getPartitionCount());
    }
    for (int i = 0; i < partitionSteps.size(); ++i) {
      spc.getPartitionAt(i + 1).setPool(partitionSteps.get(i).getPool());
    }
    workflowExecutor.save(spc);
  }


  private static class PartitionStep implements WorkflowStep {
    private final int partitionIndex;
    private final PlatformType platformType;

    private PoolProgressStep poolStep;
    private SkipProgressStep skipStep;

    PartitionStep(int partitionIndex, PlatformType platformType) {
      this.partitionIndex = partitionIndex;
      this.platformType = platformType;
    }

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.POOL, InputType.SKIP),
          String.format("Scan a Pool to assign to %s %d", platformType.getPartitionName(), partitionIndex + 1));
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
      if (poolStep == null) {
        return String.format("Selected NO POOL for %s %d", platformType.getPartitionName(), partitionIndex + 1);
      }
      Pool pool = poolStep.getInput();
      return String.format("Selected Pool %s (%s) for %s %d", pool.getAlias(), pool.getName(), platformType.getPartitionName(),
          partitionIndex + 1);
    }

    @Override
    public void processInput(PoolProgressStep step) {
      this.poolStep = step;
    }

    @Override
    public void processInput(SkipProgressStep step) {
      this.poolStep = null;
      this.skipStep = step;
    }

    /**
     * Return Pool received or null
     */
    public Pool getPool() {
      return poolStep == null ? null : poolStep.getInput();
    }
  }

  private static class ContainerStep implements WorkflowStep {

    private static final WorkflowStepPrompt PROMPT = new WorkflowStepPrompt(Sets.newHashSet(InputType.SEQUENCER_PARTITION_CONTAINER,
        InputType.STRING),
        "Scan the Sequencing Container serial number (SN)");

    private SequencerPartitionContainerProgressStep containerStep;
    private StringProgressStep stringStep;

    @Override
    public WorkflowStepPrompt getPrompt() {
      return PROMPT;
    }

    @Override
    public ProgressStep getProgressStep() {
      return containerStep != null ? containerStep : stringStep;
    }

    @Override
    public void cancelInput() {
      containerStep = null;
      stringStep = null;
    }

    @Override
    public String getLogMessage() {
      return String.format("Selected %s %s '%s'", containerStep == null ? "new" : "existing", getContainerName(), getBarcode());
    }

    private String getContainerName() {
      if (containerStep == null) {
        return "Sequencing Container";
      } else {
        return containerStep.getInput().getModel().getPlatformType().getContainerName();
      }
    }

    @Override
    public void processInput(SequencerPartitionContainerProgressStep step) {
      stringStep = null;
      containerStep = step;
    }

    @Override
    public void processInput(StringProgressStep step) {
      containerStep = null;
      stringStep = step;
    }

    public boolean isExistingContainer() {
      return containerStep != null;
    }

    public SequencerPartitionContainer getContainer() {
      return containerStep.getInput();
    }

    public String getBarcode() {
      return containerStep == null ? stringStep.getInput() : containerStep.getInput().getIdentificationBarcode();
    }
  }

  private static class ContainerModelStep implements WorkflowStep {

    private static final WorkflowStepPrompt PROMPT = new WorkflowStepPrompt(Sets.newHashSet(InputType.SEQUENCING_CONTAINER_MODEL),
        "Scan the Sequencing Container part number (REF)");

    private SequencingContainerModelProgressStep modelStep;

    @Override
    public WorkflowStepPrompt getPrompt() {
      return PROMPT;
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
      return String.format("Selected %s Model '%s'", modelStep.getInput().getPlatformType().getContainerName(),
          modelStep.getInput().getAlias());
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
