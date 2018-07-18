/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity.DonorSex;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleBuilder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;
import uk.ac.bbsrc.tgac.miso.core.data.type.StrStatus;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DetailedSampleDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ProjectDto;
import uk.ac.bbsrc.tgac.miso.dto.RunDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleIdentityDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleLCMTubeDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleSlideDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleStockDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleTissueDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleTissueProcessingDto;
import uk.ac.bbsrc.tgac.miso.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.service.ArrayService;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.service.LabService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.service.RunService;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.service.SamplePurposeService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.service.StainService;
import uk.ac.bbsrc.tgac.miso.service.TissueMaterialService;
import uk.ac.bbsrc.tgac.miso.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.service.TissueTypeService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkCreateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkPropagateTableBackend;


@Controller
@RequestMapping("/sample")
@SessionAttributes("sample")
public class EditSampleController {

  private static final Logger log = LoggerFactory.getLogger(EditSampleController.class);

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private NamingScheme namingScheme;

  @Autowired
  private SampleService sampleService;
  @Autowired
  private SampleValidRelationshipService sampleValidRelationshipService;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private StainService stainService;
  @Autowired
  private ArrayService arrayService;
  @Autowired
  private ArrayRunService arrayRunService;
  @Autowired
  private AuthorizationManager authorizationManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setSampleValidRelationshipService(SampleValidRelationshipService sampleValidRelationshipService) {
    this.sampleValidRelationshipService = sampleValidRelationshipService;
  }

  public void setChangeLogService(ChangeLogService changeLogService) {
    this.changeLogService = changeLogService;
  }

  public void setPoolService(PoolService poolService) {
    this.poolService = poolService;
  }

  public void setSampleClassService(SampleClassService sampleClassService) {
    this.sampleClassService = sampleClassService;
  }

  public void setTissueOriginService(TissueOriginService tissueOriginService) {
    this.tissueOriginService = tissueOriginService;
  }

  public void setTissueTypeService(TissueTypeService tissueTypeService) {
    this.tissueTypeService = tissueTypeService;
  }

  public void setDetailedQcStatusService(DetailedQcStatusService detailedQcStatusService) {
    this.detailedQcStatusService = detailedQcStatusService;
  }

  public void setLabService(LabService labService) {
    this.labService = labService;
  }

  public void setSamplePurposeService(SamplePurposeService samplePurposeService) {
    this.samplePurposeService = samplePurposeService;
  }

