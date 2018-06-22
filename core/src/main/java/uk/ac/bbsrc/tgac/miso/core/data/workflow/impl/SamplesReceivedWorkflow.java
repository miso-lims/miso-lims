package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractWorkflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;

public class SamplesReceivedWorkflow extends AbstractWorkflow {

  private final SampleStep sampleStep = new SampleStep();
  private final QCStep qcStep = new QCStep();
  private final AliquotStep aliquotStep = new AliquotStep();
  private final BoxStep boxStep = new BoxStep();
  private List<WorkflowStep> aliquotHandlingSteps = Collections.emptyList();

  private final Set<String> occupiedLocations = new HashSet<>();

  @Override
  protected List<WorkflowStep> getCompletedSteps() {
    return Stream.concat(Stream.of(sampleStep, qcStep, aliquotStep, boxStep), aliquotHandlingSteps.stream()).filter(this::hasInput)
        .collect(Collectors.toList());
  }

  private boolean hasInput(WorkflowStep step) {
    return step.getProgressStep() != null;
  }

  @Override
  protected WorkflowName getWorkflowName() {
    return WorkflowName.SAMPLES_RECEIVED;
  }

  @Override
  public WorkflowStepPrompt getStep(int stepNumber) {
    if (stepNumber == 0) {
      return sampleStep.getPrompt();
    } else if (stepNumber == 1) {
      return qcStep.getPrompt();
    } else if (stepNumber == 2) {
      return aliquotStep.getPrompt();
    } else if (stepNumber == 3) {
      return boxStep.getPrompt();
    } else {
      return aliquotHandlingSteps.get(asAliquotIndex(stepNumber)).getPrompt();
    }
  }

  private int asAliquotIndex(int stepNumber) {
    return stepNumber - 4;
  }

  @Override
  public boolean isComplete() {
    return Stream.concat(Stream.of(sampleStep, qcStep, aliquotStep, boxStep), aliquotHandlingSteps.stream()).allMatch(this::hasInput);
  }

  @Override
  public List<String> processInput(int stepNumber, ProgressStep step) {
    List<String> errors = new ArrayList<>();
    if (stepNumber == 0) {
      step.accept(sampleStep);
      qcStep.cancelInput();
      aliquotStep.cancelInput();
      boxStep.cancelInput();
      cancelAliquotHandlingSteps();
    } else if (stepNumber == 1) {
      step.accept(qcStep);
      aliquotStep.cancelInput();
      boxStep.cancelInput();
      cancelAliquotHandlingSteps();
    } else if (stepNumber == 2) {
      step.accept(aliquotStep);
      boxStep.cancelInput();
      boxStep.setNumAliquots(aliquotStep.getAliquotQuantity());
      cancelAliquotHandlingSteps();
    } else if (stepNumber == 3) {
      step.accept(boxStep);
      if (hasInput(boxStep)) {
        resetAliquotHandlingSteps(aliquotStep.getAliquotQuantity(), boxStep.getBox());
      } else {
        errors.add(boxStep.getError());
      }
    } else {
      step.accept(aliquotHandlingSteps.get(asAliquotIndex(stepNumber)));
      if (aliquotHandlingSteps.get(asAliquotIndex(stepNumber)).getError() != null) {
        errors.add(aliquotHandlingSteps.get(asAliquotIndex(stepNumber)).getError());
      }
    }
    return errors;
  }

  private void cancelAliquotHandlingSteps() {
    for (WorkflowStep aliquotHandlingStep : aliquotHandlingSteps) {
      aliquotHandlingStep.cancelInput();
    }
  }

  private void resetAliquotHandlingSteps(int aliquotQuantity, Box box) {
    aliquotHandlingSteps = new ArrayList<>();
    for (int i = 0; i < aliquotQuantity; i++) {
      aliquotHandlingSteps.add(new BoxPositionStep(i, box, occupiedLocations));
      aliquotHandlingSteps.add(new VolumeStep(i));
      aliquotHandlingSteps.add(new ConcentrationStep(i));
    }
  }

