package uk.ac.bbsrc.tgac.miso.notification.service;

import java.util.Date;

public abstract class WriteNewestDateField extends AbstractWriteField<Date> {

  @Override
  public final void process(Date input, IlluminaRunMessage output) throws Exception {
    Date old = getField(output);
    if (old == null) {
      setField(output, input);
    } else if (old.equals(input)) {
    } else {
      IlluminaTransformer.log.warn("In run {}, field changed from {} to {}!", new Object[] { output.getRunName(), old, input });
      if (input.after(old)) {
        setField(output, input);
      }
    }
  }

}
