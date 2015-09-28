package uk.ac.bbsrc.tgac.miso.sqlstore;

import com.eaglegenomics.simlims.core.User;
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
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.*;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcess;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowProcessImpl;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: davey
 * Date: 12/05/2014
 */
public class SQLWorkflowProcessDAO implements WorkflowProcessStore {
  private static final String TABLE_NAME = "WorkflowProcess";

  private static final String WORKFLOWPROCESS_SELECT =
      "SELECT workflowProcessId, userId, start_date, completion_date, workflowProcessDefinition_definitionId FROM " + TABLE_NAME;

  private static final String WORKFLOWPROCESS_SELECT_BY_ID =
      WORKFLOWPROCESS_SELECT + " WHERE workflowProcessId = ?";

  private static final String WORKFLOWPROCESS_SELECT_BY_WORKFLOWPROCESSDEFINITION_ID =
      WORKFLOWPROCESS_SELECT + " WHERE workflowProcessDefinition_definitionId = ?";

  private static final String WORKFLOWPROCESS_SELECT_BY_USER_ID =
      WORKFLOWPROCESS_SELECT + " WHERE userId = ?";

  private static final String WORKFLOWPROCESS_SELECT_BY_WORKFLOW_ID =
      "SELECT workflowProcessId, userId, start_date, completion_date, workflowProcessDefinition_definitionId " +
      "FROM " + TABLE_NAME + " " +
      "INNER JOIN Workflow_WorkflowProcess wwp ON wwp.processId = workflowProcessId "+
      "WHERE wwp.workflowId = ?";

  private static final String WORKFLOWPROCESS_UPDATE =
      "UPDATE " + TABLE_NAME + " " +
      "SET userId=:userId, start_date=:start_date, completion_date=:completion_date, workflowProcessDefinition_definitionId=:workflowProcessDefinition_definitionId " +
      "WHERE workflowProcessId=:workflowProcessId";

  private static final String WORKFLOWPROCESS_DELETE =
      "DELETE FROM "+TABLE_NAME+" WHERE workflowProcessId=:workflowProcessId";

  private static final String WORKFLOWPROCESS_STATE_SELECT =
      "SELECT sk.id as stateKeyId, sk.value as stateKey, sv.id as stateValueId, sv.value as stateValue " +
      "FROM WorkflowProcess_State as ws " +
      "INNER JOIN State_Key sk ON sk.id = ws.state_key_id " +
      "INNER JOIN State_Value sv ON sv.id = ws.state_value_id " +
      "WHERE ws.processId = ?";

  private static final String WORKFLOWPROCESS_STATE_UPDATE =
      "UPDATE WorkflowProcess_State SET state_key_id=:state_key_id, state_value_id=:state_value_id WHERE processId=:processId";

  private static final String WORKFLOWPROCESS_STATE_DELETE =
      "DELETE FROM WorkflowProcess_State WHERE processId=:processId";

  protected static final Logger log = LoggerFactory.getLogger(SQLWorkflowProcessDAO.class);

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

  private WorkflowProcessDefinitionStore workflowProcessDefinitionDAO;

