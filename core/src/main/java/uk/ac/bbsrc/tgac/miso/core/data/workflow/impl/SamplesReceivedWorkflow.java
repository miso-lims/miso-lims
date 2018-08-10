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
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractWorkflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep.InputType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowExecutor;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStepPrompt;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;

public class SamplesReceivedWorkflow extends AbstractWorkflow {

  private final StockStep stockStep = new StockStep();
  private final QCStep qcStep = new QCStep();
  private final StockBoxStep stockBoxStep = new StockBoxStep();
  private StockBoxPositionStep stockBoxPositionStep;
  private final AliquotStep aliquotStep = new AliquotStep();
  private final AliquotBoxStep aliquotBoxStep = new AliquotBoxStep();
  private List<WorkflowStep> aliquotHandlingSteps = Collections.emptyList();

  private final Set<String> occupiedLocations = new HashSet<>();

  @Override
  protected List<WorkflowStep> getCompletedSteps() {
    return Stream
        .concat(Stream.of(stockStep, qcStep, stockBoxStep, stockBoxPositionStep, aliquotStep, aliquotBoxStep),
            aliquotHandlingSteps.stream())
        .filter(this::hasInput)
        .collect(Collectors.toList());
  }

  private boolean hasInput(WorkflowStep step) {
    return step != null && step.getProgressStep() != null;
  }

  @Override
  protected WorkflowName getWorkflowName() {
    return WorkflowName.SAMPLES_RECEIVED;
  }

  @Override
  public WorkflowStepPrompt getStep(int stepNumber) {
    switch (stepNumber) {
    case 0:
      return stockStep.getPrompt();
    case 1:
      return qcStep.getPrompt();
    case 2:
      return stockBoxStep.getPrompt();
    case 3:
      return stockBoxPositionStep.getPrompt();
    case 4:
      return aliquotStep.getPrompt();
    case 5:
      return aliquotBoxStep.getPrompt();
    default:
      return aliquotHandlingSteps.get(asAliquotIndex(stepNumber)).getPrompt();
    }
  }

  private int asAliquotIndex(int stepNumber) {
    return stepNumber - 6;
  }

  @Override
  public boolean isComplete() {
    return Stream.concat(Stream.of(stockStep, qcStep, stockBoxStep, stockBoxPositionStep, aliquotStep, aliquotBoxStep),
        aliquotHandlingSteps.stream()).allMatch(this::hasInput);
  }

  @Override
  public List<String> processInput(int stepNumber, ProgressStep step) {
    List<String> errors = new ArrayList<>();
    switch (stepNumber) {
    case 0:
      step.accept(stockStep);
      qcStep.cancelInput();
      stockBoxStep.cancelInput();
      cancelStockBoxPositionStep();
      aliquotStep.cancelInput();
      aliquotBoxStep.cancelInput();
      cancelAliquotHandlingSteps();
      break;
    case 1:
      step.accept(qcStep);
      stockBoxStep.cancelInput();
      cancelStockBoxPositionStep();
      aliquotStep.cancelInput();
      aliquotBoxStep.cancelInput();
      cancelAliquotHandlingSteps();
      break;
    case 2:
      step.accept(stockBoxStep);
      if (hasInput(stockBoxStep)) {
        stockBoxPositionStep = new StockBoxPositionStep(stockBoxStep.getBox());
        aliquotStep.cancelInput();
        aliquotBoxStep.cancelInput();
        cancelAliquotHandlingSteps();
      } else {
        errors.add(stockBoxStep.getError());
      }
      break;
    case 3:
      step.accept(stockBoxPositionStep);
      if (hasInput(stockBoxPositionStep)) {
        aliquotStep.cancelInput();
        aliquotBoxStep.cancelInput();
        cancelAliquotHandlingSteps();
      } else {
        errors.add(stockBoxPositionStep.getError());
      }
      break;
    case 4:
      step.accept(aliquotStep);
      aliquotBoxStep.cancelInput();
      aliquotBoxStep.setNumAliquots(aliquotStep.getAliquotQuantity());
      cancelAliquotHandlingSteps();
      break;
    case 5:
      step.accept(aliquotBoxStep);
      if (hasInput(aliquotBoxStep)) {
        resetAliquotHandlingSteps(aliquotStep.getAliquotQuantity(), aliquotBoxStep.getBox());
      } else {
        errors.add(aliquotBoxStep.getError());
      }
      break;
    default:
      step.accept(aliquotHandlingSteps.get(asAliquotIndex(stepNumber)));
      if (aliquotHandlingSteps.get(asAliquotIndex(stepNumber)).getError() != null) {
        errors.add(aliquotHandlingSteps.get(asAliquotIndex(stepNumber)).getError());
      }
      break;
    }
    return errors;
  }

