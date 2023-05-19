package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DateRangeParser {


  YEAR("^(\\d{4})$") {
    @Override
    protected ZonedDateTime parseStart(Matcher matcher) {
      return makeDate(Integer.parseInt(matcher.group(1)), 0, 1, 0, 0);
    }

    @Override
    protected ZonedDateTime parseEnd(Matcher matcher) {
      return parseStart(matcher).plusYears(1);
    }
  },
  MONTH("^(\\d{4})-(\\d{2})$") {
    @Override
    protected ZonedDateTime parseStart(Matcher matcher) {
      Matcher m = matcher;
      int year = Integer.parseInt(m.group(1));
      int month = Integer.parseInt(m.group(2)) - 1;
      return makeDate(year, month, 0, 0, 0);
    }

    @Override
    protected ZonedDateTime parseEnd(Matcher matcher) {
      return parseStart(matcher).plusMonths(1);
    }
  },
  DAY("^(\\d{4})-(\\d{2})-(\\d{2})$") {
    @Override
    protected ZonedDateTime parseStart(Matcher matcher) {
      int year = Integer.parseInt(matcher.group(1));
      int month = Integer.parseInt(matcher.group(2)) - 1;
      int day = Integer.parseInt(matcher.group(3));
      return makeDate(year, month, day, 0, 0);
    }

    @Override
    protected ZonedDateTime parseEnd(Matcher matcher) {
      return parseStart(matcher).plusMonths(1);
    }
  },
  LAST_HOUR("^(now|hour|thishour|lasthour)$") {
    @Override
    protected ZonedDateTime parseStart(Matcher matcher) {
      return ZonedDateTime.now().minusHours(1);
    }

    @Override
    protected ZonedDateTime parseEnd(Matcher matcher) {
      return ZonedDateTime.now();
    }
  },
  TODAY("^today$") {
    @Override
    protected ZonedDateTime parseStart(Matcher matcher) {
      ZonedDateTime now = ZonedDateTime.now();
      return makeDate(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0);
    }

    @Override
    protected ZonedDateTime parseEnd(Matcher matcher) {
      return parseStart(matcher).plusDays(1);
    }
  },
  YESTERDAY("^yesterday$") {
    @Override
    protected ZonedDateTime parseStart(Matcher matcher) {
      return parseEnd(matcher).minusDays(1);
    }

    @Override
    protected ZonedDateTime parseEnd(Matcher matcher) {
      ZonedDateTime now = ZonedDateTime.now();
      return makeDate(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0);
    }
  },
  THIS_WEEK("^thisweek$") {
    @Override
    protected ZonedDateTime parseStart(Matcher matcher) {
      ZonedDateTime dateTime = ZonedDateTime.now();
      while (dateTime.getDayOfWeek() != DayOfWeek.SUNDAY) {
        dateTime = dateTime.minusDays(1);
      }
      return makeDate(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), 0, 0);
    }

    @Override
    protected ZonedDateTime parseEnd(Matcher matcher) {
      return parseStart(matcher).plusWeeks(1);
    }
  },
  LAST_WEEK("^lastweek$") {
    @Override
    protected ZonedDateTime parseStart(Matcher matcher) {
      ZonedDateTime dateTime = ZonedDateTime.now().minusWeeks(1);
      while (dateTime.getDayOfWeek() != DayOfWeek.SUNDAY) {
        dateTime = dateTime.minusDays(1);
      }
      return makeDate(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), 0, 0);
    }

    @Override
    protected ZonedDateTime parseEnd(Matcher matcher) {
      return parseStart(matcher).plusWeeks(1);
    }
  },
  HOURS("^(\\d+)h(?:ours)?$") {
    @Override
    protected ZonedDateTime parseStart(Matcher matcher) {
      int hours = Integer.parseInt(matcher.group(1));
      return ZonedDateTime.now().minusHours(hours);
    }

    @Override
    protected ZonedDateTime parseEnd(Matcher matcher) {
      return parseStart(matcher);
    }

    @Override
    public ZonedDateTime getUnqualifiedEnd(String text) {
      return ZonedDateTime.now();
    }
  },
  DAYS("^(\\d+)d(?:ays)?$") {
    @Override
    protected ZonedDateTime parseStart(Matcher matcher) {
      int days = Integer.parseInt(matcher.group(1));
      return ZonedDateTime.now().minusDays(days);
    }

    @Override
    protected ZonedDateTime parseEnd(Matcher matcher) {
      return parseStart(matcher);
    }

    @Override
    public ZonedDateTime getUnqualifiedEnd(String text) {
      return ZonedDateTime.now();
    }
  };

  private final Pattern pattern;

  private DateRangeParser(String pattern) {
    this.pattern = Pattern.compile(pattern);
  }

  public boolean matches(String text) {
    return pattern.matcher(text).matches();
  }

  public ZonedDateTime getStart(String text) {
    return parseStart(getValidatedMatcher(text));
  }

  public ZonedDateTime getEnd(String text) {
    return parseEnd(getValidatedMatcher(text));
  }

  public ZonedDateTime getUnqualifiedEnd(String text) {
    return getEnd(text);
  }

  private Matcher getValidatedMatcher(String text) {
    Matcher m = pattern.matcher(text);
    if (!m.matches()) {
      throw new IllegalArgumentException("Text does not match expected pattern");
    }
    return m;
  }

  protected abstract ZonedDateTime parseStart(Matcher matcher);

  protected abstract ZonedDateTime parseEnd(Matcher matcher);

  private static ZonedDateTime makeDate(int year, int month, int date, int hour, int minute) {
    return ZonedDateTime.of(year, month, date, hour, minute, 0, 0, ZoneId.systemDefault());
  }

}