  public void setWorkflowProcessDefinitionDAO(WorkflowProcessDefinitionStore workflowProcessDefinitionDAO) {
    this.workflowProcessDefinitionDAO = workflowProcessDefinitionDAO;
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

  public long save(WorkflowProcess workflowProcess) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("start_date", workflowProcess.getStartDate())
        .addValue("completion_date", workflowProcess.getCompletionDate())
        .addValue("status", workflowProcess.getStatus().getKey())
        .addValue("workflowProcessDefinition_definitionId", workflowProcess.getDefinition().getId());

    if (workflowProcess.getId() == WorkflowProcessImpl.UNSAVED_ID && workflowProcess.getAssignee() == null) {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      params.addValue("userId", user.getUserId());
    }
    else {
      params.addValue("userId", workflowProcess.getAssignee());
    }

    SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
      .withTableName("WorkflowProcess")
      .usingGeneratedKeyColumns("workflowProcessId");
    try {
      workflowProcess.setId(DbUtils.getAutoIncrement(template, "WorkflowProcess"));
      Number newId = insert.executeAndReturnKey(params);
      if (newId.longValue() != workflowProcess.getId()) {
        throw new IOException("Something bad happened. Expected WorkflowProcess ID doesn't match returned value from DB insert");
      }
      else {
        //remove existing state mappings...
        NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(template);
        //t.update(WORKFLOWPROCESS_STATE_DELETE, new MapSqlParameterSource().addValue("processId", workflowProcess.getId()));

        //...and replace with new
        List<Map<Long, Long>> stateMap = stateDAO.saveAll(workflowProcess.getState());
        for (Map<Long, Long> pair : stateMap) {
          for (long keyId : pair.keySet()) {
            t.update(WORKFLOWPROCESS_STATE_UPDATE,
              new MapSqlParameterSource()
                .addValue("processId", workflowProcess.getId())
                .addValue("state_key_id", keyId)
                .addValue("state_value_id", pair.get(keyId)));
          }
        }

        //process any attached entities
        attachableDAO.save(workflowProcess);
      }
    }
    catch (IOException e) {
      throw new IOException("Cannot save workflow process", e);
    }

    return workflowProcess.getId();
  }

  @Override
  public WorkflowProcess get(long workflowProcessId) throws IOException {
    List<WorkflowProcess> eResults = template.query(WORKFLOWPROCESS_SELECT_BY_ID, new Object[]{workflowProcessId}, new WorkflowProcessMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public WorkflowProcess lazyGet(long workflowProcessId) throws IOException {
    List<WorkflowProcess> eResults = template.query(WORKFLOWPROCESS_SELECT_BY_ID, new Object[]{workflowProcessId}, new WorkflowProcessMapper(true));
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Collection<WorkflowProcess> listAll() throws IOException {
    return template.query(WORKFLOWPROCESS_SELECT, new WorkflowProcessMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public List<WorkflowProcess> getWorkflowProcessesByWorkflowId(long workflowId) {
    return template.query(WORKFLOWPROCESS_SELECT_BY_WORKFLOW_ID, new Object[]{workflowId}, new WorkflowProcessMapper());
  }

  @Override
  public Collection<WorkflowProcess> listAllByAssignee(long userId) throws IOException {
    return template.query(WORKFLOWPROCESS_SELECT_BY_USER_ID, new Object[]{userId}, new WorkflowProcessMapper());
  }

  public JSONObject getStateByWorkflowProcessId(long workflowProcessId) {
    List<Map<String, Object>> rows = template.queryForList(WORKFLOWPROCESS_STATE_SELECT, workflowProcessId);
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

  public class WorkflowProcessMapper extends CacheAwareRowMapper<WorkflowProcess> {
    public WorkflowProcessMapper() {
      super(WorkflowProcess.class);
    }

    public WorkflowProcessMapper(boolean lazy) {
      super(WorkflowProcess.class, lazy);
    }

    public WorkflowProcess mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("workflowProcessId");
      /*
      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for WorkflowProcess " + id);
          return (WorkflowProcess)element.getObjectValue();
        }
      }
      */
      WorkflowProcess w = null;

      long workflowProcessDefinition_definitionId = rs.getLong("workflowProcessDefinition_definitionId");
      try {
        WorkflowProcessDefinition wd = workflowProcessDefinitionDAO.get(workflowProcessDefinition_definitionId);
        w = new WorkflowProcessImpl(wd, getStateByWorkflowProcessId(id));
        w.setId(id);
        w.setStartDate(rs.getDate("start_date"));
        w.setCompletionDate(rs.getDate("completion_date"));
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
        log.error("Cannot retrieve workflow process definition " + workflowProcessDefinition_definitionId + " for workflow process " + id + ": " + ioe.getMessage());
        ioe.printStackTrace();
      }

      return w;
    }
  }
}

