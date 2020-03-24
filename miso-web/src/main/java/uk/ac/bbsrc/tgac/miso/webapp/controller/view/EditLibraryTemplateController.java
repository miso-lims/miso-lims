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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryTemplateService;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.dto.DetailedLibraryTemplateDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTemplateDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkCreateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkTableBackend;

@Controller
@RequestMapping("/librarytemplate")
public class EditLibraryTemplateController {

  @Autowired
  private LibraryTemplateService libraryTemplateService;
  @Autowired
  private ProjectService projectService;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  public Boolean isDetailedSampleEnabled() {
    return detailedSample;
  }

  private final class BulkCreateLibraryTemplateBackend extends BulkCreateTableBackend<LibraryTemplateDto> {

    public BulkCreateLibraryTemplateBackend(LibraryTemplateDto dto, Integer quantity) {
      super("libraryTemplate", LibraryTemplateDto.class, "Library Templates", dto, quantity);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      // No config required
    }
  }

  private final class BulkEditLibraryTemplateBackend extends BulkEditTableBackend<LibraryTemplate, LibraryTemplateDto> {

    public BulkEditLibraryTemplateBackend() {
      super("libraryTemplate", LibraryTemplateDto.class, "Library Templates");
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

    return new BulkCreateLibraryTemplateBackend(dto, quantity).create(model);
  }

  @GetMapping(value = "/bulk/edit")
  public ModelAndView editBulkLibraryTemplates(@RequestParam("ids") String libraryTemplateIds, ModelMap model) throws IOException {
    return new BulkEditLibraryTemplateBackend().edit(libraryTemplateIds, model);
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
    model.put("pageMode", "create");
    model.put("title", "New Library Template");
    return libraryTemplatePage(template, model);
  }

  @GetMapping("/{templateId}")
  public ModelAndView edit(@PathVariable long templateId, ModelMap model) throws IOException {
    LibraryTemplate template = getTemplate(templateId);
    model.put("pageMode", "edit");
    model.put("title", "Library Template " + templateId);
    return libraryTemplatePage(template, model);
  }

  private ModelAndView libraryTemplatePage(LibraryTemplate template, ModelMap model) throws JsonProcessingException {
    LibraryTemplateDto dto = Dtos.asDto(template);
    ObjectMapper mapper = new ObjectMapper();
    model.put("template", template);
    model.put("templateDto", mapper.writeValueAsString(dto));
    model.put("templateProjects", mapper.writeValueAsString(template.getProjects().stream().map(Dtos::asDto).collect(Collectors.toList())));
    return new ModelAndView("/WEB-INF/pages/editLibraryTemplate.jsp", model);
  }

  public static class LibraryTemplateIndexDto {

    private String boxPosition;
    private Long index1Id;
    private Long index2Id;

    public LibraryTemplateIndexDto() {
      // Default constructor
    }

    public LibraryTemplateIndexDto(String boxPosition, Index index1, Index index2) {
      this.boxPosition = boxPosition;
      if (index1 != null) {
        this.index1Id = index1.getId();
      }
      if (index2 != null) {
        this.index2Id = index2.getId();
      }
    }

    public String getBoxPosition() {
      return boxPosition;
    }

    public void setBoxPosition(String boxPosition) {
      this.boxPosition = boxPosition;
    }

    public Long getIndex1Id() {
      return index1Id;
    }

    public void setIndex1Id(Long index1Id) {
      this.index1Id = index1Id;
    }

    public Long getIndex2Id() {
      return index2Id;
    }

    public void setIndex2Id(Long index2Id) {
      this.index2Id = index2Id;
    }

  }

  private static class BulkCreateTemplateIndicesBackend extends BulkCreateTableBackend<LibraryTemplateIndexDto> {

    private final LibraryTemplate libraryTemplate;

    public BulkCreateTemplateIndicesBackend(LibraryTemplate libraryTemplate, Integer quantity) {
      super("libraryTemplate_index", LibraryTemplateIndexDto.class, "Library Template Indices", new LibraryTemplateIndexDto(), quantity);
      this.libraryTemplate = libraryTemplate;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("pageMode", "create");
      config.set("libraryTemplate", mapper.valueToTree(Dtos.asDto(libraryTemplate)));
      config.set("indexFamily", mapper.valueToTree(Dtos.asDto(libraryTemplate.getIndexFamily())));
    }

  }

  @GetMapping("/{templateId}/indices/add")
  public ModelAndView addIndices(@PathVariable("templateId") long templateId, @RequestParam("quantity") int quantity, ModelMap model)
      throws IOException {
    LibraryTemplate template = getTemplateWithIndexFamily(templateId);
    return new BulkCreateTemplateIndicesBackend(template, quantity).create(model);
  }

  private static class BulkEditTemplateIndicesBackend extends BulkTableBackend<LibraryTemplateIndexDto> {

    private final LibraryTemplate libraryTemplate;

    public BulkEditTemplateIndicesBackend(LibraryTemplate libraryTemplate) {
      super("libraryTemplate_index", LibraryTemplateIndexDto.class);
      this.libraryTemplate = libraryTemplate;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("pageMode", "edit");
      config.set("libraryTemplate", mapper.valueToTree(Dtos.asDto(libraryTemplate)));
      config.set("indexFamily", mapper.valueToTree(Dtos.asDto(libraryTemplate.getIndexFamily())));
    }

    public ModelAndView edit(String positionsString, ModelMap model) throws IOException {
      String[] positions = positionsString.split(",");
      List<LibraryTemplateIndexDto> dtos = Arrays.stream(positions)
          .map(position -> new LibraryTemplateIndexDto(position, libraryTemplate.getIndexOnes().get(position),
              libraryTemplate.getIndexTwos().get(position)))
          .collect(Collectors.toList());
      return prepare(model, false, "Edit Library Template Indices", dtos);
    }

  }

  @GetMapping("/{templateId}/indices/edit")
  public ModelAndView editIndices(@PathVariable("templateId") long templateId, @RequestParam("positions") String positions, ModelMap model)
      throws IOException {
    LibraryTemplate template = getTemplateWithIndexFamily(templateId);
    return new BulkEditTemplateIndicesBackend(template).edit(positions, model);
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
