package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;
import uk.ac.bbsrc.tgac.miso.dto.AttachmentCategoryDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.AttachmentCategoryService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.MenuController;

@Controller
@RequestMapping("/rest/attachmentcategories")
public class AttachmentCategoryRestController extends RestController {

  @Autowired
  private AttachmentCategoryService attachmentCategoryService;

  @Autowired
  private MenuController menuController;

  @PostMapping
  public @ResponseBody AttachmentCategoryDto create(@RequestBody AttachmentCategoryDto dto) throws IOException {
    return doSave(dto);
  }

  @PutMapping("/{id}")
  public @ResponseBody AttachmentCategoryDto update(@PathVariable(value = "id", required = true) long id,
      @RequestBody AttachmentCategoryDto dto)
      throws IOException {
    if (dto.getId().longValue() != id) {
      throw new RestException("Attachment category ID mismatch", Status.BAD_REQUEST);
    }
    getExisting(id);
    return doSave(dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable(value = "id", required = true) long id) throws IOException {
    AttachmentCategory existing = getExisting(id);
    attachmentCategoryService.delete(existing);
    menuController.refreshConstants();
  }

  private AttachmentCategory getExisting(long id) throws IOException {
    AttachmentCategory existing = attachmentCategoryService.get(id);
    if (existing == null) {
      throw new RestException("Attachment category not found", Status.NOT_FOUND);
    }
    return existing;
  }

  private AttachmentCategoryDto doSave(AttachmentCategoryDto dto) throws IOException {
    AttachmentCategory category = Dtos.to(dto);
    long id = attachmentCategoryService.save(category);
    menuController.refreshConstants();
    AttachmentCategory saved = attachmentCategoryService.get(id);
    return Dtos.asDto(saved);
  }

}
