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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.json.JsonSanitizer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.NumberUtils;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.HierarchicalEntityGroupImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultMisoEntityPrefix;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.workflow.Workflow;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Xingdong Bian
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class EntityGroupControllerHelperService {

  protected static final Logger log = LoggerFactory.getLogger(EntityGroupControllerHelperService.class);
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

  public JSONObject checkEntityGroup(HttpSession session, JSONObject json) {
    return JSONUtils.SimpleJSONResponse("OK");
  }

  public JSONObject deleteEntityGroup(HttpSession session, JSONObject json) {
    return JSONUtils.SimpleJSONResponse("OK");
  }

  public JSONObject filterGroupElements(HttpSession session, JSONObject json) {
    return JSONUtils.SimpleJSONResponse("OK");
  }

  public JSONObject sampleSearch(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    StringBuilder b = new StringBuilder();
    Collection<Sample> samples;
    if (searchStr != null && !searchStr.equals("")) {
      if (LimsUtils.isBase64String(searchStr)) {
        //Base64-encoded string, most likely a barcode image beeped in. decode and search
        searchStr = new String(Base64.decodeBase64(searchStr));
      }
    }
    try {
      if (json.has("projectId")) {
        samples = new ArrayList<>();
        Long projectId = json.getLong("projectId");
        Project project = requestManager.getProjectById(projectId);
        Collection<Sample> pSamples = project.getSamples();
        for (Sample s : pSamples) {
          if (s.getIdentificationBarcode().contains(searchStr) || s.getName().contains(searchStr) || s.getAlias().contains(searchStr) || s.getDescription().contains(searchStr) || s.getScientificName().contains(searchStr)) {
            samples.add(s);
          }
        }
      }
      else {
        samples = requestManager.listAllSamplesBySearch(searchStr);
      }

      if (samples.size() > 0) {
        List<Sample> rSamples = new ArrayList<>(samples);
        Collections.reverse(rSamples);
        for (Sample sample : rSamples) {
          b.append(sampleHtml(sample));
        }
      }
      else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed");
    }
  }

  public JSONObject removeEntityFromGroup(HttpSession session, JSONObject json) {
    return JSONUtils.SimpleJSONResponse("OK");
  }

  private String sampleHtml(Sample s) {
    StringBuilder b = new StringBuilder();
    b.append("<a class='list-group-item' href='javascript:void(0)'");
    if (LimsUtils.isStringEmptyOrNull(s.getAlias())) {
      b.append("<div><b>" + s.getName() + " : "+s.getSampleType()+"</b><br/>");
    }
    else {
      b.append("<div><b>" + s.getName() + " (" + s.getAlias() + ") : "+s.getSampleType()+"</b><br/>");
    }
    b.append("<div>Project: "+s.getProject().getAlias()+"</div>");
    b.append("<input type='hidden' id='entity" + s.getId() + "' value='" + s.getName() + "' name='entities'/></div>");
    b.append("</a>");
    return b.toString();
  }

  public JSONObject listSampleGroupDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();

      for (HierarchicalEntityGroup<Workflow, Sample> group : requestManager.<Workflow, Sample>listAllEntityGroupsByEntityType(Workflow.class, Sample.class)) {
        String parent = "Unassigned";
        if (group.getParent() != null) {
          if (!LimsUtils.isStringEmptyOrNull(group.getParent().getAlias())) {
            parent = group.getParent().getAlias() + "("+group.getParent().getWorkflowDefinition().getName()+")";
          }
          else {
            parent = group.getParent().getWorkflowDefinition().getName();
          }
        }

        jsonArray.add(JsonSanitizer.sanitize("[\"" + group.getCreationDate() + "\",\"" +
                                             group.getCreator().getFullName() + "\",\"" +
                                             group.getAssignee().getFullName() + "\",\"" +
                                             group.getEntities().size() + "\",\"" +
                                             parent + "\",\"" +
                                             "<a href=\"/miso/samplegroup/" + group.getId() + "\"><span class=\"fa fa-pencil-square-o fa-lg\"></span></a>" + "\"]"));
      }
      j.put("array", jsonArray);
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject saveEntityGroup(HttpSession session, JSONObject json) {
    if (json.has("entityGroup")) {
      JSONObject eg = json.getJSONObject("entityGroup");

      try {
        HierarchicalEntityGroup<Nameable, Nameable> heg;

        if (eg.has("id") && !LimsUtils.isStringEmptyOrNull(String.valueOf(eg.getLong("id")))) {
          Long entityGroupId = eg.getLong("id");

          //generics fun
          heg = (HierarchicalEntityGroup<Nameable, Nameable>)requestManager.getEntityGroupById(entityGroupId);
        }
        else {
          heg = new HierarchicalEntityGroupImpl<>();
        }

        //TODO fire event
        Long assigneeId = eg.getLong("assignee");
        User user = securityManager.getUserById(assigneeId);
        heg.setAssignee(user);

        Set<Nameable> entities = new HashSet<>();
        JSONArray sa = eg.getJSONArray("entities");
        for(int i = 0; i < sa.size(); i++) {
          JSONObject p = sa.getJSONObject(i);
          if (p.has("entityId") && !LimsUtils.isStringEmptyOrNull(p.getString("entityId"))) {
            long entityId = p.getLong("entityId");
            String entityName = p.getString("entityName");
            entities.add(DefaultMisoEntityPrefix.<Nameable>getMisoObjectByName(requestManager, entityName, entityId));
          }
        }
        heg.setEntities(entities);

        if (eg.has("workflowId") && !LimsUtils.isStringEmptyOrNull(String.valueOf(eg.getLong("workflowId")))) {
          Workflow wf = requestManager.getWorkflowById(eg.getLong("workflowId"));
          if (wf != null) {
            wf.attach(heg);
            requestManager.saveWorkflow(wf);
          }
        }

        requestManager.saveEntityGroup(heg);

        return JSONUtils.SimpleJSONResponse("Entity group saved");
      }
      catch (IOException e) {
        e.printStackTrace();
        log.error("Failed to save entity group", e);
        return JSONUtils.SimpleJSONError("Failed to save entity group: " + e.getMessage());
      }
      catch (MisoNamingException e) {
        e.printStackTrace();
        log.error("Failed to save entity group", e);
        return JSONUtils.SimpleJSONError("Failed to save entity group: " + e.getMessage());
      }
    }
    else {
      log.error("No entity group specified to save");
      return JSONUtils.SimpleJSONError("No entity group specified to save");
    }
  }
}