  private void cancelAliquotHandlingSteps() {
    for (WorkflowStep aliquotHandlingStep : aliquotHandlingSteps) {
      aliquotHandlingStep.cancelInput();
    }
  }

  private void cancelStockBoxPositionStep() {
    if (stockBoxPositionStep != null) {
      stockBoxPositionStep.cancelInput();
    }
  }

  private void resetAliquotHandlingSteps(int aliquotQuantity, Box box) {
    aliquotHandlingSteps = new ArrayList<>();
    for (int i = 0; i < aliquotQuantity; i++) {
      aliquotHandlingSteps.add(new AliquotBoxPositionStep(i, box, occupiedLocations));
      aliquotHandlingSteps.add(new VolumeStep(i));
      aliquotHandlingSteps.add(new ConcentrationStep(i));
    }
  }

  @Override
  public void cancelInput() {
    if (aliquotHandlingSteps.stream().anyMatch(this::hasInput)) {
      getLastCompletedAliquotHandlingStep().cancelInput();
    } else if (hasInput(aliquotBoxStep)) {
      aliquotBoxStep.cancelInput();
    } else if (hasInput(aliquotStep)) {
      aliquotStep.cancelInput();
    } else if (hasInput(stockBoxPositionStep)) {
      stockBoxPositionStep.cancelInput();
    } else if (hasInput(stockBoxStep)) {
      stockBoxStep.cancelInput();
    } else if (hasInput(qcStep)) {
      qcStep.cancelInput();
    } else if (hasInput(stockStep)) {
      stockStep.cancelInput();
    }
  }

  private WorkflowStep getLastCompletedAliquotHandlingStep() {
    return aliquotHandlingSteps.stream().filter(this::hasInput).reduce((first, second) -> second).orElse(null);
  }

  @Override
  public String getConfirmMessage() {
    return "Add a Qubit QC with value " + qcStep.getQCValue() + "ng/µl to Stock '" + stockStep.getAlias() + "', place it in Box '"
        + stockBoxStep.getAlias() + "' at Position '" + stockBoxPositionStep.getPosition() + "', propagate to "
        + aliquotStep.getAliquotQuantity() + " Aliquot(s) and insert them into Box '" + aliquotBoxStep.getAlias()
        + "' with the specified locations and values?";
  }

