package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.*;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.AttachableStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultFileAttachmentService implements FileAttachmentService {

  private static final Logger log = LoggerFactory.getLogger(DefaultFileAttachmentService.class);

  @Autowired
  private AttachableStore attachableStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Value("${miso.fileStorageDirectory}")
  private String fileStorageDirectory;

  private final Map<String, ProviderService<? extends Attachable>> entityProviders = new HashMap<>();

  public void setAttachableStore(AttachableStore attachableStore) {
    this.attachableStore = attachableStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Autowired
  public void setLibraryService(LibraryService libraryService) {
    entityProviders.put("library", libraryService);
  }

  @Autowired
  public void setPoolService(PoolService poolService) {
    entityProviders.put("pool", poolService);
  }

  @Autowired
  public void setProjectService(ProjectService projectService) {
    entityProviders.put("project", projectService);
  }

  @Autowired
  public void setRunService(RunService runService) {
    entityProviders.put("run", runService);
  }

  @Autowired
  public void setArrayRunService(ArrayRunService arrayRunService) {
    entityProviders.put("arrayrun", arrayRunService);
  }

  @Autowired
  public void setSampleService(SampleService sampleService) {
    entityProviders.put("sample", sampleService);
  }

  @Autowired
  public void setLibraryAliquotService(LibraryAliquotService libraryAliquotService) { entityProviders.put("libraryaliquot", libraryAliquotService); }

  @Autowired
  public void setServiceRecordService(ServiceRecordService serviceRecordService) {
    entityProviders.put("servicerecord", serviceRecordService);
  }

  @Autowired
  public void setRequisitionService(RequisitionService requisitionService) {
    entityProviders.put("requisition", requisitionService);
  }

  public void setFileStorageDirectory(String fileStorageDirectory) {
    this.fileStorageDirectory = fileStorageDirectory;
  }

  @Override
  public Attachable get(String entityType, long entityId) throws IOException {
    ProviderService<? extends Attachable> provider = entityProviders.get(entityType);
    if (provider == null) {
      throw new IllegalArgumentException("Unknown entity type: " + entityType);
    }
    return provider.get(entityId);
  }

  @Override
  public void add(Attachable object, MultipartFile file, AttachmentCategory category) throws IOException {
    Attachable managed = attachableStore.getManaged(object);
    String relativeDir = makeRelativeDir(object.getAttachmentsTarget(), object.getId());
    File targetFile = storeFile(relativeDir, file);

    try {
      FileAttachment attachment = makeAttachment(file, relativeDir, targetFile, category);
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
  public void addShared(Collection<Attachable> objects, MultipartFile file, AttachmentCategory category) throws IOException {
    List<Attachable> managed = objects.stream().map(attachableStore::getManaged).collect(Collectors.toList());
    if (managed.stream().map(Attachable::getAttachmentsTarget).distinct().count() > 1L) {
      throw new IllegalArgumentException("Target objects are not all the same type");
    }
    String relativeDir = makeRelativeSharedDir(managed.get(0).getAttachmentsTarget());
    File targetFile = storeFile(relativeDir, file);

    try {
      FileAttachment attachment = makeAttachment(file, relativeDir, targetFile, category);
      for (Attachable item : managed) {
        item.getAttachments().add(attachment);
        attachableStore.save(item);
      }
    } catch (Exception e) {
      if (!targetFile.delete()) {
        log.error("Failed to save attachment, but file was still saved: {}", targetFile.getAbsolutePath());
      }
      throw e;
    }
  }

  private File storeFile(String relativeDir, MultipartFile file) throws IOException {
    String saveFilename = Long.toString(new Date().getTime());
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
    return targetFile;
  }

  private FileAttachment makeAttachment(MultipartFile sourceFile, String relativeDir, File targetFile, AttachmentCategory category)
      throws IOException {
    FileAttachment attachment = new FileAttachment();
    attachment.setFilename(sourceFile.getOriginalFilename());
    attachment.setPath(relativeDir + File.separator + targetFile.getName());
    attachment.setCategory(category);
    attachment.setCreator(authorizationManager.getCurrentUser());
    attachment.setCreationTime(new Date());
    attachableStore.save(attachment);
    return attachment;
  }

  @Override
  public void addLink(Attachable object, FileAttachment attachment) throws IOException {
    Attachable managedObject = attachableStore.getManaged(object);
    FileAttachment managedAttachment = getManaged(attachment);
    addLinkIfNecessary(managedObject, managedAttachment);
  }

  @Override
  public void addLinks(Collection<Attachable> objects, FileAttachment attachment) throws IOException {
    List<Attachable> managed = objects.stream().map(attachableStore::getManaged).collect(Collectors.toList());
    FileAttachment managedAttachment = getManaged(attachment);
    for (Attachable item : managed) {
      addLinkIfNecessary(item, managedAttachment);
    }
  }

  private FileAttachment getManaged(FileAttachment attachment) {
    FileAttachment managedAttachment = attachableStore.getAttachment(attachment.getId());
    if (managedAttachment == null) {
      throw new IllegalArgumentException("Attachment not found");
    }
    return managedAttachment;
  }

  private void addLinkIfNecessary(Attachable managedObject, FileAttachment managedAttachment) {
    if (!managedObject.getAttachments().contains(managedAttachment)) {
      managedObject.getAttachments().add(managedAttachment);
      attachableStore.save(managedObject);
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

    managed.getAttachments().remove(managedAttachment);
    attachableStore.save(managed);

    // Only delete the file if it is not attached to any other items
    if (file.exists() && attachableStore.getUsage(managedAttachment) == 1) {
      attachableStore.delete(managedAttachment);
      deleteFileOrLog(file, managed, managedAttachment);
    }
  }

  @Override
  public void beforeDelete(Attachable object) throws IOException {
    // Delete database entity only. Do not delete file until after the object is successfully deleted
    object.setPendingAttachmentDeletions(new ArrayList<>(object.getAttachments()));
  }

  @Override
  public void afterDelete(Attachable object) throws IOException {
    // Delete attachments from the object and the associated files if they are not used by anything else
    if (object.getPendingAttachmentDeletions() != null) {
      for (FileAttachment attachment : object.getPendingAttachmentDeletions()) {
        File file = new File(makeFullPath(attachment.getPath()));
        if (file.exists() && attachableStore.getUsage(attachment) == 0) {
          attachableStore.delete(attachment);
          deleteFileOrLog(file, object, attachment);
        }
      }
    }
    // Delete the entity's attachments directory and anything in it
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

  private String makeRelativeSharedDir(String entityType) {
    return File.separator + entityType + File.separator + "shared";
  }

  private String makeFullPath(String relativePath) {
    return fileStorageDirectory
        + (fileStorageDirectory.endsWith(File.separator) || relativePath.startsWith(File.separator) ? "" : File.separator)
        + relativePath;
  }

}
