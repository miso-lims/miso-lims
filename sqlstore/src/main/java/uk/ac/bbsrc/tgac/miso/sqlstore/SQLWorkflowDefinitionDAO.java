package uk.ac.bbsrc.tgac.miso.sqlstore;

import com.eaglegenomics.simlims.core.User;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.StateStore;
import uk.ac.bbsrc.tgac.miso.core.store.WorkflowDefinitionStore;
import uk.ac.bbsrc.tgac.miso.core.store.WorkflowProcessDefinitionStore;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.AbstractWorkflowDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowDefinitionImpl;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * User: davey
 * Date: 2014-08-15
 */
public class SQLWorkflowDefinitionDAO implements WorkflowDefinitionStore {
  private static final String TABLE_NAME = "WorkflowDefinition";

  private static final String WORKFLOWDEFINITION_SELECT =
      "SELECT workflowDefinitionId, userId, creation_date, name, description FROM " + TABLE_NAME;

  private static final String WORKFLOWDEFINITION_SELECT_BY_ID =
      WORKFLOWDEFINITION_SELECT + " WHERE workflowDefinitionId = ?";

  private static final String WORKFLOWDEFINITION_SELECT_BY_USER_ID =
      WORKFLOWDEFINITION_SELECT + " WHERE userId = ?";

  public static final String WORKFLOWDEFINITION_UPDATE =
      "UPDATE " + TABLE_NAME +
      " SET userId=:userId, creation_date=:creation_date, name=:name, description=:description " +
      "WHERE workflowDefinitionId=:workflowDefinitionId";

  public static final String WORKFLOWDEFINITION_DELETE =
      "DELETE FROM WorkflowDefinition WHERE workflowDefinitionId=:workflowDefinitionId";

  private static final String WORKFLOWDEFINITION_SELECT_BY_SEARCH =
      WORKFLOWDEFINITION_SELECT +
      " WHERE name LIKE ? OR description LIKE ?";

  private static final String WORKFLOWDEFINITION_STATE_SELECT =
      "SELECT sk.value as keyValue, ws.required " +
      "FROM WorkflowDefinition_State as ws " +
      "INNER JOIN State_Key sk ON sk.id = ws.state_key_id " +
      "WHERE ws.workflowDefinitionId = ?";

  private static final String WORKFLOWDEFINITION_STATE_DELETE =
      "DELETE FROM WorkflowDefinition_State WHERE workflowDefinitionId=:workflowDefinitionId";

  private static final String WORKFLOWDEFINITION_WORKFLOWPROCESSDEFINITION_DELETE =
      "DELETE FROM WorkflowDefinition_WorkflowProcessDefinition WHERE workflowDefinitionId=:workflowDefinitionId";

  protected static final Logger log = LoggerFactory.getLogger(SQLWorkflowDefinitionDAO.class);
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

  private StateStore stateDAO;

  public void setStateDAO(StateStore stateDAO) {
    this.stateDAO = stateDAO;
  }

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  private WorkflowProcessDefinitionStore workflowProcessDefinitonDAO;

  public void setWorkflowProcessDefinitionDAO(WorkflowProcessDefinitionStore workflowProcessDefinitonDAO) {
    this.workflowProcessDefinitonDAO = workflowProcessDefinitonDAO;
  }

  public long save(WorkflowDefinition workflowDefinition) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();

    if (workflowDefinition.getId() == AbstractWorkflowDefinition.UNSAVED_ID) {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      params.addValue("userId", user.getUserId());
    }
    else {
      params.addValue("userId", workflowDefinition.getCreator());
    }

    params.addValue("creation_date", workflowDefinition.getCreationDate())
          .addValue("name", workflowDefinition.getName())
          .addValue("description", workflowDefinition.getDescription());

