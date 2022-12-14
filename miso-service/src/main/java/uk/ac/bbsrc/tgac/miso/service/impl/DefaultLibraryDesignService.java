package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryDesignCodeService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryDesignService;
import uk.ac.bbsrc.tgac.miso.core.service.LibrarySelectionService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryStrategyService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryDesignDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryDesignService extends AbstractSaveService<LibraryDesign> implements LibraryDesignService {

  @Autowired
  private LibraryDesignDao libraryDesignDao;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private SampleClassService sampleClassService;
  @Autowired
  private LibraryDesignCodeService libraryDesignCodeService;
  @Autowired
  private LibrarySelectionService librarySelectionService;
  @Autowired
  private LibraryStrategyService libraryStrategyService;

  @Override
  public SaveDao<LibraryDesign> getDao() {
    return libraryDesignDao;
  }

  public void setLibraryDesignDao(LibraryDesignDao libraryDesignDao) {
    this.libraryDesignDao = libraryDesignDao;
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
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<LibraryDesign> list() throws IOException {
    return libraryDesignDao.list();
  }

  @Override
  public List<LibraryDesign> listByClass(SampleClass sampleClass) throws IOException {
    return libraryDesignDao.listByClass(sampleClass);
  }

  @Override
  public List<LibraryDesign> listByIdList(List<Long> ids) throws IOException {
    return libraryDesignDao.listByIdList(ids);
  }

  @Override
  protected void authorizeUpdate(LibraryDesign object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void loadChildEntities(LibraryDesign object) throws IOException {
    loadChildEntity(object.getLibraryDesignCode(), object::setLibraryDesignCode, libraryDesignCodeService);
    loadChildEntity(object.getSampleClass(), object::setSampleClass, sampleClassService);
    loadChildEntity(object.getLibrarySelectionType(), object::setLibrarySelectionType, librarySelectionService);
    loadChildEntity(object.getLibraryStrategyType(), object::setLibraryStrategyType, libraryStrategyService);
  }

  @Override
  protected void collectValidationErrors(LibraryDesign object, LibraryDesign beforeChange, List<ValidationError> errors)
      throws IOException {
    if ((ValidationUtils.isSetAndChanged(LibraryDesign::getName, object, beforeChange)
        || ValidationUtils.isSetAndChanged(LibraryDesign::getSampleClass, object, beforeChange))
        && libraryDesignDao.getByNameAndSampleClass(object.getName(), object.getSampleClass()) != null) {
      errors.add(new ValidationError("name",
          "There is already a library design with this name for sample class " + object.getSampleClass().getAlias()));
    }
    
    // Don't allow changing sampleClass if design is used already because the existing libraries would then have an invalid design.
    // Don't allow changing code, selection, strategy if design is used already because then the existing libraries' properties would be
    // out of sync.
    if (beforeChange != null) {
      long usage = libraryDesignDao.getUsage(object);
      if (usage > 0L) {
        if (ValidationUtils.isSetAndChanged(LibraryDesign::getSampleClass, object, beforeChange)) {
          errors.add(new ValidationError("sampleClass",
              "Cannot change sample class because library design is in use by " + usage + " " + Pluralizer.libraries(usage)));
        }
        if (ValidationUtils.isSetAndChanged(LibraryDesign::getLibraryDesignCode, object, beforeChange)) {
          errors.add(new ValidationError("libraryDesignCode",
              "Cannot change design code because library design is in use by " + usage + " " + Pluralizer.libraries(usage)));
        }
        if (ValidationUtils.isSetAndChanged(LibraryDesign::getLibrarySelectionType, object, beforeChange)) {
          errors.add(new ValidationError("librarySelectionType",
              "Cannot change selection type because library design is in use by " + usage + " " + Pluralizer.libraries(usage)));
        }
        if (ValidationUtils.isSetAndChanged(LibraryDesign::getLibraryStrategyType, object, beforeChange)) {
          errors.add(new ValidationError("libraryStrategyType",
              "Cannot change strategy type because library design is in use by " + usage + " " + Pluralizer.libraries(usage)));
        }
      }
    }
  }

  @Override
  protected void applyChanges(LibraryDesign to, LibraryDesign from) {
    to.setName(from.getName());
    to.setSampleClass(from.getSampleClass());
    to.setLibraryDesignCode(from.getLibraryDesignCode());
    to.setLibrarySelectionType(from.getLibrarySelectionType());
    to.setLibraryStrategyType(from.getLibraryStrategyType());
  }

  @Override
  public ValidationResult validateDeletion(LibraryDesign object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = libraryDesignDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.libraries(usage)));
    }
    return result;
  }

}