  @Override
  public void execute(WorkflowExecutor workflowExecutor) throws IOException {
    if (!isComplete()) throw new IllegalStateException("Workflow is not complete");

    Sample sample = stockStep.getSample();
    SampleQC qc = new SampleQC();
    Box aliquotBox = aliquotBoxStep.getBox();
    Box stockBox = stockBoxStep.getBox();

    qc.setSample(sample);
    qc.setType(workflowExecutor.getQcTypeList().stream().filter(type -> type.getQcTarget() == QcTarget.Sample).findFirst().orElse(null));
    qc.setResults(qcStep.getQCValue());

    Collection<SampleQC> qcs = sample.getQCs();
    qcs.add(qc);
    sample.setQCs(qcs);
    
    BoxPosition stockBp = new BoxPosition(stockBox, stockBoxPositionStep.getPosition(), sample.getEntityType(), sample.getId());
    stockBox.getBoxPositions().put(stockBoxPositionStep.getPosition(), stockBp);

    for (int i = 0; i < aliquotStep.getAliquotQuantity(); i++) {
      SampleAliquot aliquot = workflowExecutor.createAliquotFromParent(sample);
      aliquot.setVolume(((VolumeStep) aliquotHandlingSteps.get(3 * i + 1)).getVolume());
      aliquot.setConcentration(((ConcentrationStep) aliquotHandlingSteps.get(3 * i + 2)).getConcentration());
      workflowExecutor.save(aliquot);
      String aliquotPos = ((AliquotBoxPositionStep) aliquotHandlingSteps.get(3 * i)).getPosition();
      BoxPosition aliquotBp = new BoxPosition(aliquotBox, aliquotPos, aliquot.getEntityType(), aliquot.getId());
      aliquotBox.getBoxPositions().put(aliquotPos, aliquotBp);
    }

    workflowExecutor.save(aliquotBox);
    workflowExecutor.save(qc);
    workflowExecutor.save(sample);
    workflowExecutor.save(stockBox);
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

  private static class AliquotBoxPositionStep implements WorkflowStep {

    private BoxPositionProgressStep boxPositionStep;

    private final int aliquotIndex;
    private final Box box;
    private String firstFreePosition;
    private final Set<String> occupiedLocations;

    private String error;

    AliquotBoxPositionStep(int aliquotIndex, Box box, Set<String> occupiedLocations) {
      this.aliquotIndex = aliquotIndex;
      this.box = box;
      this.occupiedLocations = occupiedLocations;
    }

    @Override
    public WorkflowStepPrompt getPrompt() {
      firstFreePosition = getFirstFreePosition();
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.BOX_POSITION, InputType.SKIP),
          String.format("Enter a Box Position to place Aliquot #%d. Skipping will default to position %s", aliquotIndex + 1,
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

  private static class AliquotBoxStep implements WorkflowStep {

    private static final WorkflowStepPrompt PROMPT = new WorkflowStepPrompt(Sets.newHashSet(InputType.BOX),
        "Scan the barcode of the Box for the Aliquots");

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
  
  private static class StockBoxPositionStep implements WorkflowStep {

    private BoxPositionProgressStep boxPositionStep;

    private final Box box;
    private String firstFreePosition;

    private String error;

    StockBoxPositionStep(Box box) {
      this.box = box;
    }

    @Override
    public WorkflowStepPrompt getPrompt() {
      firstFreePosition = getFirstFreePosition();
      return new WorkflowStepPrompt(Sets.newHashSet(InputType.BOX_POSITION, InputType.SKIP),
          String.format("Enter a Box Position to place the Stock. Skipping will default to position %s", firstFreePosition));
    }

    @Override
    public ProgressStep getProgressStep() {
      return boxPositionStep;
    }

    @Override
    public void cancelInput() {
      boxPositionStep = null;
    }

    @Override
    public String getLogMessage() {
      return String.format("Place Stock at position %s", getPosition());
    }

    @Override
    public void processInput(BoxPositionProgressStep step) {
      if (isLocationFree(step.getInput())) {
        boxPositionStep = step;
      } else if (!box.isValidPosition(step.getInput())) {
        error = String.format("The Box '%s' does not have a position '%s'", box.getAlias(), step.getInput());
      } else if (!box.isFreePosition(step.getInput())) {
        error = String.format("The position '%s' is already occupied", step.getInput());
      }
    }

    @Override
    public void processInput(SkipProgressStep step) {
      this.boxPositionStep = new BoxPositionProgressStep();
      boxPositionStep.setInput(firstFreePosition);
    }

    public String getPosition() {
      return boxPositionStep != null ? boxPositionStep.getInput() : firstFreePosition;
    }

    public boolean isLocationFree(String position) {
      return (boxPositionStep != null && boxPositionStep.getInput().equals(position)) ||
          (box.isValidPosition(position) && isColumnNonZero(position) && box.isFreePosition(position));
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

  private static class StockBoxStep implements WorkflowStep {

    private static final WorkflowStepPrompt PROMPT = new WorkflowStepPrompt(Sets.newHashSet(InputType.BOX),
        "Scan the barcode of the Box for the Stock");

    private BoxProgressStep boxStep;

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
      return String.format("Place Stock in Box '%s'", getAlias());
    }

    @Override
    public void processInput(BoxProgressStep step) {
      if (step.getInput().getFreeCount() >= 1) {
        boxStep = step;
      } else {
        error = String.format("Box '%s' is full", step.getInput().getAlias());
      }
    }

    public String getAlias() {
      return boxStep.getInput().getAlias();
    }

    public Box getBox() {
      return boxStep.getInput();
    }

    @Override
    public String getError() {
      return error;
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

  private static class StockStep implements WorkflowStep {

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
