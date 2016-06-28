package uk.ac.bbsrc.tgac.miso.notification.service;

public abstract class AbstractWriteField<T> extends RunSink<T> {
  public <X> RunTransform<X, X> onlyIfUnset() {
    return new RunTransform<X, X>() {
      @Override
      final protected X convert(X input, IlluminaRunMessage output) throws Exception {
        return input;
      }

      @Override
      protected X uniqueForChild(X item, IlluminaRunMessage output) {
        return getField(output) == null ? item : null;
      }

    };
  }

  /**
   * Read the field
   * 
   * @param info
   *          the structure containing the field
   */
  protected abstract T getField(IlluminaRunMessage info);

  /**
   * Write the field
   * 
   * @param info
   *          the structure containing the field
   * @param value
   *          the new value for the field
   */
  protected abstract void setField(IlluminaRunMessage info, T value);

}
