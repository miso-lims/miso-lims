package uk.ac.bbsrc.tgac.miso.notification.service;

public class All<T> extends RunTransform<T, T> {

  @Override
  protected T convert(T input) throws Exception {
    return input;
  }

}
