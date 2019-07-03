package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationMapService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.StorageLocationMapDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultStorageLocationMapService implements StorageLocationMapService {

  private static final Logger log = LoggerFactory.getLogger(DefaultStorageLocationMapService.class);

  private static final String mapsDirectory = "freezermaps";

  @Autowired
  private StorageLocationMapDao storageLocationMapDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Value("${miso.fileStorageDirectory}")
  private String fileStorageDirectory;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public StorageLocationMap get(long id) throws IOException {
    return storageLocationMapDao.get(id);
  }

  @Override
  public long create(MultipartFile file, String description) throws IOException {
    authorizationManager.throwIfNonAdmin();
    StorageLocationMap map = new StorageLocationMap();
    map.setDescription(description);
    map.setFilename(file.getOriginalFilename());
    validateChange(map, null);
    storeFile(file);
    return storageLocationMapDao.create(map);
  }

  private File storeFile(MultipartFile file) throws IOException {
    String saveFilename = file.getOriginalFilename();
    File saveDir = new File(getSaveDir());
    File targetFile = new File(saveDir, saveFilename);
    if (LimsUtils.checkDirectory(saveDir, true)) {
      file.transferTo(targetFile);
    } else {
      throw new IOException("Cannot upload file - check that the directory specified in miso.properties exists and is writable");
    }
    return targetFile;
  }

  private String getSaveDir() {
    return fileStorageDirectory
        + (fileStorageDirectory.endsWith(File.separator) ? "" : File.separator)
        + mapsDirectory;
  }

  @Override
  public long update(StorageLocationMap map) throws IOException {
    authorizationManager.throwIfNonAdmin();
    StorageLocationMap managed = storageLocationMapDao.get(map.getId());
    validateChange(map, managed);
    applyChanges(managed, map);
    return storageLocationMapDao.update(managed);
  }

  private void validateChange(StorageLocationMap map, StorageLocationMap beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(StorageLocationMap::getFilename, map, beforeChange)
        && storageLocationMapDao.getByFilename(map.getFilename()) != null) {
      errors.add(new ValidationError("There is already a map with this filename"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(StorageLocationMap to, StorageLocationMap from) {
    to.setDescription(from.getDescription());
  }

  @Override
  public List<StorageLocationMap> list() throws IOException {
    return storageLocationMapDao.list();
  }

  @Override
  public ValidationResult validateDeletion(StorageLocationMap object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = storageLocationMapDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.freezers(usage)));
    }
    return result;
  }

  @Override
  public void afterDelete(StorageLocationMap object) throws IOException {
    File file = new File(getSaveDir(), object.getFilename());
    if (file.exists()) {
      if (!file.delete()) {
        log.error("Location map no longer associated with an object, but failed to delete: {}", new Object[] { file.getName() });
      }
    }
  }

}
