package uk.ac.bbsrc.tgac.miso.persistence.util;

import java.util.Date;
import java.util.regex.Matcher;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class DbUtils {

  public static void restrictPaginationByFreezer(Criteria criteria, String query, String boxStorageLocationProperty) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      criteria.add(Restrictions.isNull(boxStorageLocationProperty));
      return;
    }

    criteria.createAlias(boxStorageLocationProperty, "location1", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location1.parentLocation", "location2", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location2.parentLocation", "location3", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location3.parentLocation", "location4", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location4.parentLocation", "location5", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location5.parentLocation", "location6", JoinType.LEFT_OUTER_JOIN)
        .add(Restrictions.or(
            Restrictions.and(Restrictions.eq("location1.locationUnit", LocationUnit.FREEZER),
                textRestriction("location1.alias", query)),
            Restrictions.and(Restrictions.eq("location2.locationUnit", LocationUnit.FREEZER),
                textRestriction("location2.alias", query)),
            Restrictions.and(Restrictions.eq("location3.locationUnit", LocationUnit.FREEZER),
                textRestriction("location3.alias", query)),
            Restrictions.and(Restrictions.eq("location4.locationUnit", LocationUnit.FREEZER),
                textRestriction("location4.alias", query)),
            Restrictions.and(Restrictions.eq("location5.locationUnit", LocationUnit.FREEZER),
                textRestriction("location5.alias", query)),
            Restrictions.and(Restrictions.eq("location6.locationUnit", LocationUnit.FREEZER),
                textRestriction("location6.alias", query))));
  }

  public static void restrictPaginationByDistributionRecipient(Criteria criteria, String query,
      String collectionProperty,
      String itemIdProperty) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      DetachedCriteria subquery = DetachedCriteria.forClass(ListTransferView.class)
          .createAlias(collectionProperty, "transferItem")
          .add(Restrictions.isNotNull("recipient"))
          .setProjection(Projections.property("transferItem." + itemIdProperty));
      criteria.add(Property.forName("id").notIn(subquery));
    } else {
      criteria.createAlias("listTransferViews", "transfer")
          .add(DbUtils.textRestriction("transfer.recipient", query));
    }
  }

  public static void restrictPaginationByReceiptTransferDate(Criteria criteria, Date start, Date end) {
    criteria.createAlias("listTransferViews", "transfer")
        .add(Restrictions.isNotNull("transfer.senderLab"))
        .add(Restrictions.between("transfer.transferTime", start, end));
  }

  public static void restrictPaginationByDistributionTransferDate(Criteria criteria, Date start, Date end) {
    criteria.createAlias("listTransferViews", "transfer")
        .add(Restrictions.isNotNull("transfer.recipient"))
        .add(Restrictions.between("transfer.transferTime", start, end));
  }

  public static Criterion textRestriction(String query, String... searchProperties) {
    if (searchProperties == null || searchProperties.length == 0) {
      // Sabotage the query to return nothing if there are no properties
      return Restrictions.sqlRestriction("FALSE");
    } else if (searchProperties.length == 1) {
      return textRestriction(searchProperties[0], query);
    } else {
      Criterion[] criteria = new Criterion[searchProperties.length];
      for (int i = 0; i < searchProperties.length; i++) {
        criteria[i] = textRestriction(searchProperties[i], query);
      }
      if (LimsUtils.isStringBlankOrNull(query)) {
        // if null, all search properties must be null
        return Restrictions.and(criteria);
      } else {
        // if not null, match on any search property
        return Restrictions.or(criteria);
      }
    }
  }

  private static Criterion textRestriction(String propertyName, String query) {
    if (LimsUtils.isStringEmptyOrNull(query)) {
      return Restrictions.isNull(propertyName);
    } else if (isQuoted(query)) {
      String finalQuery = removeQuotes(query);
      return Restrictions.eq(propertyName, finalQuery);
    } else if (containsWildcards(query)) {
      String sanitized = sanitizeQueryString(query);
      String finalQuery = replaceWildcards(sanitized);
      return Restrictions.like(propertyName, finalQuery);
    } else {
      String sanitized = sanitizeQueryString(query);
      String finalQuery = removeEscapes(sanitized);
      return Restrictions.like(propertyName, finalQuery, MatchMode.ANYWHERE);
    }
  }

  /**
   * Escapes MySQL wildcard characters "_" and "%"
   * 
   * @param original the String to format
   * @return the String with MySQL wildcard characters escaped
   */
  public static String sanitizeQueryString(String original) {
    // escape MySQL LIKE wildcard characters
    return original.trim()
        .replaceAll("_", Matcher.quoteReplacement("\\_"))
        .replaceAll("%", Matcher.quoteReplacement("\\%"));
  }

  /**
   * Checks whether a String starts and ends with non-escaped double quotes ("), indicating that an
   * exact match is wanted
   * 
   * @param query the String to check
   * @return true if the String is quoted; false otherwise
   */
  public static boolean isQuoted(String query) {
    return query.matches("\"(.*[^\\\\])?\"");
  }

  /**
   * Checks whether a String contains any MISO wildcards (*), indicating that partial matching is
   * wanted
   * 
   * @param query the String to check
   * @return true if the String contains one or more wildcards; false otherwise
   */
  public static boolean containsWildcards(String query) {
    return query.matches(".*(^|[^\\\\])\\*.*");
  }

  /**
   * Removes double quotes (") from the start and end of a String, then removes any escape characters
   * from inner double quotes and MISO wildcards (*)
   * 
   * @param original the String to format
   * @return the String with quotes and escapes removed
   */
  public static String removeQuotes(String original) {
    return removeEscapes(original.replaceFirst("^\"(.*)\"$", "$1"));
  }

  /**
   * Replaces non-escaped MISO wildcards (*) with MySQL wildcards (%)
   * 
   * @param original the String to format
   * @return the formatted String
   */
  public static String replaceWildcards(String original) {
    return removeEscapes(original.replaceAll("(^|[^\\\\])\\*", "$1%"));
  }

  /**
   * Removes escape characters from double quotes (") and MISO wildcards (*)
   * 
   * @param original the String to format
   * @return the formatted String
   */
  public static String removeEscapes(String original) {
    return original
        .replaceAll("\\\\\"", "\"")
        .replaceAll("\\\\\\*", "*");
  }

}
