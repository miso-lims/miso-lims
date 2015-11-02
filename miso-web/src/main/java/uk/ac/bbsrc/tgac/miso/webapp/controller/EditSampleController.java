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

import java.io.IOException;
import java.util.*;

import com.eaglegenomics.simlims.core.SecurityProfile;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;
import uk.ac.bbsrc.tgac.miso.webapp.context.ApplicationContextProvider;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;

@Controller
@RequestMapping("/sample")
@SessionAttributes("sample")
public class EditSampleController {
  protected static final Logger log = LoggerFactory.getLogger(EditSampleController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Autowired
  private JdbcTemplate interfaceTemplate;

  public void setInterfaceTemplate(JdbcTemplate interfaceTemplate) {
    this.interfaceTemplate = interfaceTemplate;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @ModelAttribute("metrixEnabled")
  public Boolean isMetrixEnabled() {
    MisoPropertyExporter exporter = (MisoPropertyExporter)ApplicationContextProvider.getApplicationContext().getBean("propertyConfigurer");
    Map<String, String> misoProperties = exporter.getResolvedProperties();
    return misoProperties.containsKey("miso.notification.interop.enabled") && Boolean.parseBoolean(misoProperties.get("miso.notification.interop.enabled"));
  }
  
  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    MisoPropertyExporter exporter = (MisoPropertyExporter)ApplicationContextProvider.getApplicationContext().getBean("propertyConfigurer");
    Map<String, String> misoProperties = exporter.getResolvedProperties();
    return misoProperties.containsKey("miso.autoGenerateIdentificationBarcodes") && Boolean.parseBoolean(misoProperties.get("miso.autoGenerateIdentificationBarcodes"));
  }

  public Map<String, Sample> getAdjacentSamplesInGroup(Sample s, @RequestParam(value = "entityGroupId", required = true) Long entityGroupId) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Project p = s.getProject();
    EntityGroup<? extends Nameable, Sample> sgroup = (EntityGroup<? extends Nameable, Sample>) requestManager.getEntityGroupById(entityGroupId);

    Sample prevS = null;
    Sample nextS = null;

    if (p != null) {
      if (!sgroup.getEntities().isEmpty()) {
        Map<String, Sample> ret = new HashMap<>();
        List<Sample> ss = new ArrayList<>(sgroup.getEntities());
        Collections.sort(ss);
        for (int i = 0; i < ss.size(); i++) {
          if (ss.get(i).equals(s)) {
            if (i != 0 && ss.get(i-1) != null) {
              prevS = ss.get(i-1);
            }

            if (i != ss.size()-1 && ss.get(i+1) != null) {
              nextS = ss.get(i+1);
            }
            break;
          }
        }
        ret.put("previousSample", prevS);
        ret.put("nextSample", nextS);
        return ret;
      }
    }
    return Collections.emptyMap();
  }

  public Map<String, Sample> getAdjacentSamplesInProject(Sample s, @RequestParam(value = "projectId", required = false) Long projectId) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Project p = s.getProject();
    Sample prevS = null;
    Sample nextS = null;

