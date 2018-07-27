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

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.dto.DetailedLibraryTemplateDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTemplateDto;
import uk.ac.bbsrc.tgac.miso.service.LibraryTemplateService;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkCreateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;

@Controller
@RequestMapping("/librarytemplate")
@SessionAttributes("librarytemplate")
public class EditLibraryTemplateController {

  @Autowired
  private LibraryTemplateService libraryTemplateService;

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
      @RequestParam("projectId") Long projectId,
      ModelMap model) throws IOException {

    LibraryTemplateDto dto = (isDetailedSampleEnabled() ? new DetailedLibraryTemplateDto() : new LibraryTemplateDto());
    dto.setId(LibraryTemplate.UNSAVED_ID);
    dto.setProjectId(projectId);

    return new BulkCreateLibraryTemplateBackend(dto, quantity).create(model);
  }

  @GetMapping(value = "/bulk/edit")
  public ModelAndView editBulkLibraryTemplates(@RequestParam("ids") String libraryTemplateIds, ModelMap model) throws IOException {
    return new BulkEditLibraryTemplateBackend().edit(libraryTemplateIds, model);
  }
}
