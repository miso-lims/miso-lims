package uk.ac.bbsrc.tgac.miso.notification.service;

/**
 * Write a value to a field with a check that the field is not being changed.
 * 
 * @param <T>
 *          the type of the field
 */
abstract class WriteCheckedField<T> extends AbstractWriteField<T> {
  private final boolean overwrite;

  public WriteCheckedField() {
    this(true);
  }

  public WriteCheckedField(boolean overwrite) {
    this.overwrite = overwrite;
  }

  @Override
  public final void process(T input, IlluminaRunMessage output) throws Exception {
    T old = getField(output);
    if (old == null) {
      setField(output, input);
    } else if (old.equals(input)) {
    } else {
      IlluminaTransformer.log.warn("In run {}, field changed from {} to {}!", new Object[] { output.getRunName(), old, input });
      if (overwrite) {
        setField(output, input);
      }
    }
  }

}