  public void setTissueMaterialService(TissueMaterialService tissueMaterialService) {
    this.tissueMaterialService = tissueMaterialService;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public RunService getRunService() {
    return runService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  @ModelAttribute("aliasGenerationEnabled")
  public Boolean isAliasGenerationEnabled() {
    return namingScheme != null && namingScheme.hasSampleAliasGenerator();
  }

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;
  @Value("${miso.defaults.sample.bulk.scientificname:}")
  private String defaultSciName;

  private Boolean isDetailedSampleEnabled() {
    return detailedSample;
  }

  @ModelAttribute("defaultSciName")
  public String getDefaultSciName() {
    return defaultSciName != null ? defaultSciName : "";
  }

  private static class Config {
    private static final String CREATE = "create";
    private static final String PROPAGATE = "propagate";
    private static final String EDIT = "edit";
    private static final String HAS_PROJECT = "hasProject";
    private static final String DNASE_TREATABLE = "dnaseTreatable";
    private static final String DEFAULT_SCI_NAME = "defaultSciName";
    private static final String SOURCE_SAMPLE_CLASS = "sourceSampleClass";
    private static final String TARGET_SAMPLE_CLASS = "targetSampleClass";
  }

  @ModelAttribute("stains")
  public List<Stain> populateStains() {
    return stainService.list();
  }

  @ModelAttribute("sampleConcentrationUnits")
  public String sampleConcentrationUnits() {
    return Sample.CONCENTRATION_UNITS;
  }

  public Map<String, Sample> getAdjacentSamplesInProject(Sample s, @RequestParam(value = "projectId", required = false) Long projectId)
      throws IOException {
    Project p = s.getProject();
    Sample prevS = null;
    Sample nextS = null;

    if (p != null && p.getId() == projectId) {
      if (p.getSamples().isEmpty()) {
        // if p was lazy loaded then it doesn't have samples.
        p = projectService.getProjectById(p.getId());
      }
      if (!p.getSamples().isEmpty()) {
        Map<String, Sample> ret = new HashMap<>();
        List<Sample> ss = new ArrayList<>(p.getSamples());
        Collections.sort(ss, (a, b) -> Long.compare(a.getId(), b.getId()));
        for (int i = 0; i < ss.size(); i++) {
          if (ss.get(i).getId() == s.getId()) {
            if (i > 0) {
              prevS = ss.get(i - 1);
            }
            if (i < ss.size() - 2) {
              nextS = ss.get(i + 1);
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

  private Collection<Project> populateProjects() throws IOException {
    try {
      List<Project> ps = new ArrayList<>(projectService.listAllProjects());

      if (isDetailedSampleEnabled()) {
        Collections.sort(ps, (a, b) -> a.getShortName().compareTo(b.getShortName()));
      } else {
        Collections.sort(ps, (a, b) -> a.getAlias().compareTo(b.getAlias()));
      }
      return ps;
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list projects", ex);
      }
      throw ex;
    }
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return sampleService.getSampleColumnSizes();
  }

  @ModelAttribute("poolConcentrationUnits")
  public String poolConcentrationUnits() {
    return PoolImpl.CONCENTRATION_UNITS;
  }

  @Autowired
  private SampleClassService sampleClassService;

  private static final Comparator<SampleClass> SAMPLECLASS_CATEGORY_ALIAS = (SampleClass o1, SampleClass o2) -> {
    int categoryOrder = SampleClass.CATEGORIES.indexOf(o1.getSampleCategory()) - SampleClass.CATEGORIES.indexOf(o2.getSampleCategory());
    if (categoryOrder != 0) return categoryOrder;
    return o1.getAlias().compareTo(o2.getAlias());
  };

  private void populateSampleClasses(ModelMap model) throws IOException {
    List<SampleClass> sampleClasses = new ArrayList<>(sampleClassService.getAll());
    List<SampleClass> tissueClasses = sampleClasses.stream()
        .filter(sc -> SampleTissue.CATEGORY_NAME.equals(sc.getSampleCategory()))
        .collect(Collectors.toList());
    Collections.sort(sampleClasses, SAMPLECLASS_CATEGORY_ALIAS);
    Collections.sort(tissueClasses, SAMPLECLASS_CATEGORY_ALIAS);
    model.put("sampleClasses", sampleClasses);
    model.put("tissueClasses", tissueClasses);
  }

  @Autowired
  private TissueOriginService tissueOriginService;

  @ModelAttribute("tissueOrigins")
  public List<TissueOrigin> getTissueOrigins() throws IOException {
    List<TissueOrigin> list = new ArrayList<>(tissueOriginService.getAll());
    Collections.sort(list, new Comparator<TissueOrigin>() {
      @Override
      public int compare(TissueOrigin o1, TissueOrigin o2) {
        return o1.getAlias().compareTo(o2.getAlias());
      }
    });
    return list;
  }

  @Autowired
  private TissueTypeService tissueTypeService;

  @ModelAttribute("tissueTypes")
  public List<TissueType> getTissueTypes() throws IOException {
    List<TissueType> list = new ArrayList<>(tissueTypeService.getAll());
    Collections.sort(list, new Comparator<TissueType>() {
      @Override
      public int compare(TissueType o1, TissueType o2) {
        // reverse comparison as most frequently used tissue types are at bottom of alphabet
        return o2.getAlias().compareTo(o1.getAlias());
      }
    });
    return list;
  }

  @Autowired
  private DetailedQcStatusService detailedQcStatusService;

  @ModelAttribute("detailedQcStatuses")
  public List<DetailedQcStatus> getDetailedQcStatuses() throws IOException {
    List<DetailedQcStatus> list = new ArrayList<>(detailedQcStatusService.getAll());
    Collections.sort(list, new Comparator<DetailedQcStatus>() {
      @Override
      public int compare(DetailedQcStatus o1, DetailedQcStatus o2) {
        if (o1.getStatus() == null) {
          return (o2.getStatus() == null ? 0 : -1);
        } else if (o2.getStatus() == null) {
          return 1;
        } else {
          return o1.getStatus().compareTo(o2.getStatus());
        }
      }
    });
    return list;
  }

  @Autowired
  private LabService labService;

  @ModelAttribute("labs")
  public List<Lab> getLabs() throws IOException {
    List<Lab> list = new ArrayList<>(labService.getAll());
    Collections.sort(list, new Comparator<Lab>() {
      @Override
      public int compare(Lab o1, Lab o2) {
        return o1.getAlias().compareTo(o2.getAlias());
      }
    });
    return list;
  }

  @Autowired
  private SamplePurposeService samplePurposeService;

  @ModelAttribute("samplePurposes")
  public List<SamplePurpose> getSamplePurposes() throws IOException {
    List<SamplePurpose> list = new ArrayList<>(samplePurposeService.getAll());
    Collections.sort(list, new Comparator<SamplePurpose>() {
      @Override
      public int compare(SamplePurpose o1, SamplePurpose o2) {
        return o1.getAlias().compareTo(o2.getAlias());
      }
    });
    return list;
  }

  @Autowired
  private TissueMaterialService tissueMaterialService;

  @ModelAttribute("tissueMaterials")
  public List<TissueMaterial> getTissueMaterials() throws IOException {
    List<TissueMaterial> list = new ArrayList<>(tissueMaterialService.getAll());
    Collections.sort(list, new Comparator<TissueMaterial>() {
      @Override
      public int compare(TissueMaterial o1, TissueMaterial o2) {
        return o1.getAlias().compareTo(o2.getAlias());
      }
    });
    return list;
  }

  @ModelAttribute("strStatusOptions")
  public StrStatus[] getStrStatusOptions() {
    return StrStatus.values();
  }

  @ModelAttribute("consentLevelOptions")
  public ConsentLevel[] getConsentLevelOptions() {
    return ConsentLevel.values();
  }

  @ModelAttribute("donorSexOptions")
  public DonorSex[] getDonorSexOptions() {
    return DonorSex.values();
  }

  /**
   * Translates foreign keys to entity objects with only the ID set, to be used in service layer to reload persisted child objects
   *
   * @param binder
   */
  @InitBinder
  public void includeForeignKeys(WebDataBinder binder) {
    binder.registerCustomEditor(Project.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        Project p = new ProjectImpl();
        p.setId(Long.valueOf(text));
        setValue(p);
      }
    });

    binder.registerCustomEditor(SampleClass.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        if (isStringEmptyOrNull(text)) {
          setValue(null);
        } else {
          SampleClass sc = new SampleClassImpl();
          sc.setId(Long.valueOf(text));
          setValue(sc);
        }
      }
    });

    binder.registerCustomEditor(TissueOrigin.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        TissueOrigin to = new TissueOriginImpl();
        to.setId(Long.valueOf(text));
        setValue(to);
      }
    });

    binder.registerCustomEditor(TissueType.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        TissueType tt = new TissueTypeImpl();
        tt.setId(Long.valueOf(text));
        setValue(tt);
      }
    });

