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
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.StateStore;
import uk.ac.bbsrc.tgac.miso.core.store.WorkflowProcessDefinitionStore;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowProcessDefinitionImpl;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Created with IntelliJ IDEA.
 * User: bianx
 * Date: 02/12/2013
 * Time: 10:04
 */
public class SQLWorkflowProcessDefinitionDAO implements WorkflowProcessDefinitionStore {
  private static final String TABLE_NAME = "WorkflowProcessDefinition";

  private static final String WORKFLOWPROCESSDEFINITION_SELECT =
      "SELECT workflowProcessDefinitionId, userId, creation_date, name, description, inputType, outputType FROM " + TABLE_NAME;

  private static final String WORKFLOWPROCESSDEFINITION_SELECT_BY_ID =
      WORKFLOWPROCESSDEFINITION_SELECT + " WHERE workflowProcessDefinitionId = ?";

  private static final String WORKFLOWPROCESSDEFINITION_SELECT_BY_USER_ID =
      WORKFLOWPROCESSDEFINITION_SELECT + " WHERE userId = ?";

  private static final String WORKFLOWPROCESSDEFINITION_UPDATE =
      "UPDATE " + TABLE_NAME +
      " SET userId=:userId, creation_date=:creation_date, name=:name, description=:description, inputType=:inputType, outputType=:outputType " +
      "WHERE workflowProcessDefinitionId=:workflowProcessDefinitionId";

  private static final String WORKFLOWPROCESSDEFINITION_DELETE =
      "DELETE FROM "+TABLE_NAME+" WHERE workflowProcessDefinitionId=:workflowProcessDefinitionId";

  private static final String WORKFLOWPROCESSDEFINITION_STATE_SELECT =
      "SELECT sk.value as keyValue, ws.required " +
      "FROM WorkflowProcessDefinition_State as ws " +
      "INNER JOIN State_Key sk ON sk.id = ws.state_key_id " +
      "WHERE ws.workflowProcessDefinitionId = ?";

  private static final String WORKFLOWPROCESSDEFINITION_SELECT_BY_WORKFLOWDEFINITION_ID =
      "SELECT wdwpd.workflowProcessDefinitionId, wdwpd.position "+
      "FROM WorkflowDefinition_WorkflowProcessDefinition wdwpd " +
      "WHERE wdwpd.workflowDefinitionId = ?";

  private static final String WORKFLOWPROCESSDEFINITION_SELECT_BY_SEARCH =
      WORKFLOWPROCESSDEFINITION_SELECT +
      " WHERE name LIKE ? OR description LIKE ?";

  private static final String WORKFLOWPROCESSDEFINITION_STATE_DELETE =
      "DELETE FROM WorkflowProcessDefinition_State WHERE workflowProcessDefinitionId=:workflowProcessDefinitionId";

  protected static final Logger log = LoggerFactory.getLogger(SQLWorkflowProcessDefinitionDAO.class);

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

  public long save(WorkflowProcessDefinition workflowProcessDefinition) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();

    if (workflowProcessDefinition.getId() == WorkflowProcessDefinitionImpl.UNSAVED_ID) {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      params.addValue("userId", user.getUserId());
    }
    else {
      params.addValue("userId", workflowProcessDefinition.getCreator().getUserId());
    }

    params.addValue("creation_date", workflowProcessDefinition.getCreationDate())
          .addValue("name", workflowProcessDefinition.getName())
          .addValue("description", workflowProcessDefinition.getDescription());

    if (workflowProcessDefinition.getInputType() != null) {
      params.addValue("inputType", workflowProcessDefinition.getInputType().getName());
      log.info("added inputType " + workflowProcessDefinition.getInputType().getName());
    }

    if (workflowProcessDefinition.getOutputType() != null) {
      params.addValue("outputType", workflowProcessDefinition.getOutputType().getName());
      log.info("added outputType " + workflowProcessDefinition.getInputType().getName());
    }

