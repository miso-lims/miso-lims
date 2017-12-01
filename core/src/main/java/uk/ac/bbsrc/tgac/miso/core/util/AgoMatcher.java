package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgoMatcher {
  private final Pattern pattern;
  private final int seconds;

  AgoMatcher(String unit, int seconds) {
    pattern = Pattern.compile("^([0-9]+)" + unit + "s?$", Pattern.CASE_INSENSITIVE);
    this.seconds = seconds;
  }

  /**
   * Check if the text matches and compute the time span in seconds.
   * 
   * @return the number of seconds ago if it matches or 0 if it doesn't match
   */
  public int secondsAgoIfmatches(String text) {
    Matcher matcher = pattern.matcher(text);
    if (matcher.matches()) {
      return seconds * Integer.parseInt(matcher.group(1));
    }
    return 0;
  }
}
