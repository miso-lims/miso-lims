package uk.ac.bbsrc.tgac.miso.sqlstore;

import net.sf.ehcache.CacheManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import uk.ac.bbsrc.tgac.miso.core.store.StateStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 *
 * @author Rob Davey
 * @date 13/05/14
 * @since 0.2.1-SNAPSHOT
 */
public class SQLStateDAO implements StateStore {
  private static final String KEY_TABLE_NAME = "State_Key";
  private static final String VALUE_TABLE_NAME = "State_Value";

  private static final String STATE_KEY_SELECT =
      "SELECT sk.value " +
      "FROM " + KEY_TABLE_NAME + " as sk " +
      "ORDER BY sk.value ASC";

  private static final String STATE_KEY_ID_SELECT_BY_KEY =
      "SELECT sk.id " +
      "FROM " + KEY_TABLE_NAME + " as sk " +
      "WHERE sk.value = ?";

  private static final String STATE_KEY_SELECT_BY_ID =
      "SELECT sk.value " +
      "FROM " + KEY_TABLE_NAME + " as sk " +
      "WHERE sk.id  = ?";

  private static final String STATE_KEY_SELECT_BY_SEARCH =
      "SELECT sk.id, sk.value " +
      "FROM " + KEY_TABLE_NAME + " as sk " +
      "WHERE sk.value LIKE ?";

  private static final String STATE_VALUE_SELECT_BY_ID =
      "SELECT sv.value " +
      "FROM " + VALUE_TABLE_NAME + " as sv " +
      "WHERE sv.id = ?";

  private static final String STATE_VALUE_UPDATE_BY_ID =
      "UPDATE FROM "+VALUE_TABLE_NAME+" as sv SET sv.value =:value " +
      "WHERE sv.id = ?";

  protected static final Logger log = LoggerFactory.getLogger(SQLStateDAO.class);

  @Autowired
  private CacheManager cacheManager;

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  private JdbcTemplate template;

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public synchronized List<Map<Long, Long>> saveAll(JSONObject jsonObject) throws IOException {
    List<Map<Long, Long>> batch = new ArrayList<>();
    JSONArray fields = jsonObject.getJSONArray("state");
    if (fields != null) {
      for (JSONObject j : (Iterable<JSONObject>)fields) {
        String key = j.getString("key");

        Map<Long, Long> pair = new HashMap<>();
        long keyId = getIdForKey(key);
        if (keyId <= 0) {
          //key doesn't exist in key table so add it
          keyId = saveKey(key);
        }

        Long valueId = 0L;
        if (j.has("valueId")) {
          //previously saved value so update
          valueId = Long.valueOf(j.getString("valueId"));
          if (j.has("value")) {
            String value = j.getString("value");
            updateValue(Long.valueOf(valueId), value);
          }
        }
        else {
          if (j.has("value")) {
            String value = j.getString("value");
            valueId = saveValue(value);
          }
        }

        if (valueId != 0L) {
          pair.put(keyId, valueId);
          batch.add(pair);
        }
      }
    }

    /*
    Set<String> stateKeys = new HashSet<String>(jsonObject.keySet());
    List<Map<Long, Long>> batch = new ArrayList<>();

    for (String key : stateKeys) {
      Map<Long, Long> pair = new HashMap<>();
      long keyId = getIdForKey(key);
      if (keyId <= 0) {
        //key doesn't exist in key table so add it
        keyId = saveKey(key);
      }

      String value = jsonObject.getString(key);
      long valueId = saveValue(value);

      pair.put(keyId, valueId);
      batch.add(pair);
    }
    */
    return batch;

  }

