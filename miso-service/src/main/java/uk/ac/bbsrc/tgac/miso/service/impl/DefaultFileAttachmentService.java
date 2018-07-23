package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.store.AttachableStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.service.RunService;
import uk.ac.bbsrc.tgac.miso.service.ServiceRecordService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultFileAttachmentService implements FileAttachmentService {

  private static final Logger log = LoggerFactory.getLogger(DefaultFileAttachmentService.class);

  @Autowired
  private AttachableStore attachableStore;

  @Autowired
  private PoolService poolService;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private RunService runService;

  @Autowired
  private ServiceRecordService serviceRecordService;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Value("${miso.fileStorageDirectory}")
  private String fileStorageDirectory;

  private final Map<String, Function<Long, Attachable>> entityFetchers = new HashMap<>();

  public DefaultFileAttachmentService() {
    entityFetchers.put("pool", WhineyFunction.rethrow(id -> poolService.get(id)));
    entityFetchers.put("project", WhineyFunction.rethrow(id -> projectService.getProjectById(id)));
    entityFetchers.put("run", WhineyFunction.rethrow(id -> runService.get(id)));
    entityFetchers.put("servicerecord", WhineyFunction.rethrow(id -> serviceRecordService.get(id)));
  }

  public void setAttachableStore(AttachableStore attachableStore) {
    this.attachableStore = attachableStore;
  }

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  public void setServiceRecordService(ServiceRecordService serviceRecordService) {
    this.serviceRecordService = serviceRecordService;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setFileStorageDirectory(String fileStorageDirectory) {
    this.fileStorageDirectory = fileStorageDirectory;
  }

  @Override
  public Attachable get(String entityType, long entityId) {
    Function<Long, Attachable> fetcher = entityFetchers.get(entityType);
    if (fetcher == null) {
      throw new IllegalArgumentException("Unknown entity type: " + entityType);
    }
    return fetcher.apply(entityId);
  }

  @Override
  public void add(Attachable object, MultipartFile file) throws IOException {
    Attachable managed = attachableStore.getManaged(object);
    String saveFilename = Long.toString(new Date().getTime());
    String relativeDir = makeRelativeDir(object.getAttachmentsTarget(), object.getId());
    File saveDir = new File(makeFullPath(relativeDir));
    File targetFile = new File(saveDir, saveFilename);
    while (targetFile.exists()) {
      saveFilename = Long.toString(Long.valueOf(saveFilename) + 1);
      targetFile = new File(saveDir, saveFilename);
    }
    if (LimsUtils.checkDirectory(saveDir, true)) {
      file.transferTo(targetFile);
    } else {
      throw new IOException("Cannot upload file - check that the directory specified in miso.properties exists and is writable");
    }

    try {
      FileAttachment attachment = new FileAttachment();
      attachment.setFilename(file.getOriginalFilename());
      attachment.setPath(relativeDir + File.separator + saveFilename);
      attachment.setCreator(authorizationManager.getCurrentUser());
      attachment.setCreationTime(new Date());
      managed.getAttachments().add(attachment);
      attachableStore.save(managed);
    } catch (Exception e) {
      if (!targetFile.delete()) {
        log.error("Failed to save attachment, but file was still saved: {}", targetFile.getAbsolutePath());
      }
      throw e;
    }
  }

  @Override
  public void delete(Attachable object, FileAttachment attachment) throws IOException {
    if (!object.getAttachments().contains(attachment)) {
      throw new IllegalArgumentException("Attachment does not belong to this object");
    }
    Attachable managed = attachableStore.getManaged(object);
    FileAttachment managedAttachment = managed.getAttachments().stream()
        .filter(a -> a.getId() == attachment.getId())
        .findFirst().orElseThrow(() -> new IllegalArgumentException("Attachment not found in persisted entity"));

    authorizationManager.throwIfNonAdminOrMatchingOwner(managedAttachment.getCreator());

    File file = new File(makeFullPath(managedAttachment.getPath()));
    if (file.exists()) {
      if (!file.isFile()) {
        throw new IOException("Cannot delete. Not a file");
      }
      if (!file.canWrite()) {
        throw new IOException("Cannot delete file. Permission denied");
      }
    }

    managed.getAttachments().remove(attachment);
    attachableStore.save(managed);

    if (file.exists()) {
      deleteFileOrLog(file, managed, managedAttachment);
    }
  }

  @Override
  public void afterDelete(Attachable object) {
    for (FileAttachment a : object.getAttachments()) {
      File file = new File(makeFullPath(a.getPath()));
      deleteFileOrLog(file, object, a);
    }
    File dir = new File(makeFullPath(makeRelativeDir(object.getAttachmentsTarget(), object.getId())));
    if (dir.exists() && dir.isDirectory()) {
      if (dir.listFiles() != null) {
        for (File file : dir.listFiles()) {
          deleteFileOrLog(file, object, null);
        }
      }
      if (!dir.delete()) {
        log.error("Failed to delete directory for deleted object: {}", dir.getAbsolutePath());
      }
    }
  }

  private void deleteFileOrLog(File file, Attachable object, FileAttachment attachment) {
    if (!file.delete()) {
      log.error("File no longer associated with an object, but failed to delete: {}{} from {} {}", new Object[] { file.getName(),
          (attachment == null ? "" : " (" + attachment.getFilename() + ")"), object.getAttachmentsTarget(), Long.valueOf(object.getId()) });
    }
  }

  private String makeRelativeDir(String entityType, long entityId) {
    return File.separator + entityType + File.separator + entityId;
  }

  private String makeFullPath(String relativePath) {
    return fileStorageDirectory
        + (fileStorageDirectory.endsWith(File.separator) || relativePath.startsWith(File.separator) ? "" : File.separator)
        + relativePath;
  }

}
