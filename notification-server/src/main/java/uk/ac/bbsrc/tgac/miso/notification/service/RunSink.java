package uk.ac.bbsrc.tgac.miso.notification.service;

public abstract class RunSink<T> {
  public abstract void process(T input, IlluminaRunStatus output) throws Exception;

  public RunSink<T> attachTo(RunTransform<?, T> parent) {
    parent.add(this);
    return this;
  }
}