  @Override
  public void cancelInput() {
    if (aliquotHandlingSteps.stream().anyMatch(this::hasInput)) {
      getLastCompletedAliquotHandlingStep().cancelInput();
    } else if (hasInput(boxStep)) {
      boxStep.cancelInput();
    } else if (hasInput(aliquotStep)) {
      aliquotStep.cancelInput();
    } else if (hasInput(qcStep)) {
      qcStep.cancelInput();
    } else if (hasInput(sampleStep)) {
      sampleStep.cancelInput();
    }
  }

  private WorkflowStep getLastCompletedAliquotHandlingStep() {
    return aliquotHandlingSteps.stream().filter(this::hasInput).reduce((first, second) -> second).orElse(null);
  }

  @Override
  public String getConfirmMessage() {
    return "Add a Qubit QC with value " + qcStep.getQCValue() + "ng/µl to Stock '" + sampleStep.getAlias() + "', propagate to "
        + aliquotStep.getAliquotQuantity() + " Aliquot(s) and insert them into Box '" + boxStep.getAlias()
        + "' with the specified locations and values?";
  }

  @Override
  public void execute(WorkflowExecutor workflowExecutor) throws IOException {
    if (!isComplete()) throw new IllegalStateException("Workflow is not complete");

    Sample sample = sampleStep.getSample();
    SampleQC qc = new SampleQC();
    Box box = boxStep.getBox();

    qc.setSample(sample);
    qc.setType(workflowExecutor.getQcTypeList().stream().filter(type -> type.getQcTarget() == QcTarget.Sample).findFirst().orElse(null));
    qc.setResults(qcStep.getQCValue());

    Collection<SampleQC> qcs = sample.getQCs();
    qcs.add(qc);
    sample.setQCs(qcs);
    
    for (int i = 0; i < aliquotStep.getAliquotQuantity(); i++) {
      SampleAliquot aliquot = workflowExecutor.createAliquotFromParent(sample);
      aliquot.setVolume(((VolumeStep) aliquotHandlingSteps.get(3 * i + 1)).getVolume());
      aliquot.setConcentration(((ConcentrationStep) aliquotHandlingSteps.get(3 * i + 2)).getConcentration());
      workflowExecutor.save(aliquot);
      box.setBoxable(((BoxPositionStep) aliquotHandlingSteps.get(3 * i)).getPosition(), BoxableView.fromBoxable(aliquot));

    }

    workflowExecutor.save(box);
    workflowExecutor.save(qc);
    workflowExecutor.save(sample);
  }
  
  private static class ConcentrationStep implements WorkflowStep {

    private PositiveDoubleProgressStep concStep;

    private final int aliquotIndex;

