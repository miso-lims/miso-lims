package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

public enum DateRangeParser {

  YEAR(
      "^(\\d{4})$") {
    @Override
    protected DateTime parseStart(Matcher matcher) {
      return new DateTime(Integer.parseInt(matcher.group(1)), 1, 1, 0, 0);
    }

    @Override
    protected DateTime parseEnd(Matcher matcher) {
      return parseStart(matcher).plusYears(1);
    }
  },
  MONTH(
      "^(\\d{4})-(\\d{2})$") {
    @Override
    protected DateTime parseStart(Matcher matcher) {
      Matcher m = matcher;
      int year = Integer.parseInt(m.group(1));
      int month = Integer.parseInt(m.group(2));
      return new DateTime(year, month, 1, 0, 0);
    }

    @Override
    protected DateTime parseEnd(Matcher matcher) {
      return parseStart(matcher).plusMonths(1);
    }
  },
  DAY(
      "^(\\d{4})-(\\d{2})-(\\d{2})$") {
    @Override
    protected DateTime parseStart(Matcher matcher) {
      Matcher m = matcher;
      int year = Integer.parseInt(m.group(1));
      int month = Integer.parseInt(m.group(2));
      int day = Integer.parseInt(m.group(3));
      return new DateTime(year, month, day, 0, 0);
    }

    @Override
    protected DateTime parseEnd(Matcher matcher) {
      return parseStart(matcher).plusDays(1);
    }
  },
  LAST_HOUR(
      "^(now|hour|thishour|lasthour)$") {
    @Override
    protected DateTime parseStart(Matcher matcher) {
      return new DateTime().minusHours(1);
    }

    @Override
    protected DateTime parseEnd(Matcher matcher) {
      return new DateTime();
    }
  },
  TODAY(
      "^today$") {
    @Override
    protected DateTime parseStart(Matcher matcher) {
      return new DateTime().withTimeAtStartOfDay();
    }

    @Override
    protected DateTime parseEnd(Matcher matcher) {
      return parseStart(matcher).plusDays(1);
    }
  },
  YESTERDAY(
      "^yesterday$") {
    @Override
    protected DateTime parseStart(Matcher matcher) {
      return parseEnd(matcher).minusDays(1);
    }

    @Override
    protected DateTime parseEnd(Matcher matcher) {
      return new DateTime().withTimeAtStartOfDay();
    }
  },
  THIS_WEEK(
      "^thisweek$") {
    @Override
    protected DateTime parseStart(Matcher matcher) {
      return new DateTime().withDayOfWeek(1).withTimeAtStartOfDay();
    }

    @Override
    protected DateTime parseEnd(Matcher matcher) {
      return parseStart(matcher).plusWeeks(1);
    }
  },
  LAST_WEEK(
      "^lastweek$") {
    @Override
    protected DateTime parseStart(Matcher matcher) {
      return new DateTime().minusWeeks(1).withDayOfWeek(1).withTimeAtStartOfDay();
    }

    @Override
    protected DateTime parseEnd(Matcher matcher) {
      return parseStart(matcher).plusWeeks(1);
    }
  },
  HOURS(
      "^(\\d+)h(?:ours)?$") {
    @Override
    protected DateTime parseStart(Matcher matcher) {
      int hours = Integer.parseInt(matcher.group(1));
      return new DateTime().minusHours(hours);
    }

    @Override
    protected DateTime parseEnd(Matcher matcher) {
      return parseStart(matcher);
    }

    @Override
    public DateTime getUnqualifiedEnd(String text) {
      return new DateTime();
    }
  },
  DAYS(
      "^(\\d+)d(?:ays)?$") {
    @Override
    protected DateTime parseStart(Matcher matcher) {
      int days = Integer.parseInt(matcher.group(1));
      return new DateTime().minusDays(days);
    }

    @Override
    protected DateTime parseEnd(Matcher matcher) {
      return parseStart(matcher);
    }

    @Override
    public DateTime getUnqualifiedEnd(String text) {
      return new DateTime();
    }
  };

  private final Pattern pattern;

  private DateRangeParser(String pattern) {
    this.pattern = Pattern.compile(pattern);
  }

  public boolean matches(String text) {
    return pattern.matcher(text).matches();
  }

  public DateTime getStart(String text) {
    return parseStart(getValidatedMatcher(text));
  }

  public DateTime getEnd(String text) {
    return parseEnd(getValidatedMatcher(text));
  }

  public DateTime getUnqualifiedEnd(String text) {
    return getEnd(text);
  }

  private Matcher getValidatedMatcher(String text) {
    Matcher m = pattern.matcher(text);
    if (!m.matches()) {
      throw new IllegalArgumentException("Text does not match expected pattern");
    }
    return m;
  }

  protected abstract DateTime parseStart(Matcher matcher);

  protected abstract DateTime parseEnd(Matcher matcher);

}