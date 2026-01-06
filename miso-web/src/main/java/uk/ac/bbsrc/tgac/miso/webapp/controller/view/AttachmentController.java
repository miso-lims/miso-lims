package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.service.AttachmentCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ServerErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/attachments")
public class AttachmentController {

  private static final Logger log = LoggerFactory.getLogger(AttachmentController.class);

  @Autowired
  private FileAttachmentService fileAttachmentService;

  @Autowired
  private AttachmentCategoryService attachmentCategoryService;

  @Value("${miso.fileStorageDirectory}")
  private String fileStorageDirectory;

  @PostMapping(value = "/{entityType}/{entityId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void acceptUpload(@PathVariable String entityType, @PathVariable long entityId,
      @RequestParam(required = false) Long categoryId,
      @RequestParam("files") MultipartFile files[]) throws IOException {
    Attachable item = fileAttachmentService.get(entityType, entityId);
    if (item == null) {
      throw new NotFoundException(entityType + " not found");
    }
    AttachmentCategory category = getCategory(categoryId);

    for (MultipartFile fileItem : files) {
      fileAttachmentService.add(item, fileItem, category);
    }
  }

  @PostMapping(value = "/{entityType}/shared")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void acceptSharedUpload(@PathVariable String entityType, @RequestParam String entityIds,
      @RequestParam(required = false) Long categoryId, @RequestParam("files") MultipartFile files[])
      throws IOException {
    List<Attachable> items = new ArrayList<>();
    for (Long id : LimsUtils.parseIds(entityIds)) {
      Attachable item = fileAttachmentService.get(entityType, id);
      if (item == null) {
        throw new ClientErrorException(String.format("%s %d not found", entityType, id));
      } else {
        items.add(item);
      }
    }
    AttachmentCategory category = getCategory(categoryId);

    for (MultipartFile fileItem : files) {
      fileAttachmentService.addShared(items, fileItem, category);
    }
  }

  private AttachmentCategory getCategory(Long categoryId) throws IOException {
    if (categoryId == null) {
      return null;
    }
    AttachmentCategory category = attachmentCategoryService.get(categoryId);
    if (category == null) {
      throw new ClientErrorException("Attachment category not found");
    }
    return category;
  }

  @GetMapping(value = "/{entityType}/{entityId}/{fileId}")
  public void downloadAttachment(@PathVariable String entityType, @PathVariable long entityId,
      @PathVariable long fileId,
      HttpServletResponse response) throws IOException {
    Attachable item = fileAttachmentService.get(entityType, entityId);
    if (item == null) {
      throw new NotFoundException(entityType + " not found");
    }
    FileAttachment attachment = item.getAttachments().stream()
        .filter(a -> a.getId() == fileId)
        .findFirst().orElseThrow(() -> new NotFoundException("File not found"));
    final File file = new File(fileStorageDirectory + File.separator + attachment.getPath());

    if (!file.exists()) {
      throw new ServerErrorException("File not found on server");
    } else if (!file.canRead()) {
      throw new ServerErrorException("MISO cannot access this file");
    }

    MisoWebUtils.addAttachmentContentDisposition(response, attachment.getFilename());

    try (OutputStream responseStream = response.getOutputStream();
        FileInputStream fis = new FileInputStream(file)) {
      int read = 0;
      byte[] bytes = new byte[1024];
      while ((read = fis.read(bytes)) != -1) {
        responseStream.write(bytes, 0, read);
      }
      responseStream.flush();
    } catch (IOException e) {
      log.error("Error downloading file", e);
      throw new ServerErrorException("Error downloading file", e);
    }
  }

}