    ConcentrationStep(int aliquotIndex) {
      this.aliquotIndex = aliquotIndex;
    }

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.POSITIVE_DOUBLE), String.format("Enter the Concentration for Aliquot #%d"
          + " (in ng/µl)", aliquotIndex + 1));
    }

    @Override
    public ProgressStep getProgressStep() {
      return concStep;
    }

    @Override
    public void cancelInput() {
      concStep = null;
    }

    @Override
    public String getLogMessage() {
      return String.format("Set Aliquot #%d to have a Concentration of %fng/µl", aliquotIndex + 1, getConcentration());
    }

    @Override
    public void processInput(PositiveDoubleProgressStep step) {
      concStep = step;
    }

    public double getConcentration() {
      return concStep.getInput();
    }

  }

  private static class VolumeStep implements WorkflowStep {

    private PositiveDoubleProgressStep volumeStep;

    private final int aliquotIndex;

    VolumeStep(int aliquotIndex) {
      this.aliquotIndex = aliquotIndex;
    }

    @Override
    public WorkflowStepPrompt getPrompt() {
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.POSITIVE_DOUBLE), String.format("Enter the Volume for Aliquot #%d (in µl)",
          aliquotIndex + 1));
    }

    @Override
    public ProgressStep getProgressStep() {
      return volumeStep;
    }

    @Override
    public void cancelInput() {
      volumeStep = null;
    }

    @Override
    public String getLogMessage() {
      return String.format("Set Aliquot #%d to have a Volume of %fµl", aliquotIndex + 1, getVolume());
    }

    @Override
    public void processInput(PositiveDoubleProgressStep step) {
      volumeStep = step;
    }

    public double getVolume() {
      return volumeStep.getInput();
    }

  }

  private static class BoxPositionStep implements WorkflowStep {

    private BoxPositionProgressStep boxPositionStep;

    private final int aliquotIndex;
    private final Box box;
    private String firstFreePosition;
    private final Set<String> occupiedLocations;

    private String error;

    BoxPositionStep(int aliquotIndex, Box box, Set<String> occupiedLocations) {
      this.aliquotIndex = aliquotIndex;
      this.box = box;
      this.occupiedLocations = occupiedLocations;
    }

    @Override
    public WorkflowStepPrompt getPrompt() {
      firstFreePosition = getFirstFreePosition();
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.BOX_POSITION, InputType.SKIP),
          String.format("Enter a Box Position to place Aliquot #%d.\nSkipping will default to position %s", aliquotIndex + 1,
              firstFreePosition));
    }

    @Override
    public ProgressStep getProgressStep() {
      return boxPositionStep;
    }

    @Override
    public void cancelInput() {
      occupiedLocations.remove(boxPositionStep.getInput());
      boxPositionStep = null;
    }

    @Override
    public String getLogMessage() {
      return String.format("Place Aliquot #%d at position %s", aliquotIndex + 1, getPosition());
    }

    @Override
    public void processInput(BoxPositionProgressStep step) {
      if (isLocationFree(step.getInput())) {
        boxPositionStep = step;
        occupiedLocations.add(getPosition());
      } else if (!box.isValidPosition(step.getInput()) || !isColumnNonZero(step.getInput())) {
        error = String.format("The Box '%s' does not have a position '%s'", box.getAlias(), step.getInput());
      } else if (!box.isFreePosition(step.getInput()) || occupiedLocations.contains(step.getInput())) {
        error = String.format("The position '%s' is already occupied", step.getInput());
      }
    }

    @Override
    public void processInput(SkipProgressStep step) {
      this.boxPositionStep = new BoxPositionProgressStep();
      boxPositionStep.setInput(firstFreePosition);
      occupiedLocations.add(getPosition());
    }

    public String getPosition() {
      return boxPositionStep != null ? boxPositionStep.getInput() : firstFreePosition;
    }

    public boolean isLocationFree(String position) {
      return (boxPositionStep != null && boxPositionStep.getInput().equals(position)) ||
          (box.isValidPosition(position) && isColumnNonZero(position) && box.isFreePosition(position)
              && !occupiedLocations.contains(position));
      // box.isFreePosition does not seem to work properly when the workflow is viewed through the view workflows tab
    }

    public boolean isColumnNonZero(String position) {
      return BoxUtils.tryParseInt(position.substring(1, 3)) != 0;
    }

    public String getFirstFreePosition() {
      for (int i = 0; i < box.getSize().getRows(); i++) {
        for (int j = 1; j <= box.getSize().getColumns(); j++) {
          String position = (char) (i + 'A') + String.format("%02d", j);
          if (isLocationFree(position)) return position;
        }
      }
      return null;
    }

    @Override
    public String getError() {
      return error;
    }

  }

  private static class BoxStep implements WorkflowStep {

    private static final WorkflowStepPrompt PROMPT = new WorkflowStepPrompt(Sets.newHashSet(InputType.BOX), "Scan the Box barcode");

    private BoxProgressStep boxStep;

    private int numAliquots;
    private String error;

    @Override
    public WorkflowStepPrompt getPrompt() {
      return PROMPT;
    }

    @Override
    public ProgressStep getProgressStep() {
      return boxStep;
    }

    @Override
    public void cancelInput() {
      boxStep = null;
    }

    @Override
    public String getLogMessage() {
      return String.format("Place Aliquots in Box '%s'", getAlias());
    }

    @Override
    public void processInput(BoxProgressStep step) {
      if (isBoxBigEnough(step)) {
        boxStep = step;
      } else {
        error = String.format("Box '%s' does not have enough space for %d Aliquots (Box only has %d free spaces)",
            step.getInput().getAlias(), numAliquots, step.getInput().getFreeCount());
      }
    }

    public String getAlias() {
      return boxStep.getInput().getAlias();
    }

    public boolean isBoxBigEnough(BoxProgressStep step) {
      return step.getInput().getFreeCount() >= numAliquots;
    }

    public Box getBox() {
      return boxStep.getInput();
    }

    @Override
    public String getError() {
      return error;
    }

    public void setNumAliquots(int numAliquots) {
      this.numAliquots = numAliquots;
    }
  }

  private static class AliquotStep implements WorkflowStep {

    private static final WorkflowStepPrompt PROMPT = new WorkflowStepPrompt(Sets.newHashSet(InputType.POSITIVE_INTEGER),
        "Enter the number of Aliquots");

    private PositiveIntegerProgressStep aliquotStep;

    @Override
    public WorkflowStepPrompt getPrompt() {
      return PROMPT;
    }

    @Override
    public ProgressStep getProgressStep() {
      return aliquotStep;
    }

    @Override
    public void cancelInput() {
      aliquotStep = null;
    }

    @Override
    public String getLogMessage() {
      return String.format("Create %d Aliquot(s)", getAliquotQuantity());
    }

    @Override
    public void processInput(PositiveIntegerProgressStep step) {
      aliquotStep = step;
    }

    public int getAliquotQuantity() {
      return aliquotStep.getInput();
    }

  }

  private static class QCStep implements WorkflowStep {
    
    private static final WorkflowStepPrompt PROMPT = new WorkflowStepPrompt(Sets.newHashSet(InputType.POSITIVE_DOUBLE),
        "Enter the Qubit QC Value (in ng/µl)");
    
    private PositiveDoubleProgressStep qcStep;
    
    @Override
    public WorkflowStepPrompt getPrompt() {
      return PROMPT;
    }

    @Override
    public ProgressStep getProgressStep() {
      return qcStep;
    }

    @Override
    public void cancelInput() {
      qcStep = null;
    }

    @Override
    public String getLogMessage() {
      return String.format("Set QC value as %f", getQCValue());
    }
    
    @Override
    public void processInput(PositiveDoubleProgressStep step) {
      qcStep = step;
    }
    
    public double getQCValue() {
      return qcStep.getInput();
    }
  }

  private static class SampleStep implements WorkflowStep {

    private static final WorkflowStepPrompt PROMPT = new WorkflowStepPrompt(Sets.newHashSet(InputType.SAMPLE_STOCK),
        "Scan the Stock Barcode");

    private SampleProgressStep sampleStep;

    @Override
    public WorkflowStepPrompt getPrompt() {
      return PROMPT;
    }

    @Override
    public ProgressStep getProgressStep() {
      return sampleStep;
    }

    @Override
    public void cancelInput() {
      sampleStep = null;
    }

    @Override
    public String getLogMessage() {
      return String.format("Selected '%s'", getAlias());
    }

    @Override
    public void processInput(SampleProgressStep step) {
      sampleStep = step;
    }

    public String getAlias() {
      return sampleStep.getInput().getAlias();
    }

    public Sample getSample() {
      return sampleStep.getInput();
    }
  }
}
