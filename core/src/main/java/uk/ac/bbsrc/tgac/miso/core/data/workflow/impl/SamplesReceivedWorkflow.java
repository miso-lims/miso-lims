package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

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

public class SamplesReceivedWorkflow extends AbstractWorkflow {

  private final SampleStep sampleStep = new SampleStep();
  private final QCStep qcStep = new QCStep();
  private final AliquotStep aliquotStep = new AliquotStep();

  @Override
  protected List<WorkflowStep> getCompletedSteps() {
    return Stream.of(sampleStep, qcStep, aliquotStep).filter(this::hasInput).collect(Collectors.toList());
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
    switch (stepNumber) {
    case 0:
      return sampleStep.getPrompt();
    case 1:
      return qcStep.getPrompt();
    default:
      return aliquotStep.getPrompt();
    }
  }

  @Override
  public boolean isComplete() {
    return Stream.of(sampleStep, qcStep, aliquotStep).allMatch(this::hasInput);
  }

  @Override
  public void processInput(int stepNumber, ProgressStep step) {
    switch (stepNumber) {
    case 0:
      step.accept(sampleStep);
      qcStep.cancelInput();
      aliquotStep.cancelInput();
      break;
    case 1:
      step.accept(qcStep);
      aliquotStep.cancelInput();
      break;
    default:
      step.accept(aliquotStep);
      break;
    }
  }

  @Override
  public void cancelInput() {
    if (hasInput(aliquotStep)) {
      aliquotStep.cancelInput();
    } else if (hasInput(qcStep)) {
      qcStep.cancelInput();
    } else if (hasInput(sampleStep)) {
      sampleStep.cancelInput();
    }
  }

  @Override
  public String getConfirmMessage() {
    return "Add a Qubit QC with value " + qcStep.getQCValue() + "ng/µl to Stock '" + sampleStep.getAlias() + "' and propagate to "
        + aliquotStep.getAliquotQuantity() + " Aliquot(s)?";
  }

  @Override
  public void execute(WorkflowExecutor workflowExecutor) throws IOException {
    if (!isComplete()) throw new IllegalStateException("Workflow is not complete");

    Sample sample = sampleStep.getSample();
    SampleQC qc = new SampleQC();

    qc.setSample(sample);
    qc.setType(workflowExecutor.getQcTypeList().stream().filter(type -> type.getQcTarget() == QcTarget.Sample).findFirst().orElse(null));
    qc.setResults(qcStep.getQCValue());

    Collection<SampleQC> qcs = sample.getQCs();
    qcs.add(qc);
    sample.setQCs(qcs);
    
    for (int i = 0; i < aliquotStep.getAliquotQuantity(); i++) {
      SampleAliquot aliquot = workflowExecutor.createAliquotFromParent(sample);
      workflowExecutor.save(aliquot);
    }

    workflowExecutor.save(qc);
    workflowExecutor.save(sample);
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
