package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractWorkflow implements Workflow {
  private Progress progress;

  @Override
  public final Progress getProgress() {
    List<WorkflowStep> steps = getCompletedSteps();
    IntStream.range(0, steps.size()).forEach(i -> steps.get(i).getProgressStep().setStepNumber(i));
    progress.setSteps(steps.stream().map(WorkflowStep::getProgressStep).peek(step -> step.setProgress(progress)).collect(Collectors.toList()));
    return progress;
  }

  @Override
  public final void setProgress(Progress progress) {
    if (this.progress != null) throw new IllegalStateException("Progress is already set");
    validateProgress(progress);

    if (progress.getSteps() != null) processInputs(progress.getSteps());
    this.progress = progress;
  }

  private void validateProgress(Progress progress) {
    if (progress == null) throw new IllegalArgumentException("Progress is null");
    if (progress.getWorkflowName() != getWorkflowName()) throw new IllegalArgumentException("Invalid WorkflowName");
  }

  private void processInputs(SortedSet<ProgressStep> steps) {
    for (ProgressStep step : steps) {
      processInput(getNextStepNumber(), step);
    }
  }

  @Override
  public final List<String> getLog() {
    return getCompletedSteps().stream().map(WorkflowStep::getLogMessage).collect(Collectors.toList());
  }

  @Override
  public Integer getNextStepNumber() {
    return isComplete() ? null : getCompletedSteps().size();
  }

  protected abstract List<WorkflowStep> getCompletedSteps();

  protected abstract WorkflowName getWorkflowName();
}
