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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.sqlstore.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

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

  public static String convertStringToSearchQuery(String input) {
    if (input == null) {
      return "%";
    }
    return "%" + input.trim().toUpperCase().replaceAll("(\\s|,)+", " ").replaceAll("_", Matcher.quoteReplacement("\\_")).replaceAll("%",
        Matcher.quoteReplacement("\\%")) + "%";
  }

  public static ArrayList<String> getTables(JdbcTemplate template) throws MetaDataAccessException, SQLException {
    Object o = JdbcUtils.extractDatabaseMetaData(template.getDataSource(),
        new GetTableNames(template.getDataSource().getConnection().getCatalog()));
    return (ArrayList<String>) o;
  }

  public static ArrayList<String> getColumns(JdbcTemplate template, String table) throws MetaDataAccessException, SQLException {
    Object o = JdbcUtils.extractDatabaseMetaData(template.getDataSource(),
        new GetColumnNames(template.getDataSource().getConnection().getCatalog(), table));
    return (ArrayList<String>) o;
  }

  public static Map<String, Integer> getColumnSizes(JdbcTemplate template, String table) {
    Connection connection = null;
    try {
      connection = template.getDataSource().getConnection();
      Object o = JdbcUtils.extractDatabaseMetaData(template.getDataSource(), new GetColumnSizes(connection.getCatalog(), table));
      return (HashMap<String, Integer>) o;
    } catch (MetaDataAccessException e) {
      log.error("Could not retrieve table " + table + " field lengths", e);
    } catch (SQLException e) {
      log.error("Could not retrieve table " + table + " field lengths", e);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          log.error("Badness! Could not close connection!", e);
        }
      }
    }
    return null;
  }

  public static Integer getColumnSize(JdbcTemplate template, String table, String column) {
    Connection connection = null;
    try {
      connection = template.getDataSource().getConnection();
      Object o = JdbcUtils.extractDatabaseMetaData(template.getDataSource(), new GetColumnSizes(connection.getCatalog(), table));
      return ((HashMap<String, Integer>) o).get(column);
    } catch (MetaDataAccessException e) {
      log.error("Could not retrieve field " + column + " max length", e);
    } catch (SQLException e) {
      log.error("Could not retrieve field " + column + " max length", e);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          log.error("Badness! Could not close connection!", e);
        }
      }
    }
    return null;
  }

  static class GetTableNames implements DatabaseMetaDataCallback {
    String catalog = "";

    public GetTableNames() {
    }

    public GetTableNames(String catalog) {
      this.catalog = catalog;
    }

    @Override
    public Object processMetaData(DatabaseMetaData dbmd) throws SQLException {
      ResultSet rs = dbmd.getTables(catalog, null, null, new String[] { "TABLE" });
      ArrayList<String> l = new ArrayList<>();
      while (rs.next()) {
        l.add(rs.getString(3));
      }
      return l;
    }
  }

  static class GetColumnNames implements DatabaseMetaDataCallback {
    String catalog = "";
    String table = "";

    public GetColumnNames() {
    }

    public GetColumnNames(String catalog, String table) {
      this.catalog = catalog;
      this.table = table;
    }

    @Override
    public Object processMetaData(DatabaseMetaData dbmd) throws SQLException {
      ResultSet rs = dbmd.getColumns(catalog, null, table, null);
      ArrayList<String> l = new ArrayList<>();
      while (rs.next()) {
        l.add(rs.getString("COLUMN_NAME"));
      }
      return l;
    }
  }

  static class GetColumnSizes implements DatabaseMetaDataCallback {
    String catalog = "";
    String table = "";

    public GetColumnSizes() {
    }

    public GetColumnSizes(String catalog, String table) {
      this.catalog = catalog;
      this.table = table;
    }

    @Override
    public Object processMetaData(DatabaseMetaData dbmd) throws SQLException {
      ResultSet rs = dbmd.getColumns(catalog, null, table, null);
      Map<String, Integer> l = new HashMap<>();
      while (rs.next()) {
        l.put(rs.getString("COLUMN_NAME"), rs.getInt("COLUMN_SIZE"));
      }
      return l;
    }
  }

  /**
   * Create a Hibernate criterion to search for all the properties our users want to search.
   *
   * @param querystr
   * @return
   */
  public static Criterion searchRestrictions(String querystr, String... searchProperties) {
    String str = DbUtils.convertStringToSearchQuery(querystr);

    Criterion[] criteria = new Criterion[searchProperties.length];
    for (int i = 0; i < searchProperties.length; i++) {
      criteria[i] = Restrictions.ilike(searchProperties[i], str, MatchMode.ANYWHERE);
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
