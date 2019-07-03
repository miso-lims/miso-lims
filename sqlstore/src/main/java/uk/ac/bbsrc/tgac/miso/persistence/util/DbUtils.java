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

import java.util.UUID;
import java.util.regex.Matcher;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * uk.ac.bbsrc.tgac.miso.util
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class DbUtils {
  protected static final Logger log = LoggerFactory.getLogger(DbUtils.class);

  public static String convertStringToSearchQuery(String input, boolean exact) {
    if (input == null) {
      return "%";
    }
    String query = input.trim().toUpperCase().replaceAll("(\\s|,)+", " ").replaceAll("_", Matcher.quoteReplacement("\\_")).replaceAll("%",
        Matcher.quoteReplacement("\\%"));
    return exact ? query : ("%" + query + "%");
  }

  /**
   * Create a Hibernate criterion to search for all the properties our users want to search.
   *
   * @param querystr
   * @return
   */
  public static Criterion searchRestrictions(String querystr, boolean exact, String... searchProperties) {
    String str = DbUtils.convertStringToSearchQuery(querystr, exact);

    Criterion[] criteria = new Criterion[searchProperties.length];
    for (int i = 0; i < searchProperties.length; i++) {
      criteria[i] = Restrictions.ilike(searchProperties[i], str, exact ? MatchMode.EXACT : MatchMode.ANYWHERE);
    }
    return Restrictions.or(criteria);
  }

  /**
   * Prefix for a temporary name for any entity
   */
  static final public String TEMPORARY_NAME_PREFIX = "TEMPORARY_";

  /**
   * Generate a temporary name using a UUID.
   * 
   * @return Temporary name
   */
  static public String generateTemporaryName() {
    return TEMPORARY_NAME_PREFIX + UUID.randomUUID();
  }

}
