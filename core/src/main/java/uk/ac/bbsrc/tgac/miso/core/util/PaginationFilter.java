package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.joda.time.DateTime;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public abstract interface PaginationFilter {
  public final static List<AgoMatcher> AGO_MATCHERS = Arrays.asList(new AgoMatcher("h(|ours?)", 3600),
      new AgoMatcher("d(|ays?)", 3600 * 24));

  public static PaginationFilter date(Date start, Date end, boolean creation) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.restrictPaginationByDate(item, start, end, creation);
      }
    };
  }

  public static PaginationFilter fulfilled(final boolean isFulfilled) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.restrictPaginationByFulfilled(item, isFulfilled);
      }
    };
  }

  public static PaginationFilter health(EnumSet<HealthType> healths) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.restrictPaginationByHealth(item, healths);
      }
    };
  }

  public static PaginationFilter health(HealthType health) {
    return health(EnumSet.of(health));
  }

  public static PaginationFilter[] parse(String request, String currentUser, Consumer<String> errorHandler) {
    return Arrays.stream(request.split("\\s+")).<PaginationFilter> map(x -> {
      if (x.contains(":")) {
        String[] parts = x.split(":", 2);
        switch (parts[0]) {
        case "is":
        case "has":
          switch (parts[1].toLowerCase()) {
          case "fulfilled":
            return fulfilled(true);
          case "active":
          case "order":
          case "ordered":
          case "unfulfilled":
            return fulfilled(false);
          case "unknown":
            return health(HealthType.Unknown);
          case "complete":
          case "completed":
            return health(HealthType.Completed);
          case "failed":
            return health(HealthType.Failed);
          case "started":
            return health(HealthType.Started);
          case "stopped":
            return health(HealthType.Stopped);
          case "running":
            return health(HealthType.Running);
          case "incomplete":
            return health(EnumSet.of(HealthType.Running, HealthType.Started, HealthType.Stopped));
          default:
            errorHandler.accept("No filter for " + parts[1]);
            return null;
          }
        case "created":
        case "createdon":
          return parseDate(parts[1], true);
        case "changed":
        case "modified":
        case "updated":
        case "changedon":
        case "modifiedon":
        case "updatedon":
          return parseDate(parts[1], false);
        case "createdby":
        case "creator":
        case "creater":
          return parseUser(parts[1], currentUser, true);
        case "changedby":
        case "modifier":
        case "updater":
          return parseUser(parts[1], currentUser, false);
        case "platform":
          try {
            return platformType(PlatformType.valueOf(parts[1].toUpperCase()));
          } catch (IllegalArgumentException e) {
            errorHandler.accept("Invalid platform: " + parts[1]);
            return null;
          }
        }
      }
      return query(x);
    }).filter(Objects::nonNull).toArray(PaginationFilter[]::new);
  }

  static PaginationFilter parseDate(String text, boolean creation) {
    DateTime start;
    DateTime end;
    switch (text.toLowerCase()) {
    case "now":
    case "hour":
    case "thishour":
    case "lasthour":
      end = new DateTime();
      start = end.minusHours(1);
      break;
    case "today":
      start = new DateTime().withTimeAtStartOfDay();
      end = start.plusDays(1);
      break;
    case "yesterday":
      end = new DateTime().withTimeAtStartOfDay();
      start = end.minusDays(1);
      break;
    case "thisweek":
      end = new DateTime().withTimeAtStartOfDay().plusDays(1);
      start = end.withDayOfWeek(1);
      break;
    case "lastweek":
      end = new DateTime().withTimeAtStartOfDay().plusDays(1).minusWeeks(1);
      start = end.withDayOfWeek(1);
      break;
    default:
      int ago = 0;
      for (int i = 0; i < AGO_MATCHERS.size() && ago == 0; i++) {
        ago = AGO_MATCHERS.get(i).secondsAgoIfmatches(text);
      }
      if (ago > 0) {
        end = new DateTime();
        start = end.minusSeconds(ago);
      } else {
        try {
          DateTime d = DateTime.parse(text);
          start = d.withTimeAtStartOfDay();
          end = d.withTimeAtStartOfDay().plusDays(1);
        } catch (IllegalArgumentException e) {
          return null;
        }
      }
    }
    return date(start.toDate(), end.toDate(), creation);
  }

  static PaginationFilter parseUser(String username, String currentUser, boolean creator) {
    if (username.equalsIgnoreCase("me")) {
      return user(currentUser, creator);
    }
    return user(username, creator);
  }

  public static PaginationFilter platformType(final PlatformType platformType) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.restrictPaginationByPlatformType(item, platformType);
      }
    };
  }

  public static PaginationFilter pool(final long poolId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.restrictPaginationByPoolId(item, poolId);
      }
    };
  }

  public static PaginationFilter pool(final Pool pool) {
    if (pool.getId() == PoolImpl.UNSAVED_ID) {
      throw new IllegalArgumentException("Cannot filter by unsaved pool.");
    }
    return pool(pool.getId());
  }

  public static PaginationFilter project(final long projectId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.restrictPaginationByProjectId(item, projectId);
      }
    };
  }

  public static PaginationFilter query(final String query) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.restrictPaginationByQuery(item, query);
      }
    };
  }

  public static PaginationFilter user(String loginName, boolean creator) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.restrictPaginationByUser(item, loginName, creator);
      }
    };
  }

  public abstract <T> void apply(PaginationFilterSink<T> sink, T item);

}
