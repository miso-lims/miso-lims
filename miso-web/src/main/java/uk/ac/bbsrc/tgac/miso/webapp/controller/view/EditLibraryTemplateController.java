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

package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryTemplateService;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.dto.DetailedLibraryTemplateDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTemplateDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTemplateIndexDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.util.*;

@Controller
@RequestMapping("/librarytemplate")
public class EditLibraryTemplateController {

  @Autowired
  private LibraryTemplateService libraryTemplateService;
  @Autowired
  private ProjectService projectService;
  @Autowired
  private ObjectMapper mapper;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  public Boolean isDetailedSampleEnabled() {
    return detailedSample;
  }

  private final class BulkCreateLibraryTemplateBackend extends BulkCreateTableBackend<LibraryTemplateDto> {

    public BulkCreateLibraryTemplateBackend(LibraryTemplateDto dto, Integer quantity, ObjectMapper mapper) {
      super("librarytemplate", LibraryTemplateDto.class, "Library Templates", dto, quantity, mapper);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      // No config required
    }
  }

  private final class BulkEditLibraryTemplateBackend extends BulkEditTableBackend<LibraryTemplate, LibraryTemplateDto> {

    public BulkEditLibraryTemplateBackend(ObjectMapper mapper) {
      super("librarytemplate", LibraryTemplateDto.class, "Library Templates", mapper);
    }

    @Override
    protected Stream<LibraryTemplate> load(List<Long> ids) throws IOException {
      return libraryTemplateService.listByIdList(ids).stream();
    }

    @Override
    protected LibraryTemplateDto asDto(LibraryTemplate model) {
      return Dtos.asDto(model);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      // No config required
    }
  };

  @GetMapping(value = "/bulk/new")
  public ModelAndView receiveBulkLibraries(@RequestParam("quantity") Integer quantity,
      @RequestParam(value = "projectId", required = false) Long projectId,
      ModelMap model) throws IOException {

    LibraryTemplateDto dto = (isDetailedSampleEnabled() ? new DetailedLibraryTemplateDto() : new LibraryTemplateDto());
    if (projectId != null) {
      List<Long> projectIds = new ArrayList<>();
      projectIds.add(projectId);
      dto.setProjectIds(projectIds);
    }

    return new BulkCreateLibraryTemplateBackend(dto, quantity, mapper).create(model);
  }

  @PostMapping("/bulk/edit")
  public ModelAndView edit(@RequestParam Map<String, String> formData, ModelMap model) throws IOException {
    String libraryTemplateIds = MisoWebUtils.getStringInput("ids", formData, true);
    return new BulkEditLibraryTemplateBackend(mapper).edit(libraryTemplateIds, model);
  }

  @GetMapping("/new")
  public ModelAndView create(@RequestParam(name = "projectId", required = false) Long projectId, ModelMap model)
      throws IOException {
    LibraryTemplate template = isDetailedSampleEnabled() ? new DetailedLibraryTemplate() : new LibraryTemplate();
    if (projectId != null) {
      Project project = projectService.get(projectId);
      if (project == null) {
        throw new ClientErrorException("No project found with ID: " + projectId);
      }
      template.getProjects().add(project);
    }
    model.put(PageMode.PROPERTY, PageMode.CREATE.getLabel());
    model.put("title", "New Library Template");
    return libraryTemplatePage(template, model);
  }

  @GetMapping("/{templateId}")
  public ModelAndView edit(@PathVariable long templateId, ModelMap model) throws IOException {
    LibraryTemplate template = getTemplate(templateId);
    model.put(PageMode.PROPERTY, PageMode.EDIT.getLabel());
    model.put("title", "Library Template " + templateId);
    return libraryTemplatePage(template, model);
  }

  private ModelAndView libraryTemplatePage(LibraryTemplate template, ModelMap model) throws JsonProcessingException {
    LibraryTemplateDto dto = Dtos.asDto(template);
    model.put("template", template);
    model.put("templateDto", mapper.writeValueAsString(dto));
    model.put("templateProjects", mapper.writeValueAsString(template.getProjects().stream().map(Dtos::asDto).collect(Collectors.toList())));
    return new ModelAndView("/WEB-INF/pages/editLibraryTemplate.jsp", model);
  }

  private static class BulkCreateTemplateIndicesBackend extends BulkCreateTableBackend<LibraryTemplateIndexDto> {

    private final LibraryTemplate libraryTemplate;

    public BulkCreateTemplateIndicesBackend(LibraryTemplate libraryTemplate, Integer quantity, ObjectMapper mapper) {
      super("librarytemplate_index", LibraryTemplateIndexDto.class, "Library Template Indices",
          new LibraryTemplateIndexDto(), quantity, mapper);
      this.libraryTemplate = libraryTemplate;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.set("libraryTemplate", mapper.valueToTree(Dtos.asDto(libraryTemplate)));
      config.set("indexFamily", mapper.valueToTree(Dtos.asDto(libraryTemplate.getIndexFamily())));
    }
  }

  @GetMapping("/{templateId}/indices/add")
  public ModelAndView addIndices(@PathVariable("templateId") long templateId, @RequestParam("quantity") int quantity, ModelMap model)
      throws IOException {
    LibraryTemplate template = getTemplateWithIndexFamily(templateId);
    return new BulkCreateTemplateIndicesBackend(template, quantity, mapper).create(model);
  }

  private static class BulkEditTemplateIndicesBackend extends BulkTableBackend<LibraryTemplateIndexDto> {

    private final LibraryTemplate libraryTemplate;

    public BulkEditTemplateIndicesBackend(LibraryTemplate libraryTemplate, ObjectMapper mapper) {
      super("librarytemplate_index", LibraryTemplateIndexDto.class, mapper);
      this.libraryTemplate = libraryTemplate;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.set("libraryTemplate", mapper.valueToTree(Dtos.asDto(libraryTemplate)));
      config.set("indexFamily", mapper.valueToTree(Dtos.asDto(libraryTemplate.getIndexFamily())));
    }

    public ModelAndView edit(String positionsString, ModelMap model) throws IOException {
      String[] positions = positionsString.split(",");
      List<LibraryTemplateIndexDto> dtos = Arrays.stream(positions)
          .map(position -> new LibraryTemplateIndexDto(position, libraryTemplate.getIndexOnes().get(position),
              libraryTemplate.getIndexTwos().get(position)))
          .collect(Collectors.toList());
      return prepare(model, PageMode.EDIT, "Edit Library Template Indices", dtos);
    }
  }

  @GetMapping("/{templateId}/indices/edit")
  public ModelAndView editIndices(@PathVariable("templateId") long templateId, @RequestParam("positions") String positions, ModelMap model)
      throws IOException {
    LibraryTemplate template = getTemplateWithIndexFamily(templateId);
    return new BulkEditTemplateIndicesBackend(template, mapper).edit(positions, model);
  }

  private LibraryTemplate getTemplate(long templateId) throws IOException {
    LibraryTemplate template = libraryTemplateService.get(templateId);
    if (template == null) {
      throw new NotFoundException("No library template found for ID: " + templateId);
    }
    return template;
  }

  private LibraryTemplate getTemplateWithIndexFamily(long templateId) throws IOException {
    LibraryTemplate template = getTemplate(templateId);
    if (template.getIndexFamily() == null) {
      throw new ClientErrorException("This library template does not specify an index family");
    }
    return template;
  }

}
