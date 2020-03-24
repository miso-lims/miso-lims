package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.service.StainCategoryService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.StainCategoryDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/staincategories")
public class StainCategoryRestController extends RestController {

  @Autowired
  private StainCategoryService stainCategoryService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody StainCategoryDto create(@RequestBody StainCategoryDto dto) throws IOException {
    return RestUtils.createObject("Stain Category", dto, Dtos::to, stainCategoryService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{categoryId}")
  public @ResponseBody StainCategoryDto update(@PathVariable long categoryId, @RequestBody StainCategoryDto dto) throws IOException {
    return RestUtils.updateObject("Stain Category", categoryId, dto, Dtos::to, stainCategoryService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Stain Category", ids, stainCategoryService);
  }

}
