/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import com.googlecode.ehcache.annotations.key.HashCodeCacheKeyGenerator;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.BlockingCache;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

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
  private static final HashCodeCacheKeyGenerator hashCodeCacheKeyGenerator = new HashCodeCacheKeyGenerator();

  public static String convertStringToSearchQuery(String input) {
    if (input == null) {
      return "%";
    }
    return "%" + input.trim().toUpperCase().replaceAll("(\\s|,)+", " ").replaceAll("_", Matcher.quoteReplacement("\\_")).replaceAll("%",
        Matcher.quoteReplacement("\\%")) + "%";
  }

  public static long getAutoIncrement(JdbcTemplate template, String tableName) throws IOException {
    final String q = "SHOW TABLE STATUS LIKE '" + tableName + "'";
    Map<String, Object> rs = template.queryForMap(q);
    Object ai = rs.get("Auto_increment");
    if (ai != null) {
      return new Long(ai.toString());
    } else {
      throw new IOException("Cannot resolve Auto_increment value from DBMS metadata tables");
    }
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

  public static void flushAllCaches(CacheManager cacheManager) {
    if (cacheManager != null) {
      for (String s : cacheManager.getCacheNames()) {
        flushCache(cacheManager, s);
      }
    } else {
      throw new CacheException("No cacheManager declared. Please check your Spring config, or supply a non-null manager");
    }
  }

  public static void flushCache(CacheManager cacheManager, String cacheName) {
    if (cacheManager != null) {
      Ehcache c = cacheManager.getEhcache(cacheName);
      if (c != null) {
        log.info("Removing " + c.getSize() + " elements from " + cacheName);
        c.removeAll();
      } else {
        log.warn("No such cache: " + cacheName);
      }
    } else {
      throw new CacheException("No cacheManager declared. Please check your Spring config, or supply a non-null manager");
    }
  }

  public static <T> Cache lookupCache(CacheManager cacheManager, Class<T> cacheClass, boolean lazy) {
    if (lazy) {
      return cacheManager.getCache("lazy" + LimsUtils.capitalise(cacheClass.getSimpleName()) + "Cache");
    } else {
      return cacheManager.getCache(LimsUtils.noddyCamelCaseify(cacheClass.getSimpleName()) + "Cache");
    }
  }

  public static void updateCaches(Cache cache, long id) {
    if (cache != null && cache.getKeys().size() > 0) {
      log.debug("Removing " + id + " from " + cache.getName());
      BlockingCache c = new BlockingCache(cache);
      c.remove(DbUtils.hashCodeCacheKeyFor(id));
    }
  }

  public static <T extends Nameable> void updateCaches(CacheManager cacheManager, T obj, Class<T> cacheClass) {
    Cache cache = DbUtils.lookupCache(cacheManager, cacheClass, true);
    if (cache != null && cache.getKeys().size() > 0) {
      log.debug("Removing " + cacheClass.getSimpleName() + " " + obj.getId() + " from " + cache.getName());
      BlockingCache c = new BlockingCache(cache);
      c.remove(DbUtils.hashCodeCacheKeyFor(obj.getId()));
    }

    cache = DbUtils.lookupCache(cacheManager, cacheClass, false);
    if (cache != null && cache.getKeys().size() > 0) {
      log.debug("Removing " + cacheClass.getSimpleName() + " " + obj.getId() + " from " + cache.getName());
      BlockingCache c = new BlockingCache(cache);
      c.remove(DbUtils.hashCodeCacheKeyFor(obj.getId()));
    }
  }

  /**
   * Updates a single object in a cached list. Exact function depends on whether {@code obj.getId()} is found in the cache, and the value of
   * {@code replace}:
   * <ul>
   * <li>if {@code obj.getId()} is not found, obj is added to the cache. replace parameter is ignored</li>
   * <li>if {@code obj.getId()} is found and {@code replace == false}, the matching object is removed from the cache</li>
   * <li>if {@code obj.getId()} is found and {@code replace == true}, the matching object is removed from the cache, and {@code obj} is
   * added</li>
   * </ul>
   * 
   * @param cache the cache to update. Expected to contain one List
   * @param replace whether or not {@code obj} should be cached <b>IF</b> a previously-cached object matching its ID was found and removed
   * @param obj the object which should be added, updated, or removed in the cache, by ID
   */
  public static <T extends Identifiable> void updateListCache(Cache cache, boolean replace, T obj) {
    if (cache != null && cache.getKeys().size() > 0) {
      BlockingCache c = new BlockingCache(cache);
      Object cachekey = c.getKeys().get(0);
      @SuppressWarnings("unchecked")
      List<T> cachedList = (List<T>) c.get(cachekey).getObjectValue();
      if (removeFromCachedList(cachedList, obj)) {
        if (replace) {
          cachedList.add(obj);
        }
      } else {
        cachedList.add(obj);
      }
      c.put(new Element(cachekey, cachedList));
    }
  }

  /**
   * Removes the first object with an ID matching {@code obj.getId()} from {@code list}, if any
   * 
   * @param list the list to search
   * @param obj the object to remove from list if found
   * @return true if a matching object was found and removed; false otherwise
   */
  private static <T extends Identifiable> boolean removeFromCachedList(List<T> list, T obj) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getId() == obj.getId()) {
        list.remove(i);
        return true;
      }
    }
    return false;
  }

  public static <T> List<T> getByBarcodeList(JdbcTemplate template, List<String> barcodeList, String query, RowMapper<T> mapper) {
    return getByGenericList(template, barcodeList, Types.VARCHAR, query, mapper);
  }

  public static <T, N> List<T> getByGenericList(JdbcTemplate template, List<N> needles, int needleType, String query, RowMapper<T> mapper) {
    if (needles.isEmpty()) {
      return Collections.emptyList();
    }
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append(query);
    for (int i = 0; i < needles.size(); i++) {
      if (i != 0) {
        queryBuilder.append(", ");
      }
      queryBuilder.append("?");
    }
    queryBuilder.append(")");
    return template.query(queryBuilder.toString(), new Object[] { needles }, new int[] { needleType }, mapper);
  }

  public static Long hashCodeCacheKeyFor(Object... datas) {
    return hashCodeCacheKeyGenerator.generateKey(datas);
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
      ArrayList l = new ArrayList();
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
      ArrayList l = new ArrayList();
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
      Map<String, Integer> l = new HashMap<String, Integer>();
      while (rs.next()) {
        l.put(rs.getString("COLUMN_NAME"), rs.getInt("COLUMN_SIZE"));
      }
      return l;
    }
  }
  
  /**
   * Ensures that a query by unique field has performed as expected, returning 0 or 1 item
   * 
   * @param results the query results
   * @return the single entry if {@code (results.size() == 0)}; null if {@code (results.isEmpty())}
   * @throws IOException if {@code (results.size() > 1)}
   */
  public static <T> T getUniqueResult(Collection<T> results) throws IOException {
    if (results.isEmpty()) return null;
    if (results.size() > 1) throw new IOException("More than one record found matching unique field value.");
    return results.iterator().next();
  }
}
