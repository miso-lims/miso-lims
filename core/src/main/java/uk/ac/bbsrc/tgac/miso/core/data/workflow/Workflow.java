package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import java.util.List;

public interface Workflow {
  Progress getProgress();

  void setProgress(Progress progress);

  WorkflowStepPrompt getNextStep();

  /**
   * @param stepNumber step index
   */
  WorkflowStepPrompt getStep(int stepNumber);

  /**
   * @return whether input has been received for all steps
   */
  boolean isComplete();

  /**
   * @return list of log messages for each step
   */
  List<String> getLog();

  /**
   * Validate and store input for the current step, which corresponds to the result of getNextStep.
   */
  void processInput(ProgressStep step);

  /**
   * Validate and store input for a step identified by the 0-indexed stepNumber.
   * If stepNumber refers to a previous step, an implementation may or may not choose to invalidate future steps.
   */
  void processInput(int stepNumber, ProgressStep step);

  /**
   * Removes the latest step and any effects it caused.
   * Has no effect if no input has been processed.
   */
  void cancelInput();

  enum WorkflowName {
    LOADSEQUENCER
  }
}
