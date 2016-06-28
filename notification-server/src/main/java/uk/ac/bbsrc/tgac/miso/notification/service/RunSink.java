package uk.ac.bbsrc.tgac.miso.notification.service;

/**
 * Write information from an input to a message about sequencer status.
 * 
 * @param <T>
 *          the type of input data
 */
public abstract class RunSink<T> {
  /**
   * Do the writing!
   * 
   * @param input
   *          the data given by the upstream processing
   * @param output
   *          the sequencing message to be sent
   */
  public abstract void process(T input, IlluminaRunMessage output) throws Exception;

  /**
   * Consume the output of a transformation step.
   */
  public RunSink<T> attachTo(RunTransform<?, T> parent) {
    parent.add(this);
    return this;
  }
}
