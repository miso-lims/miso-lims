/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.persistence.util;

import java.util.Date;
import java.util.UUID;
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
import uk.ac.bbsrc.tgac.miso.core.util.TextQuery;

/**
 * uk.ac.bbsrc.tgac.miso.util
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class DbUtils {

  public static void restrictPaginationByFreezer(Criteria criteria, TextQuery query, String boxStorageLocationProperty) {
    if (query.getText() == null) {
      criteria.add(Restrictions.isNull(boxStorageLocationProperty));
      return;
    }
    String sanitized = sanitizeQueryString(query.getText());
    MatchMode matchMode = getMatchMode(query);

    criteria.createAlias(boxStorageLocationProperty, "location1", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location1.parentLocation", "location2", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location2.parentLocation", "location3", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location3.parentLocation", "location4", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location4.parentLocation", "location5", JoinType.LEFT_OUTER_JOIN)
        .createAlias("location5.parentLocation", "location6", JoinType.LEFT_OUTER_JOIN)
        .add(Restrictions.or(
            Restrictions.and(Restrictions.eq("location1.locationUnit", LocationUnit.FREEZER),
                textRestriction("location1.alias", sanitized, matchMode)),
            Restrictions.and(Restrictions.eq("location2.locationUnit", LocationUnit.FREEZER),
                textRestriction("location2.alias", sanitized, matchMode)),
            Restrictions.and(Restrictions.eq("location3.locationUnit", LocationUnit.FREEZER),
                textRestriction("location3.alias", sanitized, matchMode)),
            Restrictions.and(Restrictions.eq("location4.locationUnit", LocationUnit.FREEZER),
                textRestriction("location4.alias", sanitized, matchMode)),
            Restrictions.and(Restrictions.eq("location5.locationUnit", LocationUnit.FREEZER),
                textRestriction("location5.alias", sanitized, matchMode)),
            Restrictions.and(Restrictions.eq("location6.locationUnit", LocationUnit.FREEZER),
                textRestriction("location6.alias", sanitized, matchMode))));
  }

  public static void restrictPaginationByDistributionRecipient(Criteria criteria, TextQuery query, String collectionProperty,
      String itemIdProperty) {
    if (query.getText() == null) {
      DetachedCriteria subquery = DetachedCriteria.forClass(ListTransferView.class)
          .createAlias(collectionProperty, "transferItem")
          .add(Restrictions.isNotNull("recipient"))
          .setProjection(Projections.property("transferItem." + itemIdProperty));
      criteria.add(Property.forName("id").notIn(subquery));
    } else {
      criteria.createAlias("listTransferViews", "transfer")
          .add(DbUtils.textRestriction(query, "transfer.recipient"));
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

  public static Criterion textRestriction(TextQuery query, String... searchProperties) {
    String sanitized = sanitizeQueryString(query.getText());
    MatchMode matchMode = getMatchMode(query);
    if (searchProperties.length == 1) {
      return textRestriction(searchProperties[0], sanitized, matchMode);
    } else {
      Criterion[] criteria = new Criterion[searchProperties.length];
      for (int i = 0; i < searchProperties.length; i++) {
        criteria[i] = textRestriction(searchProperties[i], sanitized, matchMode);
      }
      if (sanitized == null) {
        // if null, all search properties must be null
        return Restrictions.and(criteria);
      } else {
        // if not null, match on any search property
        return Restrictions.or(criteria);
      }
    }
  }

  private static String sanitizeQueryString(String original) {
    return original == null ? null
        : original.trim()
            .replaceAll("_", Matcher.quoteReplacement("\\_"))
            .replaceAll("%", Matcher.quoteReplacement("\\%"));
  }

  private static MatchMode getMatchMode(TextQuery query) {
    if (query.isExactStart()) {
      if (query.isExactEnd()) {
        return MatchMode.EXACT;
      } else {
        return MatchMode.START;
      }
    } else if (query.isExactEnd()) {
      return MatchMode.END;
    } else {
      return MatchMode.ANYWHERE;
    }
  }

  private static Criterion textRestriction(String propertyName, String sanitizedQuery, MatchMode matchMode) {
    if (sanitizedQuery == null) {
      return Restrictions.isNull(propertyName);
    } else {
      return Restrictions.ilike(propertyName, sanitizedQuery, matchMode);
    }
  }

  /**
   * Prefix for a temporary name for any entity
   */
  public static final String TEMPORARY_NAME_PREFIX = "TEMPORARY_";

  /**
   * Generate a temporary name using a UUID.
   * 
   * @return Temporary name
   */
  public static String generateTemporaryName() {
    return TEMPORARY_NAME_PREFIX + UUID.randomUUID();
  }

}
