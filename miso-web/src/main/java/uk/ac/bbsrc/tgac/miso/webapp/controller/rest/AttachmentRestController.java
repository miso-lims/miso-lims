package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;

@Controller
@RequestMapping("/rest/attachments")
public class AttachmentRestController extends RestController {

  @Autowired
  private FileAttachmentService fileAttachmentService;

  @PostMapping("/{entityType}/{entityId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void linkFile(@PathVariable String entityType, @PathVariable long entityId,
      @RequestParam(name = "fromEntityType", required = true) String fromEntityType,
      @RequestParam(name = "fromEntityId", required = true) long fromEntityId,
      @RequestParam(name = "attachmentId", required = true) long attachmentId) throws IOException {
    Attachable item = getAttachable(entityType, entityId, true);
    // This lookup will ensure that the user has read access to the source item (and its attachments)
    Attachable sourceItem = getAttachable(fromEntityType, fromEntityId, false);
    FileAttachment attachment = sourceItem.getAttachments().stream()
        .filter(att -> att.getId() == attachmentId)
        .findFirst().orElseThrow(() -> new RestException("Attachment not found", Status.BAD_REQUEST));
    fileAttachmentService.addLink(item, attachment);
  }

  @PostMapping("/{entityType}/shared")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkLinkFile(@PathVariable String entityType,
      @RequestParam(name = "fromEntityType", required = true) String fromEntityType,
      @RequestParam(name = "fromEntityId", required = true) long fromEntityId,
      @RequestParam(name = "attachmentId", required = true) long attachmentId,
      @RequestParam(name = "entityIds", required = true) String entityIds) throws IOException {
    List<Attachable> items = new ArrayList<>();
    for (Long id : LimsUtils.parseIds(entityIds)) {
      Attachable item = fileAttachmentService.get(entityType, id);
      if (item == null) {
        throw new ClientErrorException(String.format("%s %d not found", entityType, id));
      } else {
        items.add(item);
      }
    }
    // This lookup will ensure that the user has read access to the source item (and its attachments)
    Attachable sourceItem = getAttachable(fromEntityType, fromEntityId, false);
    FileAttachment attachment = sourceItem.getAttachments().stream()
        .filter(att -> att.getId() == attachmentId)
        .findFirst().orElseThrow(() -> new RestException("Attachment not found", Status.BAD_REQUEST));
    fileAttachmentService.addLinks(items, attachment);
  }

  @DeleteMapping("/{entityType}/{entityId}/{fileId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAttachment(@PathVariable String entityType, @PathVariable long entityId, @PathVariable long fileId)
      throws IOException {
    Attachable item = getAttachable(entityType, entityId, true);

    FileAttachment attachment = item.getAttachments().stream()
        .filter(a -> a.getId() == fileId)
        .findFirst().orElseThrow(() -> new RestException("File not found", Status.NOT_FOUND));

    fileAttachmentService.delete(item, attachment);
  }

  private Attachable getAttachable(String entityType, long entityId, boolean isTargetObject) throws IOException {
    Attachable item = fileAttachmentService.get(entityType, entityId);
    if (item == null) {
      if (isTargetObject) {
        throw new RestException(entityType + " not found", Status.NOT_FOUND);
      } else {
        throw new RestException(String.format("Source object %s %d not found", entityType, entityId),
            Status.BAD_REQUEST);
      }
    }
    return item;
  }

}
