package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public abstract interface PaginationFilter {
  public final static List<AgoMatcher> AGO_MATCHERS = Arrays.asList(new AgoMatcher("h(|ours?)", 3600),
      new AgoMatcher("d(|ays?)", 3600 * 24));

  public static final Pattern WHITESPACE = Pattern.compile("\\s+");

  public static PaginationFilter archived(boolean isArchived) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByArchived(item, isArchived, errorHandler);
      }
    };
  }

  public static PaginationFilter box(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByBox(item, name, errorHandler);
      }
    };
  }

  public static PaginationFilter boxUse(long id) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByBoxUse(item, id, errorHandler);
      }
    };
  }

  public static PaginationFilter date(Date start, Date end, DateType type) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByDate(item, start, end, type, errorHandler);
      }
    };
  }

  public static PaginationFilter external(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByExternalName(item, name, errorHandler);
      }
    };
  }

  public static PaginationFilter fulfilled(final boolean isFulfilled) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByFulfilled(item, isFulfilled, errorHandler);
      }
    };
  }

  public static PaginationFilter health(EnumSet<HealthType> healths) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByHealth(item, healths, errorHandler);
      }
    };
  }

  public static PaginationFilter health(HealthType health) {
    return health(EnumSet.of(health));
  }

  public static PaginationFilter index(String index) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByIndex(item, index, errorHandler);
      }
    };
  }

  public static PaginationFilter institute(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByInstitute(item, name, errorHandler);
      }
    };
  }

  public static PaginationFilter kitType(KitType type) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByKitType(item, type, errorHandler);
      }
    };
  }

  public static PaginationFilter[] parse(String request, String currentUser, Consumer<String> errorHandler) {
    return WHITESPACE.splitAsStream(request).map(x -> {
      if (x.isEmpty()) return null;
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
            errorHandler.accept("No filter for " + x);
            return null;
          }
        case "created":
        case "createdon":
          return parseDate(parts[1], DateType.CREATE);
        case "changed":
        case "modified":
        case "updated":
        case "changedon":
        case "modifiedon":
        case "updatedon":
          return parseDate(parts[1], DateType.UPDATE);
        case "received":
        case "recieved":
        case "receivedon":
        case "recievedon":
          return parseDate(parts[1], DateType.RECEIVE);
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
        case "index":
          return index(parts[1]);
        case "class":
          return sampleClass(parts[1]);
        case "external":
        case "ext":
        case "extern":
          return external(parts[1]);
        case "institute":
        case "inst":
          return institute(parts[1]);
        case "box":
          return box(parts[1]);
        }
      }
      return query(x);
    }).filter(Objects::nonNull).toArray(PaginationFilter[]::new);
  }

  static PaginationFilter parseDate(String text, DateType type) {
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
    return date(start.toDate(), end.toDate(), type);
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
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByPlatformType(item, platformType, errorHandler);
      }
    };
  }

  public static PaginationFilter pool(final long poolId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByPoolId(item, poolId, errorHandler);
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
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByProjectId(item, projectId, errorHandler);
      }
    };
  }

  public static PaginationFilter query(final String query) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByQuery(item, query, errorHandler);
      }
    };
  }

  public static PaginationFilter sampleClass(String name) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByClass(item, name, errorHandler);
      }
    };
  }

  public static PaginationFilter sequencer(long id) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationBySequencerId(item, id, errorHandler);
      }
    };
  }

  public static PaginationFilter user(String loginName, boolean creator) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler) {
        sink.restrictPaginationByUser(item, loginName, creator, errorHandler);
      }
    };
  }

  public abstract <T> void apply(PaginationFilterSink<T> sink, T item, Consumer<String> errorHandler);

}
