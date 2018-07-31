package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ServerErrorException;

@Controller
@RequestMapping("/attachments")
public class AttachmentController {

  private static final Logger log = LoggerFactory.getLogger(AttachmentController.class);

  @Autowired
  private FileAttachmentService fileAttachmentService;

  @Value("${miso.fileStorageDirectory}")
  private String fileStorageDirectory;

  @PostMapping(value = "/{entityType}/{entityId}")
  @ResponseStatus(HttpStatus.OK)
  public void acceptUpload(@PathVariable String entityType, @PathVariable long entityId, MultipartHttpServletRequest request)
      throws IOException {
    Attachable item = fileAttachmentService.get(entityType, entityId);
    if (item == null) {
      throw new NotFoundException(entityType + " not found");
    }

    for (MultipartFile fileItem : getMultipartFiles(request)) {
      fileAttachmentService.add(item, fileItem);
    }
  }

  private List<MultipartFile> getMultipartFiles(MultipartHttpServletRequest request) {
    List<MultipartFile> files = new ArrayList<>();
    request.getFileMap().forEach((key, fileItem) -> {
      if (fileItem.getSize() > 0) {
        files.add(fileItem);
      }
    });
    return files;
  }

  @GetMapping(value = "/{entityType}/{entityId}/{fileId}")
  public void downloadAttachment(@PathVariable String entityType, @PathVariable long entityId, @PathVariable long fileId,
      HttpServletResponse response) {
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

    response.setHeader("Content-Disposition", "attachment; filename=" + attachment.getFilename());

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
