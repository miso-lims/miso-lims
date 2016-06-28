package uk.ac.bbsrc.tgac.miso.notification.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateInRunName extends RunTransform<String, Date> {
  private static final Pattern MATCH_DATE_IN_RUN = Pattern.compile(".*/(\\d{2})(\\d{2})(\\d{2})_[A-Za-z0-9]+_.*");

  @Override
  protected Date convert(String input, IlluminaRunMessage output) throws Exception {
    Matcher startMatcher = MATCH_DATE_IN_RUN.matcher(output.getFullPath());
    if (startMatcher.matches()) {
      GregorianCalendar c = new GregorianCalendar();
      c.set(Integer.parseInt(startMatcher.group(1)) + 1900, Integer.parseInt(startMatcher.group(2)),
          Integer.parseInt(startMatcher.group(3)));
      return c.getTime();
    }
    return null;
  }
}
