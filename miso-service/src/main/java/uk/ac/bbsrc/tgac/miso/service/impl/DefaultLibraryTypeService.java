package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryTypeDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;
import uk.ac.bbsrc.tgac.miso.service.LibraryTypeService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultLibraryTypeService extends AbstractSaveService<LibraryType> implements LibraryTypeService {

  @Autowired
  private LibraryTypeDao libraryTypeDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public SaveDao<LibraryType> getDao() {
    return libraryTypeDao;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public List<LibraryType> list() throws IOException {
    return libraryTypeDao.list();
  }

  @Override
  public List<LibraryType> listByPlatform(PlatformType platform) throws IOException {
    return libraryTypeDao.listByPlatform(platform);
  }

  @Override
  protected void authorizeSave(LibraryType object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(LibraryType object, LibraryType beforeChange, List<ValidationError> errors) throws IOException {
    if ((ValidationUtils.isSetAndChanged(LibraryType::getPlatformType, object, beforeChange)
        || ValidationUtils.isSetAndChanged(LibraryType::getDescription, object, beforeChange))
        && libraryTypeDao.getByPlatformAndDescription(object.getPlatformType(), object.getDescription()) != null) {
      errors.add(new ValidationError("description",
          "There is already a library type with this description for platform: " + object.getPlatformType().getKey()));
    }
    long libUsage = libraryTypeDao.getUsageByLibraries(beforeChange);
    long tempUsage = libraryTypeDao.getUsageByLibraryTemplates(beforeChange);
    if ((libUsage > 0L || tempUsage > 0L) && ValidationUtils.isSetAndChanged(LibraryType::getPlatformType, object, beforeChange)) {
      errors.add(new ValidationError("platform",
          String.format("Cannot be changed because library type is already used by %d librar%s and %d library template%s", libUsage,
              libUsage == 1L ? "y" : "ies", tempUsage, tempUsage == 1L ? "" : "s")));
    }
  }

  @Override
  protected void applyChanges(LibraryType to, LibraryType from) {
    to.setDescription(from.getDescription());
    to.setPlatformType(from.getPlatformType());
    to.setAbbreviation(from.getAbbreviation());
    to.setArchived(from.getArchived());
  }

  @Override
  public ValidationResult validateDeletion(LibraryType object) throws IOException {
    ValidationResult result = new ValidationResult();
    long libUsage = libraryTypeDao.getUsageByLibraries(object);
    if (libUsage > 0L) {
      result.addError(
          new ValidationError(
              "Library type '" + object.getDescription() + "' is used by " + libUsage + " librar" + (libUsage > 1L ? "ies" : "y")));
    }
    long tempUsage = libraryTypeDao.getUsageByLibraryTemplates(object);
    if (tempUsage > 0L) {
      result.addError(
          new ValidationError("Library type '" + object.getDescription() + "' is used by " + tempUsage + " library template"
              + (tempUsage > 1L ? "s" : "")));
    }
    return result;
  }

}
