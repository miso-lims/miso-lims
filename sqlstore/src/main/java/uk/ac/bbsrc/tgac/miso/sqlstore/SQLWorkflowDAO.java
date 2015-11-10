package uk.ac.bbsrc.tgac.miso.sqlstore;

import com.eaglegenomics.simlims.core.User;
import net.sf.ehcache.CacheManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.*;
import uk.ac.bbsrc.tgac.miso.core.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowImpl;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: davey
 * Date: 03/05/2014
 */
public class SQLWorkflowDAO implements WorkflowStore {
  private static final String TABLE_NAME = "Workflow";

  private static final String WORKFLOW_SELECT =
      "SELECT workflowId, userId, alias, start_date, completion_date, status, workflowDefinition_definitionId FROM " + TABLE_NAME;

  private static final String WORKFLOW_SELECT_BY_ID =
      WORKFLOW_SELECT + " WHERE workflowId = ?";

  private static final String WORKFLOW_SELECT_BY_USER_ID =
      WORKFLOW_SELECT + " WHERE userId = ?";

  private static final String WORKFLOW_SELECT_BY_INVERSE_STATUS =
      WORKFLOW_SELECT + " WHERE status <> ?";

  private static final String WORKFLOW_SELECT_BY_STATUS =
      WORKFLOW_SELECT + " WHERE status = ?";

  private static final String WORKFLOW_INSERT =
      "INSERT INTO "+TABLE_NAME+" (userId, alias, start_date, completion_date, status, workflowDefinition_definitionId) " +
      "values(?,?,?,?,?,?)";

  private static final String WORKFLOW_UPDATE =
      "UPDATE " + TABLE_NAME +
      " SET userId=:userId, alias=:alias, start_date=:start_date, completion_date=:completion_date, status=:status, workflowDefinition_definitionId=:workflowDefinition_definitionId " +
      "WHERE workflowId=:workflowId";

  private static final String WORKFLOW_DELETE =
      "DELETE FROM "+TABLE_NAME+" WHERE workflowId=:workflowId";

  private static final String WORKFLOW_STATE_SELECT =
      "SELECT sk.id as stateKeyId, sk.value as stateKey, sv.id as stateValueId, sv.value as stateValue " +
      "FROM Workflow_State as ws " +
      "INNER JOIN State_Key sk ON sk.id = ws.state_key_id " +
      "INNER JOIN State_Value sv ON sv.id = ws.state_value_id " +
      "WHERE ws.workflowId = ?";

  private static final String WORKFLOW_STATE_UPDATE =
      "UPDATE Workflow_State SET state_key_id=:state_key_id, state_value_id=:state_value_id WHERE workflowId=:workflowId";

  private static final String WORKFLOW_STATE_DELETE =
      "DELETE FROM Workflow_State WHERE workflowId=:workflowId";

  protected static final Logger log = LoggerFactory.getLogger(SQLWorkflowDAO.class);

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

  private WorkflowDefinitionStore workflowDefinitionDAO;

  public void setWorkflowDefinitionDAO(WorkflowDefinitionStore workflowDefinitionDAO) {
    this.workflowDefinitionDAO = workflowDefinitionDAO;
  }

  private WorkflowProcessStore workflowProcessDAO;

  public void setWorkflowProcessDAO(WorkflowProcessStore workflowProcessDAO) {
    this.workflowProcessDAO = workflowProcessDAO;
  }

  private AssigneeStore assigneeDAO;

  public void setAssigneeDAO(AssigneeStore assigneeDAO) {
    this.assigneeDAO = assigneeDAO;
  }

  private StateStore stateDAO;

  public void setStateDAO(StateStore stateDAO) {
    this.stateDAO = stateDAO;
  }

  private AttachableStore attachableDAO;

