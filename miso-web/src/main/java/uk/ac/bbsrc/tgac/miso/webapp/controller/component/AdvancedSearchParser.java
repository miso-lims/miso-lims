package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import java.util.Calendar;
import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

@Component
public class AdvancedSearchParser {

  private Integer fiscalYearStartMonth;

  private static final Pattern WHITESPACE = Pattern.compile("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

  private static final Pattern fiscalQuarter = Pattern.compile("^(?:fy(\\d{4})\\s?)?q([1234])$");
  private static final Pattern fiscalYear = Pattern.compile("^fy(\\d{4})$");

  private enum DateRangePoint {
    START, END, UNQUALIFIED_END
  }

  @Value("${miso.fiscalYearStartMonth:}")
  public void setFiscalYearEnd(String property) {
    if (LimsUtils.isStringEmptyOrNull(property)) {
      fiscalYearStartMonth = null;
    } else {
      Integer intMonth = Integer.parseInt(property);
      if (intMonth < 1 || intMonth > 12) {
        throw new IllegalArgumentException("fiscalYearStartMonth must be between 1 and 12");
      }
      fiscalYearStartMonth = intMonth;
    }
  }

  /**
   * Search terms are documented in miso-web/src/main/webapp/scripts/list.js
   */
  public PaginationFilter[] parseQuery(String request, String currentUser, Consumer<String> errorHandler) {
    return WHITESPACE.splitAsStream(request).map(x -> {
      x = x.replace("\"", "");
      if (x.isEmpty()) return null;
      if (x.contains(":")) {
        String[] parts = x.split(":", 2);
        switch (parts[0]) {
        case "is":
        case "has":
          switch (parts[1].toLowerCase()) {
          case "fulfilled":
            return PaginationFilter.fulfilled(true);
          case "active":
          case "order":
          case "ordered":
          case "unfulfilled":
            return PaginationFilter.fulfilled(false);
          case "unknown":
            return PaginationFilter.health(HealthType.Unknown);
          case "complete":
          case "completed":
            return PaginationFilter.health(HealthType.Completed);
          case "failed":
            return PaginationFilter.health(HealthType.Failed);
          case "started":
            return PaginationFilter.health(HealthType.Started);
          case "stopped":
            return PaginationFilter.health(HealthType.Stopped);
          case "running":
            return PaginationFilter.health(HealthType.Running);
          case "incomplete":
            return PaginationFilter.health(EnumSet.of(HealthType.Running, HealthType.Started, HealthType.Stopped));
          case "ghost":
            return PaginationFilter.ghost(true);
          case "real":
            return PaginationFilter.ghost(false);
          default:
            errorHandler.accept("No filter for " + x);
            return null;
          }
        case "created":
        case "createdon":
          return parseDate(parts[1], DateType.CREATE, errorHandler);
        case "entered":
        case "enteredOn":
        case "recorded":
        case "recordedOn":
          return parseDate(parts[1], DateType.ENTERED, errorHandler);
        case "changed":
        case "modified":
        case "updated":
        case "changedon":
        case "modifiedon":
        case "updatedon":
          return parseDate(parts[1], DateType.UPDATE, errorHandler);
        case "received":
        case "recieved":
        case "receivedon":
        case "recievedon":
          return parseDate(parts[1], DateType.RECEIVE, errorHandler);
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
            return PaginationFilter.platformType(PlatformType.valueOf(parts[1].toUpperCase()));
          } catch (IllegalArgumentException e) {
            errorHandler.accept("Invalid platform: " + parts[1]);
            return null;
          }
        case "id":
          try {
            if (parts[1].contains(",")) {
              return PaginationFilter.ids(LimsUtils.parseIds(parts[1]));
            } else {
              return PaginationFilter.id(Long.parseLong(parts[1]));
            }
          } catch (NumberFormatException ex) {
            errorHandler.accept("Invalid ID: " + parts[1]);
            return null;
          }
        case "index":
          return PaginationFilter.index(parts[1]);
        case "class":
          return PaginationFilter.sampleClass(parts[1]);
        case "external":
        case "ext":
        case "extern":
          return PaginationFilter.external(parts[1]);
        case "institute":
        case "inst":
          return PaginationFilter.institute(parts[1]);
        case "box":
          return PaginationFilter.box(parts[1]);
        case "boxType":
          try {
            return PaginationFilter.boxType(BoxType.valueOf(parts[1].toUpperCase()));
          } catch (IllegalArgumentException e) {
            errorHandler.accept("Invalid box type: " + parts[1]);
            return null;
          }
        case "kitname":
          return PaginationFilter.kitName(parts[1]);
        case "subproject":
          return PaginationFilter.subproject(parts[1]);
        case "sequencingparameters":
        case "parameters":
        case "params":
          return PaginationFilter.sequencingParameters(parts[1]);
        case "groupid":
          return PaginationFilter.groupId(parts[1]);
        case "distributed":
          if (LimsUtils.isStringEmptyOrNull(parts[1])) {
            return PaginationFilter.distributed();
          } else {
            return parseDate(parts[1], DateType.DISTRIBUTED, errorHandler);
          }
        case "distributedto":
          return PaginationFilter.distributedTo(parts[1]);
        case "freezer":
          return PaginationFilter.freezer(parts[1]);
        case "req":
        case "requisition":
        case "requisitionId":
          return PaginationFilter.requisitionId(parts[1]);
        case "tissueOrigin":
        case "origin":
          return PaginationFilter.tissueOrigin(parts[1]);
        case "tissueType":
          return PaginationFilter.tissueType(parts[1]);
        }
      }
      return PaginationFilter.query(x);
    }).filter(Objects::nonNull).toArray(PaginationFilter[]::new);
  }