    if (workflowProcessDefinition.getId() == WorkflowProcessDefinitionImpl.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
        .withTableName(TABLE_NAME)
        .usingGeneratedKeyColumns("workflowProcessDefinitionId");
      try {
        workflowProcessDefinition.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));
        Number newId = insert.executeAndReturnKey(params);
        if (newId.longValue() != workflowProcessDefinition.getId()) {
          throw new IOException("Something bad happened. Expected WorkflowProcessDefinition ID doesn't match returned value from DB insert");
        }
      }
      catch (IOException e) {
        throw new IOException("Cannot save workflow process definition", e);
      }
    }
    else {
      //persist state
      //remove existing state mappings...
      NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(template);
      t.update(WORKFLOWPROCESSDEFINITION_STATE_DELETE, new MapSqlParameterSource().addValue("workflowProcessDefinitionId", workflowProcessDefinition.getId()));

      //update the static fields
      params.addValue("workflowProcessDefinitionId", workflowProcessDefinition.getId());
      t.update(WORKFLOWPROCESSDEFINITION_UPDATE, params);
    }

    //...and replace with new
    for (String key : workflowProcessDefinition.getStateFields()) {
      long keyId = stateDAO.getIdForKey(key);
      if (keyId == 0L) {
        keyId = stateDAO.saveKey(key);
      }

      SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template)
        .withTableName("WorkflowProcessDefinition_State");
      MapSqlParameterSource flParams = new MapSqlParameterSource();
      flParams.addValue("workflowProcessDefinitionId", workflowProcessDefinition.getId())
              .addValue("state_key_id", keyId)
              .addValue("required", true);
      try {
        eInsert.execute(flParams);
      }
      catch (DuplicateKeyException dke) {
        log.debug("This WorkflowProcessDefinition/State_Key combination already exists - not inserting: " + dke.getMessage());
      }
    }

    return workflowProcessDefinition.getId();
  }

  @Override
  public WorkflowProcessDefinition get(long workflowProcessDefinitionId) throws IOException {
    List<WorkflowProcessDefinition> eResults = template.query(WORKFLOWPROCESSDEFINITION_SELECT_BY_ID, new Object[]{workflowProcessDefinitionId}, new WorkflowProcessDefinitionMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public WorkflowProcessDefinition lazyGet(long workflowProcessDefinitionId) throws IOException {
    List<WorkflowProcessDefinition> eResults = template.query(WORKFLOWPROCESSDEFINITION_SELECT_BY_ID, new Object[]{workflowProcessDefinitionId}, new WorkflowProcessDefinitionMapper(true));
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  //public JSONObject getStateByWorkflowProcessDefinitionId(long workflowProcessDefinitionId) {
  public Set<String> getStateByWorkflowProcessDefinitionId(long workflowProcessDefinitionId) {
    List<Map<String, Object>> rows = template.queryForList(WORKFLOWPROCESSDEFINITION_STATE_SELECT, workflowProcessDefinitionId);
    Set<String> fields = new HashSet<>();
    for (Map<String, Object> map : rows) {
      fields.add((String)map.get("keyValue"));

      //Boolean required = Boolean.parseBoolean((String)map.get("ws.required"));
      //json.put("field", field);
      //json.put("required", required);
    }
    return fields;
  }

  @Override
  public SortedMap<Integer, WorkflowProcessDefinition> getWorkflowProcessDefinitionsByWorkflowDefinition(long workflowDefinitionId) throws IOException {
    List<Map<String, Object>> eResults = template.queryForList(WORKFLOWPROCESSDEFINITION_SELECT_BY_WORKFLOWDEFINITION_ID, workflowDefinitionId);
    SortedMap<Integer, WorkflowProcessDefinition> wpds = new TreeMap<>();
    if (!eResults.isEmpty()) {
      for (Map<String, Object> row : eResults) {
        Long wpdId = (Long)row.get("workflowProcessDefinitionId");
        Integer order = (Integer)row.get("position");
        WorkflowProcessDefinition wpd = get(wpdId);
        if (wpd != null) {
          wpds.put(order, wpd);
        }
      }
    }
    return wpds;
  }

  @Override
  public Collection<WorkflowProcessDefinition> listAll() throws IOException {
    return template.query(WORKFLOWPROCESSDEFINITION_SELECT, new WorkflowProcessDefinitionMapper(true));
  }

  @Override
  public Collection<WorkflowProcessDefinition> listBySearch(String str) throws IOException {
    String mySQLQuery = "%" + str.replaceAll("_", Matcher.quoteReplacement("\\_")) + "%";
    List<Map<String, Object>> results = template.queryForList(WORKFLOWPROCESSDEFINITION_SELECT_BY_SEARCH, mySQLQuery, mySQLQuery);
    Set<WorkflowProcessDefinition> wpds = new TreeSet<>();
    if (!results.isEmpty()) {
      for (Map<String, Object> row : results) {
        Long wpdId = (Long)row.get("workflowProcessDefinitionId");
        WorkflowProcessDefinition wpd = get(wpdId);
        if (wpd != null) {
          wpds.add(wpd);
        }
      }
    }
    return wpds;
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM "+TABLE_NAME);
  }

  @Override
  public Collection<WorkflowProcessDefinition> listAllByCreator(long userId) throws IOException {
    return template.query(WORKFLOWPROCESSDEFINITION_SELECT_BY_USER_ID, new Object[]{userId}, new WorkflowProcessDefinitionMapper(true));
  }

  public class WorkflowProcessDefinitionMapper extends CacheAwareRowMapper<WorkflowProcessDefinition> {
    public WorkflowProcessDefinitionMapper() {
      super(WorkflowProcessDefinition.class);
    }

    public WorkflowProcessDefinitionMapper(boolean lazy) {
      super(WorkflowProcessDefinition.class, lazy);
    }

    public WorkflowProcessDefinition mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("workflowProcessDefinitionId");
      /*
      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for WorkflowProcessDefinition " + id);
          return (WorkflowProcessDefinition)element.getObjectValue();
        }
      }
      */
      try {
        WorkflowProcessDefinition wd = new WorkflowProcessDefinitionImpl();
        wd.setId(id);
        wd.setName(rs.getString("name"));
        wd.setDescription(rs.getString("description"));
        wd.setCreationDate(rs.getDate("creation_date"));
        wd.setCreator(securityManager.getUserById(rs.getLong("userId")));
        wd.setStateFields(getStateByWorkflowProcessDefinitionId(wd.getId()));
        if (rs.getString("inputType") != null) wd.setInputType((Class<? extends Nameable>) Class.forName(rs.getString("inputType")));
        if (rs.getString("outputType") != null) wd.setOutputType((Class<? extends Nameable>) Class.forName(rs.getString("outputType")));

        /*
        if (isCacheEnabled() && lookupCache(cacheManager) != null) {
          lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id) ,wd));
        }
        */
        return wd;
      }
      catch (IOException e) {
        throw new SQLException(e);
      }
      catch (ClassNotFoundException e) {
        throw new SQLException("Cannot map inputType/outputType to a valid class", e);
      }
    }
  }
}

