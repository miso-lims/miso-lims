package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.service.DeliverableService;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.context.ExternalUriBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/project")
public class EditProjectController {

  @Autowired
  private ProjectService projectService;
  @Autowired(required = false)
  private IssueTrackerManager issueTrackerManager;
  @Autowired
  private ExternalUriBuilder externalUriBuilder;
  @Autowired
  private SubprojectService subprojectService;
  @Autowired
  private NamingSchemeHolder namingSchemeHolder;
  @Autowired
  private ObjectMapper mapper;
  @Autowired
  private DeliverableService deliverableService;

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  @GetMapping("/new")
  public ModelAndView setupForm(ModelMap model) throws IOException {
    model.put("title", "New Project");
    return setupForm(new ProjectImpl(), model);
  }

  @GetMapping("/shortname/{shortName}")
  public ModelAndView byProjectShortName(@PathVariable String shortName, ModelMap model) throws IOException {
    model.clear();
    return new ModelAndView("redirect:/project/code/%s".formatted(shortName), model);
  }

  @GetMapping("/code/{code}")
  public ModelAndView byProjectCode(@PathVariable String code, ModelMap model) throws IOException {
    Project project = projectService.getProjectByCode(code);
    if (project == null) {
      throw new NotFoundException("No project found for code " + code);
    }
    return setupForm(project, model);
  }

  @GetMapping("/{projectId}")
  public ModelAndView setupForm(@PathVariable Long projectId, ModelMap model) throws IOException {
    Project project = projectService.get(projectId);
    if (project == null) {
      throw new NotFoundException("No project found for ID " + projectId.toString());
    }
    return setupForm(project, model);
  }

  private ModelAndView setupForm(Project project, ModelMap model) throws IOException {
    if (project.isSaved()) {
      Collection<Subproject> subprojects = subprojectService.listByProjectId(project.getId());
      model.put("subprojects", Dtos.asSubprojectDtos(subprojects));
      model.put("title", "Project " + project.getId());
      MisoWebUtils.addIssues(issueTrackerManager, () -> issueTrackerManager.getIssuesByTag(project.getCode()), model);
      model.put("projectReportLinks", externalUriBuilder.getUris(project));
    }

    model.put("project", project);
    model.put("projectDto", mapper.writeValueAsString(Dtos.asDto(project, true)));

    ObjectNode formConfig = mapper.createObjectNode();
    MisoWebUtils.addJsonArray(mapper, formConfig, "statusOptions", Arrays.asList(StatusType.values()),
        StatusType::getKey);
    MisoWebUtils.addJsonArray(mapper, formConfig, "deliverables", deliverableService.list(), Dtos::asDto);

    ObjectNode namingConfig = formConfig.putObject("naming");
    addNamingSchemeConfig(namingConfig, "primary", namingSchemeHolder.getPrimary(), project);
    addNamingSchemeConfig(namingConfig, "secondary", namingSchemeHolder.getSecondary(), project);
    model.put("formConfig", mapper.writeValueAsString(formConfig));

    return new ModelAndView("/WEB-INF/pages/editProject.jsp", model);
  }

  private void addNamingSchemeConfig(ObjectNode namingConfig, String property, NamingScheme scheme, Project project)
      throws IOException {
    if (scheme != null) {
      ObjectNode config = namingConfig.putObject(property);
      config.put("codeRequired", !scheme.nullProjectCodeAllowed());
      config.put("codeModifiable", scheme.nullProjectCodeAllowed() || !projectService.hasSamples(project));
    }
  }

}
