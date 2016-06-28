package uk.ac.bbsrc.tgac.miso.notification.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsDate extends RunTransform<String, Date> {
  protected static final Logger log = LoggerFactory.getLogger(AsDate.class);

  private final DateFormat[] formatters;

  AsDate(String... formats) {
    formatters = new DateFormat[formats.length];
    for (int i = 0; i < formats.length; i++) {
      formatters[i] = new SimpleDateFormat(formats[i]);
    }
  }

  @Override
  protected Date convert(String input, IlluminaRunMessage output) throws Exception {
    for (DateFormat formatter : formatters) {
      try {
        return formatter.parse(input);
      } catch (ParseException e) {
      }
    }
    return null;
  }
}
