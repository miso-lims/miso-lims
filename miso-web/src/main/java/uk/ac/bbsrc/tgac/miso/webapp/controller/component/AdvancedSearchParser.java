package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

@Component
public class AdvancedSearchParser {

  private static final String TERMED_CRITERION_PATTERN = "(\\w+):(.*)";

  private Integer fiscalYearStartMonth;

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
    return splitCriteria(request).stream().map(x -> {
      if (LimsUtils.isStringBlankOrNull(x)) {
        return null;
      }
      Matcher m = Pattern.compile(TERMED_CRITERION_PATTERN).matcher(x);
      if (m.matches()) {
        String term = m.group(1).toLowerCase();
        String phrase = m.group(2);
        switch (term) {
          case "is":
          case "has":
            switch (phrase.toLowerCase()) {
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
              case "archived":
              case "retired":
                return PaginationFilter.archived(true);
              default:
                errorHandler.accept("No filter for " + x);
                return null;
            }
          case "not":
            switch (phrase.toLowerCase()) {
              case "archived":
              case "retired":
                return PaginationFilter.archived(false);
              default:
                errorHandler.accept("No filter for " + x);
                return null;
            }
          case "barcode":
            return PaginationFilter.barcode(phrase);
          case "created":
          case "createdon":
            return parseDate(phrase, DateType.CREATE, errorHandler);
          case "entered":
          case "enteredon":
          case "recorded":
          case "recordedon":
            return parseDate(phrase, DateType.ENTERED, errorHandler);
          case "changed":
          case "modified":
          case "updated":
          case "changedon":
          case "modifiedon":
          case "updatedon":
            return parseDate(phrase, DateType.UPDATE, errorHandler);
          case "received":
          case "recieved":
          case "receivedon":
          case "recievedon":
            return parseDate(phrase, DateType.RECEIVE, errorHandler);
          case "createdby":
          case "creator":
          case "creater":
            return parseUser(phrase, currentUser, true);
          case "changedby":
          case "modifier":
          case "updater":
            return parseUser(phrase, currentUser, false);
          case "platform":
            try {
              return PaginationFilter.platformType(PlatformType.valueOf(phrase.toUpperCase()));
            } catch (IllegalArgumentException e) {
              errorHandler.accept("Invalid platform: " + phrase);
              return null;
            }
          case "id":
            try {
              if (phrase.contains(",")) {
                return PaginationFilter.ids(LimsUtils.parseIds(phrase));
              } else {
                return PaginationFilter.id(Long.parseLong(phrase));
              }
            } catch (NumberFormatException ex) {
              errorHandler.accept("Invalid ID: " + phrase);
              return null;
            }
          case "identityid":
            try {
              return PaginationFilter.identityIds(LimsUtils.parseIds(phrase));
            } catch (NumberFormatException ex) {
              errorHandler.accept("Invalid identity ID: " + phrase);
              return null;
            }
          case "index":
            return PaginationFilter.index(phrase);
          case "class":
            return PaginationFilter.sampleClass(phrase);
          case "external":
          case "ext":
          case "extern":
            return PaginationFilter.external(phrase);
          case "lab":
            return PaginationFilter.lab(phrase);
          case "box":
            return PaginationFilter.box(phrase);
          case "boxType":
            try {
              return PaginationFilter.boxType(BoxType.valueOf(phrase.toUpperCase()));
            } catch (IllegalArgumentException e) {
              errorHandler.accept("Invalid box type: " + phrase);
              return null;
            }
          case "kitname":
            return PaginationFilter.kitName(phrase);
          case "project":
            return PaginationFilter.project(phrase);
          case "subproject":
            return PaginationFilter.subproject(phrase);
          case "sequencingparameters":
          case "parameters":
          case "params":
            return PaginationFilter.sequencingParameters(phrase);
          case "groupid":
            return PaginationFilter.groupId(phrase);
          case "distributed":
            return parseDate(phrase, DateType.DISTRIBUTED, errorHandler);
          case "distributedto":
            return PaginationFilter.distributedTo(phrase);
          case "freezer":
            return PaginationFilter.freezer(phrase);
          case "req":
          case "requisition":
            return PaginationFilter.requisition(phrase);
          case "tissueorigin":
          case "origin":
            return PaginationFilter.tissueOrigin(phrase);
          case "tissuetype":
            return PaginationFilter.tissueType(phrase);
          case "timepoint":
            return PaginationFilter.timepoint(phrase);
          case "stage":
            return PaginationFilter.stage(phrase);
          case "model":
            return PaginationFilter.model(phrase);
          case "workstation":
            return PaginationFilter.workstation(phrase);
          default:
            errorHandler.accept("Unknown search term: " + term);
        }
      }
      return PaginationFilter.query(x);
    }).filter(Objects::nonNull).toArray(PaginationFilter[]::new);
  }

  /**
   * The first criterion may not have a term. All others must have a term. Any word containing an
   * unescaped colon is treated as a term
   * 
   * @param request
   * @return all the separate criteria with any colons unescaped
   */
  @VisibleForTesting
  protected List<String> splitCriteria(String request) {
    String[] words = request.split(" ");
    List<String> criteria = new ArrayList<>();
    String currentTerm = words[0];
    for (int i = 1; i < words.length; i++) {
      if (words[i].matches(TERMED_CRITERION_PATTERN)) {
        criteria.add(currentTerm.replace("\\:", ":"));
        currentTerm = words[i];
      } else {
        currentTerm += " " + words[i];
      }
    }
    criteria.add(currentTerm.replace("\\:", ":"));
    return criteria;
  }

  private PaginationFilter parseDate(String text, DateType type, Consumer<String> errorHandler) {
    ZonedDateTime start;
    ZonedDateTime end;
    String lowerCaseText = text.toLowerCase();
    if (lowerCaseText.startsWith("before ")) {
      start = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()); // epoch (1970-01-01)
      end = parseDate(lowerCaseText.substring(7), DateRangePoint.START);
    } else if (lowerCaseText.startsWith("after ")) {
      start = parseDate(lowerCaseText.substring(6), DateRangePoint.END);
      end = ZonedDateTime.now();
    } else {
      start = parseDate(lowerCaseText, DateRangePoint.START);
      end = parseDate(lowerCaseText, DateRangePoint.UNQUALIFIED_END);
    }
    if (start != null && end != null) {
      Date startDate = Date.from(start.toInstant());
      Date endDate = Date.from(end.toInstant());
      return PaginationFilter.date(startDate, endDate, type);
    } else {
      errorHandler.accept("Invalid date format: " + text);
      return null;
    }
  }

  private ZonedDateTime parseDate(String text, DateRangePoint dateRangePoint) {
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
        ZonedDateTime date = ZonedDateTime.of(fiscalYear, fiscalYearStartMonth, 1, 0, 0, 0, 0, ZoneId.systemDefault())
            .plusMonths(3 * (quarter - 1));
        return dateRangePoint == DateRangePoint.START ? date : date.plusMonths(3);
      }
      Matcher fiscalYearMatcher = fiscalYear.matcher(text);
      if (fiscalYearMatcher.matches()) {
        int fiscalYear = Integer.parseInt(fiscalYearMatcher.group(1));
        ZonedDateTime date = ZonedDateTime.of(fiscalYear, fiscalYearStartMonth, 1, 0, 0, 0, 0, ZoneId.systemDefault());
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