  public void setAttachableDAO(AttachableStore attachableDAO) {
    this.attachableDAO = attachableDAO;
  }

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public long save(final Workflow workflow) throws IOException {
    final User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

    NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(template);

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("userId", user.getUserId())
        .addValue("alias", workflow.getAlias())
        .addValue("start_date", workflow.getStartDate())
        .addValue("completion_date", workflow.getCompletionDate())
        .addValue("status", workflow.getStatus().getKey())
        .addValue("workflowDefinition_definitionId", workflow.getWorkflowDefinition().getId());

    if (workflow.getId() == WorkflowImpl.UNSAVED_ID) {
      KeyHolder keyHolder = new GeneratedKeyHolder();
      template.update(
        new PreparedStatementCreator() {
          public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement(WORKFLOW_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, user.getUserId());
            ps.setString(2, workflow.getAlias());
            ps.setDate(3, new java.sql.Date(workflow.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(workflow.getCompletionDate().getTime()));
            ps.setString(5, workflow.getStatus().getKey());
            ps.setLong(6, workflow.getWorkflowDefinition().getId());
            return ps;
          }
        },
        keyHolder);

      try {
        workflow.setId(DbUtils.getAutoIncrement(template, "Workflow"));
        Number newId = keyHolder.getKey();
        if (newId.longValue() != workflow.getId()) {
          throw new IOException("Something bad happened. Expected Workflow ID doesn't match returned value from DB insert");
        }
        else {
          //remove existing state mappings and replace with new
          List<Map<Long, Long>> stateMap = stateDAO.saveAll(workflow.getState());
          for (Map<Long, Long> pair : stateMap) {
            for (long keyId : pair.keySet()) {
              t.update(WORKFLOW_STATE_UPDATE,
                new MapSqlParameterSource()
                  .addValue("workflowId", workflow.getId())
                  .addValue("state_key_id", keyId)
                  .addValue("state_value_id", pair.get(keyId)));
            }
          }

          //process any attached entities
          attachableDAO.save(workflow);
        }
      }
      catch (IOException e) {
        throw new IOException("Cannot save workflow", e);
      }
    }
    else {
      params.addValue("workflowId", workflow.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(WORKFLOW_UPDATE, params);
    }

    return workflow.getId();
  }

  @Override
  public Workflow get(long workflowId) throws IOException {
    List<Workflow> eResults = template.query(WORKFLOW_SELECT_BY_ID, new Object[]{workflowId}, new WorkflowMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Workflow lazyGet(long workflowId) throws IOException {
    List<Workflow> eResults = template.query(WORKFLOW_SELECT_BY_ID, new Object[]{workflowId}, new WorkflowMapper(true));
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Collection<Workflow> listAll() throws IOException {
    return template.query(WORKFLOW_SELECT, new WorkflowMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public Collection<Workflow> listAllByAssignee(long userId) throws IOException {
    return template.query(WORKFLOW_SELECT_BY_USER_ID, new Object[]{userId}, new WorkflowMapper());
  }

  @Override
  public Collection<Workflow> listAllIncomplete() throws IOException {
    return template.query(WORKFLOW_SELECT_BY_INVERSE_STATUS, new Object[]{HealthType.Completed.getKey()}, new WorkflowMapper());
  }

  @Override
  public Collection<Workflow> listAllByStatus(HealthType healthType) throws IOException {
    return template.query(WORKFLOW_SELECT_BY_STATUS, new Object[]{healthType.getKey()}, new WorkflowMapper());
  }

  public JSONObject getStateByWorkflowId(long workflowProcessId) {
    List<Map<String, Object>> rows = template.queryForList(WORKFLOW_STATE_SELECT, workflowProcessId);
    JSONObject json = new JSONObject();
    JSONArray kvs = new JSONArray();
    for (Map<String, Object> map : rows) {
      JSONObject kv = new JSONObject();
      String stateKey = (String)map.get("stateKey");
      String stateValue = (String)map.get("stateValue");
      kv.put("key", stateKey);
      kv.put("value", stateValue);
      kv.put("keyId", (Long)map.get("stateKeyId"));
      kv.put("valueId", (Long)map.get("stateValueId"));
      kvs.add(kv);
    }
    json.put("state", kvs);
    return json;
  }

  public class WorkflowMapper extends CacheAwareRowMapper<Workflow> {
    public WorkflowMapper() {
      super(Workflow.class);
    }

    public WorkflowMapper(boolean lazy) {
      super(Workflow.class, lazy);
    }

    public Workflow mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("workflowId");
      /*
      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for Workflow " + id);
          return (Workflow)element.getObjectValue();
        }
      }
      */
      Workflow w = null;

      long workflowDefinition_definitionId = rs.getLong("workflowDefinition_definitionId");
      try {
        WorkflowDefinition wd = workflowDefinitionDAO.get(workflowDefinition_definitionId);
        w = new WorkflowImpl(wd, getStateByWorkflowId(id));
        w.setId(id);
        w.setAlias(rs.getString("alias"));
        w.setStartDate(rs.getDate("start_date"));
        w.setCompletionDate(rs.getDate("completion_date"));
        w.setWorkflowProcesses(workflowProcessDAO.getWorkflowProcessesByWorkflowId(id));
        w.setStatus(HealthType.get(rs.getString("status")));

        try {
          w.setAssignee(assigneeDAO.getAssigneeByEntityName(w.getAssignableIdentifier()));

          if (!isLazy()) {
            for (Nameable n : attachableDAO.listAttachedByAttachable(w)) {
              w.attach(n);
            }
          }
        }
        catch (IOException e1) {
          e1.printStackTrace();
        }
        /*
        if (isCacheEnabled() && lookupCache(cacheManager) != null) {
          lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id) ,w));
        }
        */
      }
      catch (IOException ioe) {
        log.error("Cannot retrieve workflow definition " + workflowDefinition_definitionId + " for workflow " + id + ": " + ioe.getMessage());
        ioe.printStackTrace();
      }

      return w;
    }
  }
}

