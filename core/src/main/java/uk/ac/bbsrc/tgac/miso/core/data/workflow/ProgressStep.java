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
}
