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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.NameComparator;
import uk.ac.bbsrc.tgac.miso.core.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcess;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.*;

import java.io.IOException;
import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 *
 * @author Rob Davey
 * @date 14/05/14
 * @since 0.2.1-SNAPSHOT
 */
@Controller
@RequestMapping("/workflow")
public class EditWorkflowController {
  protected static final Logger log = LoggerFactory.getLogger(EditSampleController.class);

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Autowired
  private uk.ac.bbsrc.tgac.miso.core.manager.RequestManager requestManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @ModelAttribute("users")
  public Collection<User> populateUsers() throws IOException {
    try {
      return LimsSecurityUtils.getInternalUsers(securityManager.listAllUsers());
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list users", ex);
      }
      throw ex;
    }
  }

  @ModelAttribute("definitionTypes")
  public Collection<Class<? extends Nameable>> populateDefinitionTypes() throws IOException {
    //collate types that can be used as input to a workflow process
    Reflections reflections = new Reflections("uk.ac.bbsrc.tgac.miso.core.data", new FilterBuilder().excludePackage("uk.ac.bbsrc.tgac.miso.core.data.impl").excludePackage("uk.ac.bbsrc.tgac.miso.core.workflow.impl").exclude(".*Abstract.*"));
    List<Class<? extends Nameable>> classes = new ArrayList<>(reflections.getSubTypesOf(Nameable.class));
    try {
      Collections.sort(classes, new NameComparator(Class.class));
      return classes;
    }
    catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return classes;
  }

  @ModelAttribute("workflowDefinitions")
  public Collection<WorkflowDefinition> populateWorkflowDefinitions() throws IOException {
    return requestManager.listAllWorkflowDefinitions();
  }

  @ModelAttribute("workflowProcessDefinitions")
  public Collection<WorkflowProcessDefinition> populateWorkflowProcessDefinitions() throws IOException {
    return requestManager.listAllWorkflowProcessDefinitions();
  }

  @ModelAttribute("healthTypes")
  public Collection<String> populateHealthTypes() {
    return HealthType.getKeys();
  }

//management
  @RequestMapping(value = "/manage", method = RequestMethod.GET)
  public ModelAndView manageWorkflows(ModelMap model) throws IOException {
    model.put("workflowProcessDefinitions", requestManager.listAllWorkflowProcessDefinitions());
    model.put("workflowDefinitions", requestManager.listAllWorkflowDefinitions());
    model.put("stateKeys", requestManager.listAllStateKeys());
    return new ModelAndView("/pages/manageWorkflows.jsp", model);
  }

