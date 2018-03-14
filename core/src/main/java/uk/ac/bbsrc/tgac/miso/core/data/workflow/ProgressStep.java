package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import java.io.Serializable;

/**
 * Holds the data for a single workflow step
 * Each input should have its own step
 */
public interface ProgressStep extends Serializable, Comparable<ProgressStep> {
  Progress getProgress();

  void setProgress(Progress progress);

  int getStepNumber();

  void setStepNumber(int stepNumber);

  /**
   * Part of the Visitor Pattern to use WorkflowStep to validate ProgressStep
   * All implementations of this method should call {@code visitor.processInput(this)}
   * @param visitor WorkflowStep used to validate {@code this}
   */
  void accept(WorkflowStep visitor);

  enum InputType {
    SEQUENCERPARTITIONCONTAINER, POOL, PLATFORM, INTEGER
  }
}
