/*
 * Copyright (c) 2014. The Genome Analysis Centre, Norwich, UK
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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import com.eaglegenomics.simlims.core.User;
import com.google.json.JsonSanitizer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.MutableWorkflowDefinitionImpl;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowDefinitionImpl;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowImpl;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowProcessDefinitionImpl;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 *
 * @author Rob Davey
 * @date 14/05/14
 * @since 0.2.1-SNAPSHOT
 */
@Ajaxified
public class WorkflowControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(WorkflowControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public JSONObject listWorkflowsDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Workflow workflow : requestManager.listAllWorkflows()) {
        /*
            { "sTitle": "Name"},
            { "sTitle": "Description"},
            { "sTitle": "Alias"},
            { "sTitle": "Assigned To"},
            { "sTitle": "Status"},
            { "sTitle": "Start Date"},
            { "sTitle": "Completion Date"},
            { "sTitle": "Progress"},
            { "sTitle": "View"}
        */
        jsonArray.add(JsonSanitizer.sanitize("[\"" +
          workflow.getWorkflowDefinition().getName() + "\",\"" +
          workflow.getWorkflowDefinition().getDescription() + "\",\"" +
          workflow.getAlias() + "\",\"" +
          workflow.getAssignee().getFullName() + "\",\"" +

          (workflow.getStatus() != null ? workflow.getStatus().getKey() : "") + "\",\"" +
          (workflow.getStartDate() != null ? LimsUtils.getDateAsString(workflow.getStartDate()) : "") + "\",\"" +
          (workflow.getCompletionDate() != null ? LimsUtils.getDateAsString(workflow.getCompletionDate()) : "") + "\",\"" +
          (workflow.getCurrentProcess() != null ? workflow.getCurrentProcess() + "/" + workflow.getWorkflowDefinition().getWorkflowProcessDefinitions().size() : "") + "\",\"" +
          "<a href=\"/miso/workflow/" + workflow.getId() + "\"><span class=\"fa fa-pencil-square-o fa-lg\"></span></a>" + "\"]"));
      }
      j.put("workflowsArray", jsonArray);
      return j;
    }
    catch (IOException e) {
      e.printStackTrace();
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject listIncompleteWorkflowsDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Workflow workflow : requestManager.listIncompleteWorkflows()) {
        /*
            { "sTitle": "Name"},
            { "sTitle": "Description"},
            { "sTitle": "Alias"},
            { "sTitle": "Assigned To"},
            { "sTitle": "Status"},
            { "sTitle": "Start Date"},
            { "sTitle": "Progress"},
            { "sTitle": "View"}
        */
        jsonArray.add(JsonSanitizer.sanitize("[\"" + workflow.getWorkflowDefinition().getName() + "\",\"" +
          workflow.getWorkflowDefinition().getDescription() + "\",\"" +
          workflow.getAlias() + "\",\"" +
          workflow.getAssignee().getFullName() + "\",\"" +
          (workflow.getStatus() != null ? workflow.getStatus().getKey() : "") + "\",\"" +
          (workflow.getStartDate() != null ? LimsUtils.getDateAsString(workflow.getStartDate()) : "") + "\",\"" +
          (workflow.getCurrentProcess() != null ? workflow.getCurrentProcess() + "/" + workflow.getWorkflowDefinition().getWorkflowProcessDefinitions().size() : "") + "\",\"" +
          "<a href=\"/miso/workflow/" + workflow.getId() + "\"><span class=\"fa fa-pencil-square-o fa-lg\"></span></a>" + "\"]"));
      }
      j.put("incompleteWorkflowsArray", jsonArray);
      return j;
    }
    catch (IOException e) {
      e.printStackTrace();
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject listAssignedWorkflowsDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();

      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      for (Workflow workflow : requestManager.listWorkflowsByAssignee(user.getUserId())) {
        /*
            { "sTitle": "Name"},
            { "sTitle": "Description"},
            { "sTitle": "Alias"},
            { "sTitle": "Status"},
            { "sTitle": "Start Date"},
            { "sTitle": "Completion Date"},
            { "sTitle": "Progress"},
            { "sTitle": "View"}
        */
        jsonArray.add(JsonSanitizer.sanitize("[\"" + workflow.getWorkflowDefinition().getName() + "\",\"" +
                      workflow.getWorkflowDefinition().getDescription() + "\",\"" +
                      workflow.getAlias() + "\",\"" +
                      (workflow.getStatus() != null ? workflow.getStatus().getKey() : "") + "\",\"" +
                      (workflow.getStartDate() != null ? LimsUtils.getDateAsString(workflow.getStartDate()) : "") + "\",\"" +
                      (workflow.getCompletionDate() != null ? LimsUtils.getDateAsString(workflow.getCompletionDate()) : "") + "\",\"" +
                      (workflow.getCurrentProcess() != null ? workflow.getCurrentProcess() + "/" + workflow.getWorkflowDefinition().getWorkflowProcessDefinitions().size() : "") + "\",\"" +
                      "<a href=\"/miso/workflow/" + workflow.getId() + "\"><span class=\"fa fa-pencil-square-o fa-lg\"></span></a>" + "\"]"));
      }
      j.put("assignedWorkflowsArray", jsonArray);
      return j;
    }
    catch (IOException e) {
      e.printStackTrace();
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject searchStateFieldKeys(HttpSession session, JSONObject json) {
    String searchStr = json.getString("key");
    Boolean disable = json.has("disable") && json.getBoolean("disable");
    Map<String, Object> responseMap = new HashMap<>();
    JSONArray response = new JSONArray();
    try {
      Map<Long, String> keys = requestManager.listStateKeysBySearch(searchStr);
      if (!keys.isEmpty()) {
        for (Long index : keys.keySet()) {
          JSONObject j = new JSONObject();
          j.put("id", index);
          j.put("text", keys.get(index));
          if (disable) j.put("disabled", true);
          response.add(j);
        }
      }
      responseMap.put("response", response);
      return JSONUtils.JSONObjectResponse(responseMap);
    }
    catch (IOException e) {
      e.printStackTrace();
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject searchWorkflowProcessDefinitions(HttpSession session, JSONObject json) {
    String searchStr = json.getString("query");
    Map<String, Object> responseMap = new HashMap<>();
    JSONArray response = new JSONArray();
    try {
      Collection<WorkflowProcessDefinition> wpds = requestManager.listWorkflowProcessDefinitionsBySearch(searchStr);
      for (WorkflowProcessDefinition wpd : wpds) {
        JSONObject j = new JSONObject();
        j.put("id", wpd.getId());
        j.put("text", wpd.getName());
        response.add(j);
      }
      responseMap.put("response", response);
      return JSONUtils.JSONObjectResponse(responseMap);
    }
    catch (IOException e) {
      e.printStackTrace();
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject addWorkflowDefinition(HttpSession session, JSONObject json) {
    String def = json.getString("definition");
    if (!LimsUtils.isStringEmptyOrNull(def)) {
      try {
        JSONObject j = JSONObject.fromObject(def);
        String name = j.getString("name");
        String description = j.getString("description");
        JSONArray keys = j.getJSONArray("keys");
        JSONArray processes = j.getJSONArray("processes");

        if (!LimsUtils.isStringEmptyOrNull(name) && !LimsUtils.isStringEmptyOrNull(description)) {
          SortedMap<Integer, WorkflowProcessDefinition> processMap = new TreeMap<>();
          for(int i = 0; i < processes.size(); i++){
            JSONObject p = processes.getJSONObject(i);
            if (!p.has("order") || !p.has("processId")) {
              return JSONUtils.SimpleJSONError("Supplied WorkflowProcessDefinitions are invalid.");
            }
            processMap.put(p.getInt("order"), requestManager.getWorkflowProcessDefinitionById(p.getLong("processId")));
          }

          WorkflowDefinition wd = null;

          if (j.has("id")) {
            WorkflowDefinition wfd = requestManager.getWorkflowDefinitionById(j.getLong("id"));
            if (wfd != null) {
              wd = new MutableWorkflowDefinitionImpl(wfd);
              wd.setWorkflowProcessDefinitions(processMap);
            }
          }
          else {
            wd = new WorkflowDefinitionImpl(processMap);
            wd.setName(name);
            wd.setDescription(description);
            wd.setCreationDate(new Date());
          }

          if (wd != null) {
            Set<String> stateKeys = new HashSet<>();
            for(int i = 0; i < keys.size(); i++) {
              //long keyId = requestManager.getIdForStateKey(keys.getString(i));
              //if (keyId != 0L) stateKeys.add(keys.getString(i));

              JSONObject p = keys.getJSONObject(i);
              if (!p.has("keyId") || !p.has("keyText")) {
                return JSONUtils.SimpleJSONError("Supplied WorkflowDefinition state keys are invalid.");
              }
              stateKeys.add(p.getString("keyText"));
            }
            wd.setStateFields(stateKeys);

            log.info("keys: [" + LimsUtils.join(wd.getStateFields(), ",") + "]");
            log.info("processes: [" + LimsUtils.join(wd.getWorkflowProcessDefinitions().values(), ",") + "]");

            long wpdId = requestManager.saveWorkflowDefinition(wd);
            wd.setId(wpdId);
          }
          else {
            return JSONUtils.SimpleJSONError("No valid WorkflowDefinition found");
          }

          Map<String, Object> responseMap = new HashMap<>();
          JSONObject res = new JSONObject();
          res.put("id", wd.getId());
          res.put("name", wd.getName());
          res.put("description", wd.getDescription());
          res.put("creationDate", LimsUtils.getDateAsString(wd.getCreationDate()));
          responseMap.put("definition", res);
          return JSONUtils.JSONObjectResponse(responseMap);
        }
        else {
          return JSONUtils.SimpleJSONError("A definition needs a name and a description");
        }
      }
      catch (IOException e) {
        e.printStackTrace();
        log.error("Failed", e);
        return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
      }
    }
    else {
      log.error("No definition attributes specified to save");
      return JSONUtils.SimpleJSONError("No definition attributes specified to save");
    }
  }

  public JSONObject addWorkflowProcessDefinition(HttpSession session, JSONObject json) {
    String processDef = json.getString("processDefinition");
    if (!LimsUtils.isStringEmptyOrNull(processDef)) {
      try {
        JSONObject j = JSONObject.fromObject(processDef);
        String name = j.getString("name");
        String description = j.getString("description");

        JSONArray keys = j.getJSONArray("keys");

        if (!LimsUtils.isStringEmptyOrNull(name) && !LimsUtils.isStringEmptyOrNull(description)) {
          WorkflowProcessDefinition wpd = null;

          if (j.has("id")) {
            wpd = requestManager.getWorkflowProcessDefinitionById(j.getLong("id"));
          }
          else {
            wpd = new WorkflowProcessDefinitionImpl();
            wpd.setName(name);
            wpd.setDescription(description);
            wpd.setCreationDate(new Date());
          }

          if (wpd != null) {
            if (j.has("inputType")) {
              wpd.setInputType((Class<? extends Nameable>) Class.forName(j.getString("inputType")));
            }

            if (j.has("outputType")) {
              wpd.setOutputType((Class<? extends Nameable>) Class.forName(j.getString("outputType")));
            }

            Set<String> stateKeys = new HashSet<>();
            for(int i = 0; i < keys.size(); i++){
              //stateKeys.add(requestManager.getStateKey(keys.getLong(i)));
              JSONObject p = keys.getJSONObject(i);
              if (!p.has("keyId") || !p.has("keyText")) {
                return JSONUtils.SimpleJSONError("Supplied WorkflowProcessDefinition state keys are invalid.");
              }
              stateKeys.add(p.getString("keyText"));
            }
            wpd.setStateFields(stateKeys);

            long wpdId = requestManager.saveWorkflowProcessDefinition(wpd);
            wpd.setId(wpdId);
          }
          else {
            return JSONUtils.SimpleJSONError("No valid WorkflowProcessDefinition found");
          }

          Map<String, Object> responseMap = new HashMap<>();
          JSONObject res = new JSONObject();
          res.put("id", wpd.getId());
          res.put("name", wpd.getName());
          res.put("description", wpd.getDescription());
          res.put("creationDate", LimsUtils.getDateAsString(wpd.getCreationDate()));
          res.put("inputType", wpd.getInputType().getName());
          res.put("outputType", wpd.getOutputType().getName());
          responseMap.put("definition", res);
          return JSONUtils.JSONObjectResponse(responseMap);
        }
        else {
          return JSONUtils.SimpleJSONError("A process definition needs a name and a description");
        }
      }
      catch (IOException e) {
        e.printStackTrace();
        log.error("Failed", e);
        return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
      }
      catch (ClassNotFoundException e) {
        e.printStackTrace();
        log.error("Failed", e);
        return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
      }
    }
    else {
      log.error("No process definition attributes specified to save");
      return JSONUtils.SimpleJSONError("No process definition attributes specified to save");
    }
  }

  public JSONObject addStateKey(HttpSession session, JSONObject json) {
    if (json.has("key")) {
      try {
        long keyId = requestManager.saveStateKey(json.getString("key"));
        return JSONUtils.SimpleJSONResponse("Key '"+json.getString("key")+"' saved successfully ["+keyId+"]");
      }
      catch (IOException e) {
        e.printStackTrace();
        log.error("Failed to save state key", e);
        return JSONUtils.SimpleJSONError("Failed to save state key: " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No state key specified to save");
    }
  }

  public JSONObject initiateWorkflow(HttpSession session, JSONObject json) {
    if (json.has("workflowDefinition")) {
      JSONObject def = json.getJSONObject("workflowDefinition");
      Long assigneeId = def.getLong("assignee");

      try {
        Workflow w;
        if (def.has("id")) {
          Long workflowId = def.getLong("id");
          w = requestManager.getWorkflowById(workflowId);

          JSONArray sa = def.getJSONArray("keys");
          for(int i = 0; i < sa.size(); i++){
            JSONObject p = sa.getJSONObject(i);
            if (p.has("key") && !LimsUtils.isStringEmptyOrNull(p.getString("key")) && !p.has("keyId")) {
              long keyId = requestManager.getIdForStateKey(p.getString("key"));
              if (keyId != 0L) p.put("keyId", keyId);
              //replace
              sa.set(i, p);
            }
          }
          JSONObject state = new JSONObject();
          state.put(state, sa);
          w.setState(state);
        }
        else {
          WorkflowDefinition wfd = requestManager.getWorkflowDefinitionById(def.getLong("workflowDefinitionId"));
          w = new WorkflowImpl(wfd);
          //w.setStartDate(new Date());
          w.setStatus(HealthType.Unknown);
          User user = securityManager.getUserById(assigneeId);
          if (user != null) {
            w.setAssignee(user);
          }
          else {
            return JSONUtils.SimpleJSONError("Cannot find user with ID '"+assigneeId+"' to act as assignee");
          }

          JSONArray sa = def.getJSONArray("keys");
          for(int i = 0; i < sa.size(); i++){
            JSONObject p = sa.getJSONObject(i);
            if (p.has("key") && !LimsUtils.isStringEmptyOrNull(p.getString("key")) && !p.has("keyId")) {
              long keyId = requestManager.getIdForStateKey(p.getString("key"));
              if (keyId != 0L) p.put("keyId", keyId);
              //replace
              sa.set(i, p);
            }
          }
          JSONObject state = new JSONObject();
          state.put("state", sa);
          w.setState(state);
        }

        if (def.has("alias")) {
          String alias = def.getString("alias");
          w.setAlias(alias);
        }

        requestManager.saveWorkflow(w);

        return JSONUtils.SimpleJSONResponse("Workflow started");
      }
      catch (IOException e) {
        e.printStackTrace();
        log.error("Failed to save workflow", e);
        return JSONUtils.SimpleJSONError("Failed to save workflow: " + e.getMessage());
      }
    }
    else {
      log.error("No workflow definition selected to start this workflow");
      return JSONUtils.SimpleJSONError("No workflow definition selected to start this workflow");
    }
  }

  public JSONObject updateWorkflow(HttpSession session, JSONObject json) {
    if (json.has("workflow")) {
      JSONObject def = json.getJSONObject("workflow");

      try {
        if (def.has("id")) {
          Long workflowId = def.getLong("id");
          Workflow w = requestManager.getWorkflowById(workflowId);

          if (def.has("alias")) {
            String alias = def.getString("alias");
            w.setAlias(alias);
          }

          if (def.has("status")) {
            w.setStatus(HealthType.valueOf(def.getString("status")));
          }

          if (def.has("assignee")) {
            Long assigneeId = def.getLong("assignee");
            User user = securityManager.getUserById(assigneeId);
            if (user != null) {
              w.setAssignee(user);
            }
            else {
              return JSONUtils.SimpleJSONError("Cannot find user with ID '"+assigneeId+"' to act as assignee");
            }
          }

          if (def.has("keys")) {
            JSONArray sa = def.getJSONArray("keys");
            for(int i = 0; i < sa.size(); i++){
              JSONObject p = sa.getJSONObject(i);
              if (p.has("key") && !LimsUtils.isStringEmptyOrNull(p.getString("key")) && !p.has("keyId")) {
                long keyId = requestManager.getIdForStateKey(p.getString("key"));
                if (keyId != 0L) p.put("keyId", keyId);
                //replace
                sa.set(i, p);
              }
            }
            JSONObject state = new JSONObject();
            state.put(state, sa);
            w.setState(state);
          }

          requestManager.saveWorkflow(w);
        }
        else {
          return JSONUtils.SimpleJSONError("Cannot update workflow. No workflow ID specified.");
        }
        return JSONUtils.SimpleJSONResponse("Workflow updated");
      }
      catch (IOException e) {
        e.printStackTrace();
        log.error("Failed to save workflow", e);
        return JSONUtils.SimpleJSONError("Failed to save workflow: " + e.getMessage());
      }
    }
    else {
      log.error("No workflow definition selected to start this workflow");
      return JSONUtils.SimpleJSONError("No workflow definition selected to start this workflow");
    }
  }
}