//workflow process definitions
  @RequestMapping(value = "/process/definition/new", method = RequestMethod.GET)
  public ModelAndView newWorkflowProcessDefinition(ModelMap model) throws IOException {
    return setupWorkflowProcessDefinitionForm(WorkflowProcessDefinitionImpl.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/process/definition/{workflowProcessDefinitionId}", method = RequestMethod.GET)
  public ModelAndView setupWorkflowProcessDefinitionForm(@PathVariable Long workflowProcessDefinitionId, ModelMap model) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    WorkflowProcessDefinition workflowProcessDefinition = null;
    SortedMap<Long, String> keyMap = new TreeMap<>();
    if (workflowProcessDefinitionId == WorkflowImpl.UNSAVED_ID) {
      workflowProcessDefinition = new WorkflowProcessDefinitionImpl();
      model.put("title", "New Workflow Process Definition");
    }
    else {
      workflowProcessDefinition = requestManager.getWorkflowProcessDefinitionById(workflowProcessDefinitionId);

      for (String key : workflowProcessDefinition.getStateFields()) {
        long keyId = requestManager.getIdForStateKey(key);
        if (keyId != 0L) keyMap.put(keyId, key);
      }

      model.put("title", "Workflow Process Definition" + workflowProcessDefinitionId);
    }
    if (workflowProcessDefinition == null) {
      throw new SecurityException("No such Workflow Process Definition");
    }
    model.put("formObj", workflowProcessDefinition);
    model.put("workflowProcessDefinition", workflowProcessDefinition);
    model.put("stateKeyMap", keyMap);

    return new ModelAndView("/pages/editWorkflowProcessDefinition.jsp", model);
  }

//workflow definitions
  @RequestMapping(value = "/definition/new", method = RequestMethod.GET)
  public ModelAndView newWorkflowDefinition(ModelMap model) throws IOException {
    return setupWorkflowDefinitionForm(AbstractWorkflowDefinition.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/definition/{workflowDefinitionId}", method = RequestMethod.GET)
  public ModelAndView setupWorkflowDefinitionForm(@PathVariable Long workflowDefinitionId, ModelMap model) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    WorkflowDefinition workflowDefinition = null;
    SortedMap<Long, String> keyMap = new TreeMap<>();
    if (workflowDefinitionId == WorkflowImpl.UNSAVED_ID) {
      workflowDefinition = new WorkflowDefinitionImpl(new TreeMap<Integer, WorkflowProcessDefinition>());
      model.put("title", "New Workflow Definition");
    }
    else {
      workflowDefinition = requestManager.getWorkflowDefinitionById(workflowDefinitionId);

      for (String key : workflowDefinition.getStateFields()) {
        long keyId = requestManager.getIdForStateKey(key);
        if (keyId != 0L) keyMap.put(keyId, key);
      }

      model.put("title", "Workflow Definition" + workflowDefinitionId);
    }
    if (workflowDefinition == null) {
      throw new SecurityException("No such Workflow Definition");
    }
    model.put("formObj", workflowDefinition);
    model.put("workflowDefinition", workflowDefinition);
    model.put("stateKeyMap", keyMap);

    return new ModelAndView("/pages/editWorkflowDefinition.jsp", model);
  }

  @RequestMapping(value = "/{workflowId}", method = RequestMethod.GET)
  public ModelAndView setupWorkflowForm(@PathVariable Long workflowId, ModelMap model) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Workflow workflow = null;
    SortedMap<Long, String> keyMap = new TreeMap<>();
    SortedMap<Long, String> valueMap = new TreeMap<>();
    Map<Long, Long> pairMap = new HashMap<>();

    if (workflowId == WorkflowImpl.UNSAVED_ID) {
      throw new IOException("No such Workflow");
    }
    else {
      workflow = requestManager.getWorkflowById(workflowId);
      model.put("title", "Workflow " + workflowId);

      JSONObject state = workflow.getState();
      if (state.has("state")) {
        JSONArray a = state.getJSONArray("state");
        for (JSONObject j : (Iterable<JSONObject>) a) {
          keyMap.put(j.getLong("keyId"), j.getString("key"));
          valueMap.put(j.getLong("valueId"), j.getString("value"));
          pairMap.put(j.getLong("keyId"), j.getLong("valueId"));
        }
      }
      else {
        throw new IOException("No valid state detected for workflow: " + workflow.getId());
      }
    }
    if (workflow == null) {
      throw new SecurityException("No such Workflow");
    }
    model.put("formObj", workflow);
    model.put("workflow", workflow);
    model.put("keyMap", keyMap);
    model.put("valueMap", valueMap);
    model.put("pairMap", pairMap);

    return new ModelAndView("/pages/editWorkflow.jsp", model);
  }

  @RequestMapping(value = "/start/{workflowDefinitionId}", method = RequestMethod.GET)
  public ModelAndView startWorkflowForm(@PathVariable Long workflowDefinitionId, ModelMap model) throws IOException {
    WorkflowDefinition workflowDefinition = requestManager.getWorkflowDefinitionById(workflowDefinitionId);
    Workflow workflow = new WorkflowImpl(workflowDefinition);
    model.put("title", "Start Workflow");

    SortedMap<String, String> keyMap = new TreeMap<>();
    for (String key : workflowDefinition.getStateFields()) {
      long keyId = requestManager.getIdForStateKey(key);
      if (keyId != 0L) keyMap.put(key, "");
    }

    model.put("formObj", workflow);
    model.put("workflow", workflow);
    model.put("stateKeyMap", keyMap);
    model.put("start", true);

    return new ModelAndView("/pages/editWorkflow.jsp", model);
  }

  @RequestMapping(value = "/process/{workflowProcessId}", method = RequestMethod.GET)
  public ModelAndView setupWorkflowProcessForm(@PathVariable Long workflowProcessId, ModelMap model) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    WorkflowProcess workflowProcess = null;
    SortedMap<Long, String> keyMap = new TreeMap<>();
    SortedMap<Long, String> valueMap = new TreeMap<>();
    Map<Long, Long> pairMap = new HashMap<>();

    if (workflowProcessId == WorkflowProcessImpl.UNSAVED_ID) {
      throw new IOException("No such WorkflowProcess");
    }
    else {
      workflowProcess = requestManager.getWorkflowProcessById(workflowProcessId);
      model.put("title", "Workflow Process" + workflowProcessId);

      JSONObject state = workflowProcess.getState();
      if (state.has("state")) {
        JSONArray a = state.getJSONArray("state");
        for (JSONObject j : (Iterable<JSONObject>) a) {
          keyMap.put(j.getLong("keyId"), j.getString("key"));
          valueMap.put(j.getLong("valueId"), j.getString("value"));
          pairMap.put(j.getLong("keyId"), j.getLong("valueId"));
        }
      }
      else {
        throw new IOException("No valid state detected for workflow process: " + workflowProcess.getId());
      }
    }
    if (workflowProcess == null) {
      throw new SecurityException("No such Workflow Process");
    }
    model.put("formObj", workflowProcess);
    model.put("workflowProcess", workflowProcess);
    model.put("keyMap", keyMap);
    model.put("valueMap", valueMap);
    model.put("pairMap", pairMap);

    return new ModelAndView("/pages/editWorkflowProcess.jsp", model);
  }
}