  @Override
  public Map<Long, String> listStateKeysBySearch(String str) {
    String mySQLQuery = "%" + str.replaceAll("_", Matcher.quoteReplacement("\\_")) + "%";
    List<Map<String, Object>> results = template.queryForList(STATE_KEY_SELECT_BY_SEARCH, mySQLQuery);
    if (!results.isEmpty()) {
      Map<Long, String> map = new TreeMap<>();
      for (Map<String, Object> row : results) {
        Long id = (Long)row.get("id");
        String value = (String)row.get("value");
        if (id != null && value != null) map.put(id, value);
      }
      return map;
    }
    return Collections.emptyMap();
  }

  @Override
  public Set<String> listAllKeys() {
    return new HashSet<>(template.queryForList(STATE_KEY_SELECT, String.class));
  }

  @Override
  public boolean validateKeys(Set<String> keys) {
    Set<String> allKeys = listAllKeys();
    return allKeys.containsAll(keys);
  }

  @Override
  public long getIdForKey(String key) throws IOException {
    List<Long> eResults = template.queryForList(STATE_KEY_ID_SELECT_BY_KEY, Long.class, key);
    if (eResults.isEmpty()) return 0L;
    return eResults.get(0);
  }

  @Override
  public String getKey(long keyId) throws IOException {
    List<String> eResults = template.queryForList(STATE_KEY_SELECT_BY_ID, String.class, keyId);
    if (eResults.isEmpty()) return null;
    return eResults.get(0);
  }

  @Override
  public String getValue(long valueId) throws IOException {
    List<String> eResults = template.queryForList(STATE_VALUE_SELECT_BY_ID, String.class, valueId);
    if (eResults.isEmpty()) return null;
    return eResults.get(0);
  }

  @Override
  public long saveKey(String key) throws IOException {
    SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
      .withTableName(KEY_TABLE_NAME)
      .usingGeneratedKeyColumns("id");

    if (getIdForKey(key) > 0) {
      throw new IOException("Key already exists");
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("value", key);
    long expId = DbUtils.getAutoIncrement(template, KEY_TABLE_NAME);
    long newId = insert.executeAndReturnKey(params).longValue();
    if (newId != expId) {
      throw new IOException("Something bad happened. Expected State Key ID doesn't match returned value from DB insert");
    }
    return newId;
  }

  @Override
  public long saveValue(String value) throws IOException {
    SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
      .withTableName(VALUE_TABLE_NAME)
      .usingGeneratedKeyColumns("id");

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("value", value);

    long expId = DbUtils.getAutoIncrement(template, VALUE_TABLE_NAME);
    long newId = insert.executeAndReturnKey(params).longValue();
    if (newId != expId) {
      throw new IOException("Something bad happened. Expected State Value ID doesn't match returned value from DB insert");
    }
    return newId;
  }

  @Override
  public long updateValue(long valueId, String value) throws IOException {
    if (getValue(valueId) != null) {
      MapSqlParameterSource params = new MapSqlParameterSource();
      params.addValue("valueId", valueId);
      params.addValue("value", value);

      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(STATE_VALUE_UPDATE_BY_ID, params);
      return valueId;
    }
    else {
      return saveValue(value);
    }
  }

  @Override
  public long save(JSONObject jsonObject) throws IOException {
    throw new IOException(new UnsupportedOperationException("Please use the specific save*() methods for aggregate/singular key-value pairs"));
  }

  @Override
  public JSONObject get(long id) throws IOException {
    throw new IOException(new UnsupportedOperationException("Please use the specific get*() methods for aggregate/singular key-value pairs"));
  }

  @Override
  public JSONObject lazyGet(long id) throws IOException {
    throw new IOException(new UnsupportedOperationException("Please use the specific get*() methods for aggregate/singular key-value pairs"));
  }

  @Override
  public Collection<JSONObject> listAll() throws IOException {
    throw new IOException(new UnsupportedOperationException("Please use the specific list*() methods for aggregate/singular key-value pair lists"));
  }

  @Override
  public int count() throws IOException {
    throw new IOException(new UnsupportedOperationException("Please use the specific count*() methods for aggregate/singular key-value pair counts"));
  }
}