  private PaginationFilter parseDate(String text, DateType type, Consumer<String> errorHandler) {
    DateTime start;
    DateTime end;
    String lowerCaseText = text.toLowerCase();
    if (lowerCaseText.startsWith("before ")) {
      start = new DateTime(0L); // epoch (1970-01-01)
      end = parseDate(lowerCaseText.substring(7), DateRangePoint.START);
    } else if (lowerCaseText.startsWith("after ")) {
      start = parseDate(lowerCaseText.substring(6), DateRangePoint.END);
      end = new DateTime();
    } else {
      start = parseDate(lowerCaseText, DateRangePoint.START);
      end = parseDate(lowerCaseText, DateRangePoint.UNQUALIFIED_END);
    }
    if (start != null && end != null) {
      return PaginationFilter.date(start.toDate(), end.toDate(), type);
    } else {
      errorHandler.accept("Invalid date format: " + text);
      return null;
    }
  }

  private DateTime parseDate(String text, DateRangePoint dateRangePoint) {
    if (fiscalYearStartMonth != null) {
      Matcher fiscalQuarterMatcher = fiscalQuarter.matcher(text);
      if (fiscalQuarterMatcher.matches()) {
        int fiscalYear;
        if (LimsUtils.isStringEmptyOrNull(fiscalQuarterMatcher.group(1))) {
          Calendar now = Calendar.getInstance();
          fiscalYear = now.get(Calendar.YEAR);
          if (now.get(Calendar.MONTH) + 1 < fiscalYearStartMonth) {
            fiscalYear -= 1;
          }
        } else {
          fiscalYear = Integer.parseInt(fiscalQuarterMatcher.group(1));
        }
        int quarter = Integer.parseInt(fiscalQuarterMatcher.group(2));
        DateTime date = new DateTime(fiscalYear, fiscalYearStartMonth, 1, 0, 0)
            .plusMonths(3 * (quarter - 1));
        return dateRangePoint == DateRangePoint.START ? date : date.plusMonths(3);
      }
      Matcher fiscalYearMatcher = fiscalYear.matcher(text);
      if (fiscalYearMatcher.matches()) {
        int fiscalYear = Integer.parseInt(fiscalYearMatcher.group(1));
        DateTime date = new DateTime(fiscalYear, fiscalYearStartMonth, 1, 0, 0);
        return dateRangePoint == DateRangePoint.START ? date : date.plusYears(1);
      }
    }
    for (DateRangeParser parser : DateRangeParser.values()) {
      if (parser.matches(text)) {
        switch (dateRangePoint) {
        case START:
          return parser.getStart(text);
        case END:
          return parser.getEnd(text);
        case UNQUALIFIED_END:
          return parser.getUnqualifiedEnd(text);
        default:
          throw new IllegalStateException("Unhandled DateRangePoint");
        }
      }
    }
    return null;
  }

  private PaginationFilter parseUser(String username, String currentUser, boolean creator) {
    if (username.equalsIgnoreCase("me")) {
      return PaginationFilter.user(currentUser, creator);
    }
    return PaginationFilter.userOrGroup(username, creator);
  }

}
