package uk.ac.bbsrc.tgac.miso.notification.service;

import java.util.ArrayList;
import java.util.List;

public abstract class RunTransform<I, O> extends RunSink<I> {
  private final List<RunSink<O>> children = new ArrayList<>();

  public RunTransform<I, O> add(RunSink<O> child) {
    children.add(child);
    return this;
  }

  protected abstract O convert(I input) throws Exception;

  @Override
  public final void process(I input, IlluminaRunStatus output) throws Exception {
    O converted = convert(input);
    if (converted == null) {
      return;
    }

    for (RunSink<O> child : children) {
      child.process(converted, output);
    }
  }

  @Override
  public RunTransform<I, O> attachTo(RunTransform<?, I> parent) {
    parent.add(this);
    return this;
  }
}
