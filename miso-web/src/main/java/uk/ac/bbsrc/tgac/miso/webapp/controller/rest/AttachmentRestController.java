package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.service.FileAttachmentService;

@Controller
@RequestMapping("/rest/attachments")
public class AttachmentRestController extends RestController {

  @Autowired
  private FileAttachmentService fileAttachmentService;

  @DeleteMapping("/{entityType}/{entityId}/{fileId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAttachment(@PathVariable String entityType, @PathVariable long entityId, @PathVariable long fileId) throws IOException {
    Attachable item = fileAttachmentService.get(entityType, entityId);
    if (item == null) {
      throw new NotFoundException(entityType + " not found");
    }

    FileAttachment attachment = item.getAttachments().stream()
        .filter(a -> a.getId() == fileId)
        .findFirst().orElseThrow(() -> new RestException("File not found", Status.NOT_FOUND));

    fileAttachmentService.delete(item, attachment);
  }

}
