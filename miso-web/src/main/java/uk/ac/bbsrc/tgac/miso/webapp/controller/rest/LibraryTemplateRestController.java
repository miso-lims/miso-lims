package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryTemplateService;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTemplateDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTemplateIndexDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/librarytemplates")
public class LibraryTemplateRestController extends RestController {

  private static final String TYPE_LABEL = "Library Template";

  @Autowired
  private LibraryTemplateService libraryTemplateService;
  @Autowired
  private ProjectService projectService;
  @Autowired
  private AdvancedSearchParser advancedSearchParser;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  private final JQueryDataTableBackend<LibraryTemplate, LibraryTemplateDto> jQueryBackend =
      new JQueryDataTableBackend<LibraryTemplate, LibraryTemplateDto>() {

        @Override
        protected LibraryTemplateDto asDto(LibraryTemplate model) {
          return Dtos.asDto(model);
        }

        @Override
        protected PaginatedDataSource<LibraryTemplate> getSource() throws IOException {
          return libraryTemplateService;
        }

      };

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkCreateAsync(@RequestBody List<LibraryTemplateDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate(TYPE_LABEL, dtos, Dtos::to, libraryTemplateService);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkUpdateAsync(@RequestBody List<LibraryTemplateDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkUpdate(TYPE_LABEL, dtos, Dtos::to, libraryTemplateService);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, LibraryTemplate.class, libraryTemplateService, Dtos::asDto);
  }

  @PostMapping(produces = "application/json")
  @ResponseBody
  public LibraryTemplateDto create(@RequestBody LibraryTemplateDto libraryTemplate, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    return RestUtils.createObject(TYPE_LABEL, libraryTemplate, Dtos::to, libraryTemplateService, Dtos::asDto);
  }

  @PutMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public LibraryTemplateDto update(@PathVariable("id") Long id, @RequestBody LibraryTemplateDto libraryTemplateDto)
      throws IOException {
    return RestUtils.updateObject(TYPE_LABEL, id, libraryTemplateDto, Dtos::to, libraryTemplateService, Dtos::asDto);
  }

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryTemplateDto> getLibraryTemplates(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

  @GetMapping(value = "/dt/project/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryTemplateDto> getDTLibraryTemplatesByProject(@PathVariable("id") Long id,
      HttpServletRequest request)
      throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.project(id));
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody List<Long> ids) throws IOException {
    RestUtils.bulkDelete(TYPE_LABEL, ids, libraryTemplateService);
  }

  @PostMapping(value = "/project/add")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkAddProject(@RequestParam("projectId") Long projectId, @RequestBody List<Long> templateIds)
      throws IOException {
    List<LibraryTemplate> templates =
        templateIds.stream().map(WhineyFunction.rethrow(id -> libraryTemplateService.get(id)))
            .collect(Collectors.toList());
    templates.forEach(template -> {
      try {
        addProject(template, projectId);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private void addProject(LibraryTemplate template, Long projectId) throws IOException {
    template.getProjects().add(projectService.get(projectId));
    libraryTemplateService.update(template);
  }

  @PostMapping(value = "/project/remove")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkRemoveProject(@RequestParam("projectId") Long projectId, @RequestBody List<Long> templateIds)
      throws IOException {
    List<LibraryTemplate> templates =
        templateIds.stream().map(WhineyFunction.rethrow(id -> libraryTemplateService.get(id)))
            .collect(Collectors.toList());
    templates.forEach(template -> {
      try {
        removeProject(template, projectId);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private void removeProject(LibraryTemplate template, Long projectId) throws IOException {
    template.getProjects().removeIf(project -> projectId.equals(project.getId()));
    libraryTemplateService.update(template);
  }

  @PostMapping("/{templateId}/indices")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode addIndices(@PathVariable long templateId,
      @RequestBody List<LibraryTemplateIndexDto> dtos) throws IOException {
    List<LibraryTemplateDto> templateDtos = makeTemplateDtosForIndexUpdate(templateId, dtos);
    return asyncOperationManager.startAsyncBulkUpdate(TYPE_LABEL, templateDtos, Dtos::to, libraryTemplateService);
  }

  @PutMapping("/{templateId}/indices")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode updateIndices(@PathVariable long templateId,
      @RequestBody List<LibraryTemplateIndexDto> dtos) throws IOException {
    List<LibraryTemplateDto> templateDtos = makeTemplateDtosForIndexUpdate(templateId, dtos);
    return asyncOperationManager.startAsyncBulkUpdate(TYPE_LABEL, templateDtos, Dtos::to, libraryTemplateService);
  }

  private List<LibraryTemplateDto> makeTemplateDtosForIndexUpdate(long templateId, List<LibraryTemplateIndexDto> dtos)
      throws IOException {
    LibraryTemplate template = RestUtils.retrieve(TYPE_LABEL, templateId, libraryTemplateService);
    LibraryTemplateDto templateDto = Dtos.asDto(template);
    for (LibraryTemplateIndexDto index : dtos) {
      setIndex(index.getBoxPosition(), index.getIndex1Id(), templateDto.getIndexOneIds());
      setIndex(index.getBoxPosition(), index.getIndex2Id(), templateDto.getIndexTwoIds());
    }

    return Collections.singletonList(templateDto);
  }

  private void setIndex(String boxPosition, Long indexId, Map<String, Long> indices) {
    if (indexId == null) {
      indices.remove(boxPosition);
    } else {
      indices.put(boxPosition, indexId);
    }
  }

}
