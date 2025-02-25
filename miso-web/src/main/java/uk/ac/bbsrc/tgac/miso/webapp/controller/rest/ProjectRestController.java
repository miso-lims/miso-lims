package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.AttachmentDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ProjectDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

/**
 * A controller to handle all REST requests for Projects
 * 
 * @author Rob Davey
 * @date 01-Sep-2011
 * @since 0.1.0
 */
@Controller
@RequestMapping("/rest/projects")
public class ProjectRestController extends AbstractRestController {

  @Autowired
  private ProjectService projectService;
  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  private final JQueryDataTableBackend<Project, ProjectDto> jQueryBackend =
      new JQueryDataTableBackend<Project, ProjectDto>() {

        @Override
        protected ProjectDto asDto(Project model) {
          return Dtos.asDto(model);
        }

        @Override
        protected PaginatedDataSource<Project> getSource() throws IOException {
          return projectService;
        }

      };

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  @GetMapping(value = "/{projectId}", produces = "application/json")
  public @ResponseBody ProjectDto getProjectById(@PathVariable long projectId) throws IOException {
    return RestUtils.getObject("Project", projectId, projectService, Dtos::asDto);
  }

  @GetMapping(value = "/search")
  @ResponseBody
  public List<ProjectDto> getProjectsBySearch(@RequestParam("q") String query) throws IOException {
    return projectService.list(0, 0, false, "id", PaginationFilter.query(query))
        .stream()
        .map(Dtos::asDto)
        .toList();
  }

  @GetMapping(value = "/{projectId}/files")
  public @ResponseBody List<AttachmentDto> getAttachments(
      @PathVariable(name = "projectId", required = true) long projectId)
      throws IOException {
    Project project = projectService.get(projectId);
    if (project == null) {
      throw new RestException("Project not found", Status.NOT_FOUND);
    }
    return project.getAttachments().stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  @PostMapping
  public @ResponseBody ProjectDto create(@RequestBody ProjectDto dto) throws IOException {
    return RestUtils.createObject("Project", dto, Dtos::to, projectService, project -> Dtos.asDto(project, true));
  }

  @PutMapping("/{projectId}")
  public @ResponseBody ProjectDto update(@PathVariable long projectId, @RequestBody ProjectDto dto) throws IOException {
    return RestUtils.updateObject("Project", projectId, dto, Dtos::to, projectService,
        project -> Dtos.asDto(project, true));
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Project", ids, projectService);
  }

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<ProjectDto> getLibraryAliquots(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

}