    binder.registerCustomEditor(DetailedQcStatus.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        if (isStringEmptyOrNull(text)) {
          setValue(null);
        } else {
          DetailedQcStatus qcpd = new DetailedQcStatusImpl();
          qcpd.setId(Long.valueOf(text));
          setValue(qcpd);
        }
      }
    });

    binder.registerCustomEditor(Subproject.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        if (isStringEmptyOrNull(text)) {
          setValue(null);
        } else {
          Subproject sp = new SubprojectImpl();
          sp.setId(Long.valueOf(text));
          setValue(sp);
        }
      }
    });

    binder.registerCustomEditor(Lab.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        if (isStringEmptyOrNull(text)) {
          setValue(null);
        } else {
          Lab lab = new LabImpl();
          lab.setId(Long.valueOf(text));
          setValue(lab);
        }
      }
    });

    binder.registerCustomEditor(SamplePurpose.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        if (isStringEmptyOrNull(text)) {
          setValue(null);
        } else {
          SamplePurpose sp = new SamplePurposeImpl();
          sp.setId(Long.valueOf(text));
          setValue(sp);
        }
      }
    });

    binder.registerCustomEditor(TissueMaterial.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        if (isStringEmptyOrNull(text)) {
          setValue(null);
        } else {
          TissueMaterial tm = new TissueMaterialImpl();
          tm.setId(Long.valueOf(text));
          setValue(tm);
        }
      }
    });

    binder.registerCustomEditor(Long.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        setValue(isStringEmptyOrNull(text) ? null : Long.valueOf(text));
      }
    });
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView newUnassignedSample(ModelMap model) throws IOException {
    return setupForm(AbstractSample.UNSAVED_ID, null, model);
  }

  @RequestMapping(value = "/new/{projectId}", method = RequestMethod.GET)
  public ModelAndView newAssignedSample(@PathVariable Long projectId, ModelMap model) throws IOException {
    return setupForm(AbstractSample.UNSAVED_ID, projectId, model);
  }

  @RequestMapping(value = "/rest/{sampleId}", method = RequestMethod.GET)
  public @ResponseBody Sample jsonRest(@PathVariable Long sampleId) throws IOException {
    return sampleService.get(sampleId);
  }

  @RequestMapping(value = "/{sampleId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long sampleId, ModelMap model) throws IOException {
    return setupForm(sampleId, null, model);
  }

  @RequestMapping(value = "/{sampleId}/project/{projectId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long sampleId, @PathVariable Long projectId, ModelMap model) throws IOException {
    try {
      User user = authorizationManager.getCurrentUser();
      Sample sample = null;
      if (sampleId == AbstractSample.UNSAVED_ID) {
        sample = detailedSample ? new DetailedSampleBuilder(user) : new SampleImpl(user);
        model.put("sampleCategory", "new");
        model.put("title", "New Sample");

        if (projectId != null) {
          Project project = projectService.getProjectById(projectId);
          if (project == null) throw new NotFoundException("No project found for ID " + projectId.toString());
          model.addAttribute("project", project);
          sample.setProject(project);

          if (Arrays.asList(user.getRoles()).contains("ROLE_TECH")) {
            SecurityProfile sp = new SecurityProfile(user);
            LimsUtils.inheritUsersAndGroups(sample, project.getSecurityProfile());
            sp.setOwner(user);
            sample.setSecurityProfile(sp);
          } else {
            sample.inheritPermissions(project);
          }
        } else {
          model.put("accessibleProjects", populateProjects());
        }
        List<ProjectDto> projects = new ArrayList<>();
        for (Project p : projectService.listAllProjects()) {
          projects.add(Dtos.asDto(p));
        }
        model.put("projectsDtos", mapper.valueToTree(projects));
      } else {
        sample = sampleService.get(sampleId);
        if (sample == null) throw new NotFoundException("No sample found for ID " + sampleId.toString());
        model.put("sampleCategory", detailedSample ? ((DetailedSample) sample).getSampleClass().getSampleCategory() : "plain");
        if (detailedSample) {
          model.put("sampleClass", ((DetailedSample) sample).getSampleClass().getAlias());
          Optional<DetailedSample> effective = ((DetailedSample) sample).getEffectiveGroupIdSample();
          if (effective.isPresent()) {
            model.put("effectiveGroupId", effective.get().getGroupId());
            model.put("effectiveGroupIdSample", effective.get().getAlias());
          }
        }
        model.put("title", "Sample " + sampleId);

        if (projectId != null && projectId.longValue() != sample.getProject().getId()) {
          throw new IllegalArgumentException("Requested project does not match sample project");
        }

        Map<String, Sample> adjacentSamples = getAdjacentSamplesInProject(sample, sample.getProject().getId());
        if (!adjacentSamples.isEmpty()) {
          model.put("previousSample", adjacentSamples.get("previousSample"));
          model.put("nextSample", adjacentSamples.get("nextSample"));
        }

        model.put("projectsDtos", "[]");

        model.put("sampleLibraries", sample.getLibraries().stream().map(Dtos::asDto).collect(Collectors.toList()));
        Set<Pool> pools = sample.getLibraries().stream()
            .flatMap(WhineyFunction.flatRethrow(library -> poolService.listByLibraryId(library.getId())))
            .distinct().collect(Collectors.toSet());
        List<RunDto> runDtos = pools.stream().flatMap(WhineyFunction.flatRethrow(pool -> runService.listByPoolId(pool.getId())))
            .map(Dtos::asDto)
            .collect(Collectors.toList());
        model.put("samplePools", pools.stream().map(p -> Dtos.asDto(p, false)).collect(Collectors.toList()));
        model.put("sampleRuns", runDtos);
        model.put("sampleRelations", getRelations(sample));
        addArrayData(sampleId, model);
      }

      model.put("formObj", sample);
      model.put("sample", sample);
      Collection<String> sampleTypes = sampleService.listSampleTypes();
      if (!sampleTypes.contains(sample.getSampleType())) {
        sampleTypes.add(sample.getSampleType());
      }
      model.put("sampleTypes", sampleTypes);
      if (LimsUtils.isDetailedSample(sample) && LimsUtils.getIdentityConsentLevel((DetailedSample) sample) == ConsentLevel.REVOKED) {
        model.put("warning", "Donor has revoked consent");
      }

      Collection<User> allUsers = securityManager.listAllUsers();
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, sample, allUsers));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, sample, allUsers));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, sample, securityManager.listAllGroups()));
      populateSampleClasses(model);

      return new ModelAndView("/pages/editSample.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show sample", ex);
      }
      throw ex;
    }
  }

  private void addArrayData(long sampleId, ModelMap model) throws IOException {
    model.put("sampleArrays", arrayService.listBySampleId(sampleId).stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList()));
    model.put("sampleArrayRuns", arrayRunService.listBySampleId(sampleId).stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList()));
  }

  private List<SampleDto> getRelations(Sample sample) {
    List<SampleDto> relations = new ArrayList<>();
    if (LimsUtils.isDetailedSample(sample)) {
      DetailedSample detailed = (DetailedSample) sample;
      for (DetailedSample parent = detailed.getParent(); parent != null; parent = parent.getParent()) {
        relations.add(0, Dtos.asDto(LimsUtils.deproxify(parent)));
      }
      addChildren(relations, detailed.getChildren());
    }
    return relations;
  }

  private void addChildren(List<SampleDto> relations, Collection<DetailedSample> children) {
    for (DetailedSample child : children) {
      relations.add(Dtos.asDto(LimsUtils.deproxify(child)));
      addChildren(relations, child.getChildren());
    }
  }

  @RequestMapping(value = "/rest/changes", method = RequestMethod.GET)
  public @ResponseBody Collection<ChangeLog> jsonRestChanges() throws IOException {
    return changeLogService.listAll("Sample");
  }

  /**
   * Used to edit samples with ids from given {sampleIds}.
   * Sends Dtos objects which will then be used for editing in grid.
   */
  @RequestMapping(value = "/bulk/edit", method = RequestMethod.GET)
  public ModelAndView editBulkSamples(@RequestParam("ids") String sampleIds, ModelMap model) throws IOException {
    return new BulkEditSampleBackend().edit(sampleIds, model);
  }

  /**
   * Used to create propagate new samples from existing samples (Detailed Sample only).
   * 
   * Sends Dtos objects which will then be used for editing in grid.
   */
  @RequestMapping(value = "/bulk/propagate", method = RequestMethod.GET)
  public ModelAndView propagateBulkSamples(@RequestParam("parentIds") String parentIds, @RequestParam("sampleClassId") Long sampleClassId,
      @RequestParam("replicates") int replicates,
      ModelMap model) throws IOException {
    BulkPropagateSampleBackend bulkPropagateSampleBackend = new BulkPropagateSampleBackend(sampleClassService.get(sampleClassId));
    return bulkPropagateSampleBackend.propagate(parentIds, replicates, model);
  }

  /**
   * Used to create new samples.
   * <ul>
   * <li>Detailed Sample: create new samples of a given sample class. Root identities will be found or created.</li>
   * <li>Plain Sample: create new samples.</li>
   * </ul>
   * Sends Dtos objects which will then be used for editing in grid.
   */
  @RequestMapping(value = "/bulk/new", method = RequestMethod.GET)
  public ModelAndView createBulkSamples(@RequestParam("quantity") Integer quantity,
      @RequestParam(value = "sampleClassId", required = false) Long sampleClassId,
      @RequestParam(value = "projectId", required = false) Long projectId, ModelMap model) throws IOException {
    if (quantity == null || quantity <= 0) throw new RestException("Must specify quantity of samples to create", Status.BAD_REQUEST);

    final SampleDto template;
    final SampleClass target;

    if (sampleClassId != null) {
      // create new detailed samples
      target = sampleClassService.get(sampleClassId);
      if (target == null || target.getSampleCategory() == null) {
        throw new RestException("Cannot find sample class with ID " + sampleClassId, Status.NOT_FOUND);
      }
      template = getCorrectDetailedSampleDto(target, sampleClassId);
    } else {
      if (detailedSample) throw new RestException("Must specify sample class of samples to create", Status.BAD_REQUEST);
      template = new SampleDto();
      target = null;
    }
    final Project project;
    if (projectId == null) {
      project = null;
    } else {
      project = projectService.getProjectById(projectId);
      template.setProjectId(projectId);
    }

    return new BulkCreateSampleBackend(template.getClass(), template, quantity, project, target).create(model);
  }

  private DetailedSampleDto getCorrectDetailedSampleDto(SampleClass target, Long sampleClassId) {
    // need to instantiate the correct DetailedSampleDto class to get the correct fields
    final DetailedSampleDto detailedTemplate;
    switch (target.getSampleCategory()) {
    case SampleIdentity.CATEGORY_NAME:
      detailedTemplate = new SampleIdentityDto();
      break;
    case SampleTissue.CATEGORY_NAME:
      detailedTemplate = new SampleTissueDto();
      break;
    case SampleTissueProcessing.CATEGORY_NAME:
      if (SampleSlide.SAMPLE_CLASS_NAME.equals(target.getAlias())) {
        detailedTemplate = new SampleSlideDto();
      } else if (SampleLCMTube.SAMPLE_CLASS_NAME.equals(target.getAlias())) {
        detailedTemplate = new SampleLCMTubeDto();
      } else {
        detailedTemplate = new SampleTissueProcessingDto();
      }
      break;
    case SampleStock.CATEGORY_NAME:
      detailedTemplate = new SampleStockDto();
      break;
    case SampleAliquot.CATEGORY_NAME:
      detailedTemplate = new SampleAliquotDto();
      break;
    default:
      throw new RestException("Unknown category for sample class with ID " + sampleClassId, Status.BAD_REQUEST);
    }
    detailedTemplate.setSampleClassId(sampleClassId);
    return detailedTemplate;
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("sample") Sample sample, ModelMap model, SessionStatus session)
      throws IOException {
    if (sample instanceof DetailedSampleBuilder) {
      DetailedSampleBuilder builder = (DetailedSampleBuilder) sample;
      builder.setSampleClass(sampleClassService.get(builder.getSampleClass().getId()));
      if (builder.getTissueClass() != null) {
        builder.setTissueClass(sampleClassService.get(builder.getTissueClass().getId()));
      }
      if (builder.getParent() == null && builder.getSampleClass().getSampleCategory().equals(SampleAliquot.CATEGORY_NAME)) {
        builder.setStockClass(sampleClassService.inferParentFromChild(builder.getSampleClass().getId(), SampleAliquot.CATEGORY_NAME,
            SampleStock.CATEGORY_NAME));
        if (builder.getStockClass() == null) {
          throw new IllegalStateException(String.format("%s class with id %d has no %s parents", SampleAliquot.CATEGORY_NAME,
              builder.getSampleClass().getId(), SampleStock.CATEGORY_NAME));
        }
      }
      sample = builder.build();
    }
    try {
      if (sample.getId() == Sample.UNSAVED_ID) {
        sampleService.create(sample);
      } else {
        sampleService.update(sample);
      }
      session.setComplete();
      model.clear();
      return "redirect:/miso/sample/" + sample.getId();
    } catch (IOException ex) {
      log.debug("Failed to save sample", ex);
      throw ex;
    }
  }

  private final class BulkEditSampleBackend extends BulkEditTableBackend<Sample, SampleDto> {
    private SampleClass sampleClass = null;

    private BulkEditSampleBackend() {
      super("sample", SampleDto.class, "Samples");
    }

    @Override
    protected SampleDto asDto(Sample model) {
      return Dtos.asDto(model);
    }

    @Override
    protected Stream<Sample> load(List<Long> modelIds) throws IOException {
      List<Sample> results = (List<Sample>) sampleService.listByIdList(modelIds);
      for (Sample sample : results) {
        if (isDetailedSampleEnabled()) {
          if (sampleClass == null) {
            sampleClass = ((DetailedSample) sample).getSampleClass();
          } else if (((DetailedSample) sample).getSampleClass().getId() != sampleClass.getId()) {
            throw new IOException("Can only bulk edit samples when samples all have the same class.");
          }
        }
      }
      return results.stream().sorted(new AliasComparator<>());
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      if (sampleClass != null) {
        config.putPOJO(Config.TARGET_SAMPLE_CLASS, Dtos.asDto(sampleClass));
        config.putPOJO(Config.SOURCE_SAMPLE_CLASS, Dtos.asDto(sampleClass));
        config.put(Config.DNASE_TREATABLE, sampleClass.getDNAseTreatable());
      } else {
        config.put(Config.DNASE_TREATABLE, false);
      }
      config.put(Config.PROPAGATE, false);
      config.put(Config.EDIT, true);
    }
  };

  private final class BulkPropagateSampleBackend extends BulkPropagateTableBackend<Sample, SampleDto> {
    private SampleClass sourceSampleClass;
    private final SampleClass targetSampleClass;

    private BulkPropagateSampleBackend(SampleClass targetSampleClass) {
      super("sample", SampleDto.class, "Samples", "Samples");
      this.targetSampleClass = targetSampleClass;
    }

    @Override
    protected SampleDto createDtoFromParent(Sample item) {
      DetailedSampleDto dto;
      if (LimsUtils.isDetailedSample(item)) {
        DetailedSample sample = (DetailedSample) item;
        if (targetSampleClass == null) {
          throw new IllegalArgumentException("Target sample class not set!");
        } else {
          switch (targetSampleClass.getSampleCategory()) {
          case SampleTissue.CATEGORY_NAME:
            dto = new SampleTissueDto();
            break;
          case SampleTissueProcessing.CATEGORY_NAME:
            if (targetSampleClass.getAlias().equals("Slide")) {
              dto = new SampleSlideDto();
            } else if (targetSampleClass.getAlias().equals("LCM Tube")) {
              dto = new SampleLCMTubeDto();
            } else {
              dto = new SampleTissueProcessingDto();
            }
            break;
          case SampleStock.CATEGORY_NAME:
            dto = new SampleStockDto();
            break;
          case SampleAliquot.CATEGORY_NAME:
            dto = new SampleAliquotDto();
            break;
          default:
            throw new IllegalArgumentException("Cannot determine sample category");
          }
        }
        dto.setScientificName(sample.getScientificName());
        dto.setSampleType(sample.getSampleType());
        dto.setParentId(sample.getId());
        dto.setParentAlias(sample.getAlias());
        dto.setParentTissueSampleClassId(sample.getSampleClass().getId());
        dto.setProjectId(sample.getProject().getId());
        if (sample.getSubproject() != null) dto.setSubprojectId(sample.getSubproject().getId());
        dto.setGroupId(sample.getGroupId());
        dto.setGroupDescription(sample.getGroupDescription());

        dto.setSampleClassId(targetSampleClass.getId());
        sourceSampleClass = sample.getSampleClass();
      } else {
        throw new IllegalArgumentException("Cannot create plain samples from other plain samples!");
      }
      return dto;
    }

    @Override
    protected Stream<Sample> loadParents(List<Long> parentIds) throws IOException {
      return sampleService.listByIdList(parentIds).stream().sorted(new AliasComparator<>());
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.put(Config.PROPAGATE, true);
      config.put(Config.EDIT, false);
      config.put(Config.DNASE_TREATABLE, targetSampleClass.getDNAseTreatable());
      config.putPOJO(Config.TARGET_SAMPLE_CLASS, Dtos.asDto(targetSampleClass));
      config.putPOJO(Config.SOURCE_SAMPLE_CLASS, Dtos.asDto(sourceSampleClass));
    }
  };

  private final class BulkCreateSampleBackend extends BulkCreateTableBackend<SampleDto> {
    private final SampleClass targetSampleClass;
    private final Project project;

    public BulkCreateSampleBackend(Class<? extends SampleDto> dtoClass, SampleDto dto, Integer quantity, Project project,
        SampleClass sampleClass) {
      super("sample", dtoClass, "Samples", dto, quantity);
      targetSampleClass = sampleClass;
      this.project = project;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      if (targetSampleClass != null) {
        config.putPOJO(Config.TARGET_SAMPLE_CLASS, Dtos.asDto(targetSampleClass));
        config.put(Config.DNASE_TREATABLE, targetSampleClass.hasPathToDnaseTreatable(sampleValidRelationshipService.getAll()));
      } else {
        config.put(Config.DNASE_TREATABLE, false);
      }
      config.put(Config.CREATE, true);
      config.put(Config.HAS_PROJECT, project != null);
      if (project == null) {
        projectService.listAllProjects().stream().map(Dtos::asDto).forEach(config.putArray("projects")::addPOJO);
      } else {
        config.putPOJO("project", Dtos.asDto(project));
      }
      config.put(Config.DEFAULT_SCI_NAME, defaultSciName);
    }
  };

}