    if (p != null && p.getId() == projectId) {
      if (!p.getSamples().isEmpty()) {
        Map<String, Sample> ret = new HashMap<>();
        List<Sample> ss = new ArrayList<>(p.getSamples());
        Collections.sort(ss);
        for (int i = 0; i < ss.size(); i++) {
          if (ss.get(i).equals(s)) {
            if (i != 0 && ss.get(i-1) != null) {
              prevS = ss.get(i-1);
            }

            if (i != ss.size()-1 && ss.get(i+1) != null) {
              nextS = ss.get(i+1);
            }
            break;
          }
        }
        ret.put("previousSample", prevS);
        ret.put("nextSample", nextS);
        return ret;
      }
    }
    return Collections.emptyMap();
  }

  public Collection<Project> populateProjects(@RequestParam(value = "projectId", required = false) Long projectId) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (projectId != null) {
        Collection<Project> ps = new ArrayList<Project>();
        for (Project p : requestManager.listAllProjects()) {
          if (!p.getProjectId().equals(projectId)) {
            ps.add(p);
          }
        }
        return ps;
      }
      return requestManager.listAllProjects();
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list projects", ex);
      }
      throw ex;
    }
  }

  public Experiment populateExperiment(@RequestParam(value = "experimentId", required = false) Long experimentId) throws IOException {
    try {
      if (experimentId != null) {
        return requestManager.getExperimentById(experimentId);
      }
      else {
        return dataObjectFactory.getExperiment();
      }
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to get parent experiment", ex);
      }
      throw ex;
    }
  }

  private Set<Pool<? extends Poolable>> getPoolsBySample(Sample s) throws IOException {
    if (!s.getLibraries().isEmpty()) {
      Set<Pool<? extends Poolable>> pools = new TreeSet<>();
      for (Library l : s.getLibraries()) {
        List<Pool<? extends Poolable>> prs = new ArrayList<>(requestManager.listPoolsByLibraryId(l.getId()));
        pools.addAll(prs);
      }
      return pools;
    }
    return Collections.emptySet();
  }

  private Set<Run> getRunsBySamplePools(Set<Pool<? extends Poolable>> pools) throws IOException {
    if (!pools.isEmpty()) {
      Set<Run> runs = new TreeSet<>();
      for (Pool<? extends Poolable> pool : pools) {
        Collection<Run> prs = requestManager.listRunsByPoolId(pool.getId());
        runs.addAll(prs);
      }
      return runs;
    }
    return Collections.emptySet();
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return DbUtils.getColumnSizes(interfaceTemplate, "Sample");
  }

  @ModelAttribute("sampleTypesString")
  public String sampleTypesString() throws IOException {
    List<String> types = new ArrayList<String>();
    List<String> sampleTypes = new ArrayList<String>(requestManager.listAllSampleTypes());
    Collections.sort(sampleTypes);
    for (String s : sampleTypes) {
      types.add("\"" + s + "\"" + ":" + "\"" + s + "\"");
    }
    return LimsUtils.join(types, ",");
  }

  @ModelAttribute("sampleQCUnits")
  public String sampleQCUnits() throws IOException {
    return AbstractSampleQC.UNITS;
  }

  @ModelAttribute("libraryQcTypesString")
  public String libraryTypesString() throws IOException {
    List<String> types = new ArrayList<String>();
    List<QcType> libraryQcTypes = new ArrayList<QcType>(requestManager.listAllLibraryQcTypes());
    Collections.sort(libraryQcTypes);
    for (QcType s : libraryQcTypes) {
      types.add("\"" + s.getQcTypeId() + "\"" + ":" + "\"" + s.getName() + "\"");
    }
    return LimsUtils.join(types, ",");
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView newUnassignedSample(ModelMap model) throws IOException {
    return setupForm(AbstractSample.UNSAVED_ID, null, model);
  }

  @RequestMapping(value = "/new/{projectId}", method = RequestMethod.GET)
  public ModelAndView newAssignedSample(@PathVariable Long projectId,
                                        ModelMap model) throws IOException {
    return setupForm(AbstractSample.UNSAVED_ID, projectId, model);
  }

  @RequestMapping(value = "/rest/{sampleId}", method = RequestMethod.GET)
  public
  @ResponseBody
  Sample jsonRest(@PathVariable Long sampleId) throws IOException {
    return requestManager.getSampleById(sampleId);
  }

  @RequestMapping(value = "/{sampleId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long sampleId,
                                ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Sample sample = null;
      if (sampleId == AbstractSample.UNSAVED_ID) {
        sample = dataObjectFactory.getSample(user);
        model.put("title", "New Sample");
      }
      else {
        sample = requestManager.getSampleById(sampleId);
        model.put("title", "Sample " + sampleId);
      }

      if (sample == null) {
        throw new SecurityException("No such Sample.");
      }
      if (!sample.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      model.put("formObj", sample);
      model.put("sample", sample);
      model.put("sampleTypes", requestManager.listAllSampleTypes());
      model.put("accessibleProjects", populateProjects(null));
      Map<String, Sample> adjacentSamples = getAdjacentSamplesInProject(sample, sample.getProject().getProjectId());
      if (!adjacentSamples.isEmpty()) {
        model.put("previousSample", adjacentSamples.get("previousSample"));
        model.put("nextSample", adjacentSamples.get("nextSample"));
      }

      Set<Pool<? extends Poolable>> pools = getPoolsBySample(sample);
      Map<Long, Sample> poolSampleMap = new HashMap<>();
      for (Pool pool : pools) {
        poolSampleMap.put(pool.getId(), sample);
      }
      model.put("poolSampleMap", poolSampleMap);
      model.put("samplePools", pools);
      model.put("sampleRuns", getRunsBySamplePools(pools));


      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, sample, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, sample, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, sample, securityManager.listAllGroups()));

      return new ModelAndView("/pages/editSample.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show sample", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/{sampleId}/project/{projectId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long sampleId,
                                @PathVariable Long projectId,
                                ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Sample sample = null;
      if (sampleId == AbstractSample.UNSAVED_ID) {
        sample = dataObjectFactory.getSample(user);
        model.put("title", "New Sample");

        if (projectId != null) {
          Project project = requestManager.getProjectById(projectId);
          model.addAttribute("project", project);
          sample.setProject(project);

          if (Arrays.asList(user.getRoles()).contains("ROLE_TECH")) {
            SecurityProfile sp = new SecurityProfile(user);
            LimsUtils.inheritUsersAndGroups(sample, project.getSecurityProfile());
            sp.setOwner(user);
            sample.setSecurityProfile(sp);
          }
          else {
            sample.inheritPermissions(project);
          }
        }
        else {
          model.put("accessibleProjects", populateProjects(null));
        }
      }
      else {
        sample = requestManager.getSampleById(sampleId);
        model.put("title", "Sample " + sampleId);

        if (projectId != null) {
          Project project = requestManager.getProjectById(projectId);
          model.addAttribute("project", project);
          sample.setProject(project);
          sample.inheritPermissions(project);

          Map<String, Sample> adjacentSamples = getAdjacentSamplesInProject(sample, sample.getProject().getProjectId());
          if (!adjacentSamples.isEmpty()) {
            model.put("previousSample", adjacentSamples.get("previousSample"));
            model.put("nextSample", adjacentSamples.get("nextSample"));
          }
        }
        else {
          model.put("accessibleProjects", populateProjects(null));
        }
      }

      if (sample != null && !sample.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }

      model.put("formObj", sample);
      model.put("sample", sample);
      model.put("sampleTypes", requestManager.listAllSampleTypes());

      Set<Pool<? extends Poolable>> pools = getPoolsBySample(sample);
      Map<Long, Sample> poolSampleMap = new HashMap<>();
      for (Pool pool : pools) {
        poolSampleMap.put(pool.getId(), sample);
      }
      model.put("poolSampleMap", poolSampleMap);
      model.put("samplePools", pools);
      model.put("sampleRuns", getRunsBySamplePools(pools));

      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, sample, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, sample, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, sample, securityManager.listAllGroups()));

      return new ModelAndView("/pages/editSample.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show sample", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/bulk/dummy", method = RequestMethod.POST)
  public String processSubmit() {
    return null;
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("sample") Sample sample,
                              ModelMap model,
                              SessionStatus session) throws IOException, MalformedSampleException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!sample.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }

      requestManager.saveSample(sample);
      session.setComplete();
      model.clear();
      return "redirect:/miso/sample/" + sample.getId();
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save sample", ex);
      }
      throw ex;
    }
  }
}