    if (workflowDefinition.getId() == AbstractWorkflowDefinition.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
        .withTableName(TABLE_NAME)
        .usingGeneratedKeyColumns("workflowDefinitionId");
      try {
        workflowDefinition.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));
        Number newId = insert.executeAndReturnKey(params);
        if (newId.longValue() != workflowDefinition.getId()) {
          throw new IOException("Something bad happened. Expected WorkflowDefinition ID doesn't match returned value from DB insert");
        }
      }
      catch (IOException e) {
        throw new IOException("Cannot save workflow definition", e);
      }
    }
    else {
      //remove existing state and process mappings...
      NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(template);
      t.update(WORKFLOWDEFINITION_STATE_DELETE, new MapSqlParameterSource().addValue("workflowDefinitionId", workflowDefinition.getId()));
      t.update(WORKFLOWDEFINITION_WORKFLOWPROCESSDEFINITION_DELETE, new MapSqlParameterSource().addValue("workflowDefinitionId", workflowDefinition.getId()));
    }

    //...and replace with new
    for (String key : workflowDefinition.getStateFields()) {
      long keyId = stateDAO.getIdForKey(key);
      if (keyId == 0L) {
        keyId = stateDAO.saveKey(key);
      }

      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template)
        .withTableName("WorkflowDefinition_State");
      MapSqlParameterSource flParams = new MapSqlParameterSource();
      flParams.addValue("workflowDefinitionId", workflowDefinition.getId())
              .addValue("state_key_id", keyId)
              .addValue("required", true);
      try {
        eInsert.execute(flParams);
      }
      catch (DuplicateKeyException dke) {
        log.debug("This WorkflowDefinition/State_Key combination already exists - not inserting: " + dke.getMessage());
      }
    }

    for (Integer order : workflowDefinition.getWorkflowProcessDefinitions().keySet()) {
      WorkflowProcessDefinition wpd = workflowDefinition.getWorkflowProcessDefinitions().get(order);
      long wpdId = workflowProcessDefinitonDAO.save(wpd);

      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template)
        .withTableName("WorkflowDefinition_WorkflowProcessDefinition");
      MapSqlParameterSource flParams = new MapSqlParameterSource();
      flParams.addValue("workflowDefinitionId", workflowDefinition.getId())
              .addValue("workflowProcessDefinitionId", wpdId)
              .addValue("position", order);
      try {
        eInsert.execute(flParams);
      }
      catch (DuplicateKeyException dke) {
        log.debug("This WorkflowDefinition/State_Key combination already exists - not inserting: " + dke.getMessage());
      }
    }

    return workflowDefinition.getId();
  }

  @Override
  public WorkflowDefinition get(long workflowDefinitionId) throws IOException {
    List<WorkflowDefinition> eResults = template.query(WORKFLOWDEFINITION_SELECT_BY_ID, new Object[]{workflowDefinitionId}, new WorkflowDefinitionMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public WorkflowDefinition lazyGet(long workflowDefinitionId) throws IOException {
    List<WorkflowDefinition> eResults = template.query(WORKFLOWDEFINITION_SELECT_BY_ID, new Object[]{workflowDefinitionId}, new WorkflowDefinitionMapper(true));
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Collection<WorkflowDefinition> listAll() throws IOException {
    return template.query(WORKFLOWDEFINITION_SELECT, new WorkflowDefinitionMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM "+TABLE_NAME);
  }

  //public JSONObject getStateByWorkflowDefinitionId(long workflowProcessDefinitionId) {
  public Set<String> getStateByWorkflowDefinitionId(long workflowDefinitionId) {
    List<Map<String, Object>> rows = template.queryForList(WORKFLOWDEFINITION_STATE_SELECT, workflowDefinitionId);
    Set<String> fields = new HashSet<>();
    for (Map<String, Object> map : rows) {
      fields.add((String)map.get("keyValue"));

      //Boolean required = Boolean.parseBoolean((String)map.get("required"));
      //json.put("field", field);
      //json.put("required", required);
    }
    return fields;
  }

  @Override
  public Collection<WorkflowDefinition> listAllByCreator(long userId) throws IOException {
    return template.query(WORKFLOWDEFINITION_SELECT_BY_USER_ID, new Object[]{userId}, new WorkflowDefinitionMapper(true));
  }

  @Override
  public Collection<WorkflowDefinition> listBySearch(String searchStr) throws IOException {
    String mySQLQuery = "%" + searchStr.replaceAll("_", Matcher.quoteReplacement("\\_")) + "%";
    List<Map<String, Object>> results = template.queryForList(WORKFLOWDEFINITION_SELECT_BY_SEARCH, mySQLQuery, mySQLQuery);
    Set<WorkflowDefinition> wds = new TreeSet<>();
    if (!results.isEmpty()) {
      for (Map<String, Object> row : results) {
        Long wdId = (Long)row.get("workflowDefinitionId");
        WorkflowDefinition wd = get(wdId);
        if (wd != null) {
          wds.add(wd);
        }
      }
    }
    return wds;
  }

  public class WorkflowDefinitionMapper extends CacheAwareRowMapper<WorkflowDefinition> {
    public WorkflowDefinitionMapper() {
      super(WorkflowDefinition.class);
    }

    public WorkflowDefinitionMapper(boolean lazy) {
      super(WorkflowDefinition.class, lazy);
    }

    public WorkflowDefinition mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("workflowDefinitionId");
      /*
      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for WorkflowDefinition " + id);
          return (WorkflowDefinition)element.getObjectValue();
        }
      }
      */
      //get process definitions
      WorkflowDefinition wd = null;

      try {
        SortedMap<Integer, WorkflowProcessDefinition> processDefinitionMap = workflowProcessDefinitonDAO.getWorkflowProcessDefinitionsByWorkflowDefinition(rs.getLong("workflowDefinitionId"));
        wd = new WorkflowDefinitionImpl(processDefinitionMap);
        wd.setId(id);
        wd.setName(rs.getString("name"));
        wd.setDescription(rs.getString("description"));
        wd.setCreationDate(rs.getDate("creation_date"));
        wd.setCreator(securityManager.getUserById(rs.getLong("userId")));
        wd.setStateFields(getStateByWorkflowDefinitionId(wd.getId()));
        /*
        if (isCacheEnabled() && lookupCache(cacheManager) != null) {
          lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id) ,wd));
        }
        */
      }
      catch (IOException e) {
        e.printStackTrace();
      }

      return wd;
    }
  }
}

