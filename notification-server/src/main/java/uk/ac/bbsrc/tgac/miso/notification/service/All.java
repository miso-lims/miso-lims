package uk.ac.bbsrc.tgac.miso.notification.service;

/**
 * A fanout that does not transform the data
 */
public class All<T> extends RunTransform<T, T> {

  @SafeVarargs
  public All(RunSink<T>... children) {
    super();
    for (RunSink<T> child : children) {
      this.add(child);
    }
  }

  @Override
  protected T convert(T input, IlluminaRunMessage output) throws Exception {
    return input;
  }

}
