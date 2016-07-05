package uk.ac.bbsrc.tgac.miso.notification.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transform data as a step in writing it to a message.
 *
 * @param <I>
 *          The type this transformation will receive
 * @param <O>
 *          The type this transformation will emit
 */
public abstract class RunTransform<I, O> extends RunSink<I> {
  private final List<RunSink<O>> children = new ArrayList<>();
  protected final Logger log;

  public RunTransform() {
    log = LoggerFactory.getLogger(getClass());
  }

  /**
   * Add a sink to receive the transformed value.
   * 
   * @see attachTo
   */
  public RunTransform<I, O> add(RunSink<O> child) {
    children.add(child);
    return this;
  }

  @Override
  public RunTransform<I, O> attachTo(RunTransform<?, I> parent) {
    parent.add(this);
    return this;
  }

  /**
   * Transform the data.
   * 
   * @param input
   *          The received value
   * @return the transformed value to send to the attached sinks, or null to stop processing
   */
  protected abstract O convert(I input, IlluminaRunMessage output) throws Exception;

  @Override
  public final void process(I input, IlluminaRunMessage output) throws Exception {
    O converted = convert(input, output);
    if (converted == null) {
      log.info("Conversion failed: " + input.toString() + " by " + getClass().getName());
      return;
    }

    for (RunSink<O> child : children) {
      O forChild = uniqueForChild(converted, output);
      if (forChild != null) child.process(forChild, output);
    }
  }

  /**
   * Do a reset operation on the data, and potentially return null to stop processing.
   */
  protected O uniqueForChild(O item, IlluminaRunMessage output) {
    return item;
  }
}
