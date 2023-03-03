package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.AssayTestService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryDesignCodeService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.core.service.TissueTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.AssayTestDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultAssayTestService extends AbstractSaveService<AssayTest> implements AssayTestService {

  @Autowired
  private AssayTestDao assayTestDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TissueTypeService tissueTypeService;
  @Autowired
  private SampleClassService sampleClassService;
  @Autowired
  private LibraryDesignCodeService libraryDesignCodeService;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  public void setDetailedSample(boolean detailedSample) {
    this.detailedSample = detailedSample;
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
  public List<AssayTest> listByIdList(List<Long> ids) throws IOException {
    return assayTestDao.listByIdList(ids);
  }

  @Override
  public List<AssayTest> list() throws IOException {
    return assayTestDao.list();
  }

  @Override
  public SaveDao<AssayTest> getDao() {
    return assayTestDao;
  }

  @Override
  protected void loadChildEntities(AssayTest object) throws IOException {
    loadChildEntity(object.getTissueType(), object::setTissueType, tissueTypeService);
    loadChildEntity(object.getExtractionClass(), object::setExtractionClass, sampleClassService);
    loadChildEntity(object.getLibraryDesignCode(), object::setLibraryDesignCode, libraryDesignCodeService);
    loadChildEntity(object.getLibraryQualificationDesignCode(), object::setLibraryQualificationDesignCode,
        libraryDesignCodeService);
  }

  @Override
  protected void collectValidationErrors(AssayTest object, AssayTest beforeChange, List<ValidationError> errors)
      throws IOException {
    if (object.getLibraryQualificationMethod() == null) {
      errors.add(ValidationError.forRequired("libraryQualificationMethod"));
    }

    if (detailedSample) {
      if (object.getExtractionClass() == null) {
        errors.add(ValidationError.forRequired("extractionClassId"));
      }
      if (object.getLibraryDesignCode() == null) {
        errors.add(ValidationError.forRequired("libraryDesignCodeId"));
      }
      if (object.getLibraryQualificationMethod() == AssayTest.LibraryQualificationMethod.ALIQUOT) {
        if (object.getLibraryQualificationDesignCode() == null) {
          errors.add(ValidationError.forRequired("libraryQualificationDesignCodeId"));
        }
      } else if (object.getLibraryQualificationDesignCode() != null) {
        errors.add(new ValidationError("libraryQualificationDesignCodeId",
            "Invalid for the selected library qualification method"));
      }
    }
  }

  @Override
  protected void applyChanges(AssayTest to, AssayTest from) throws IOException {
    to.setAlias(from.getAlias());
    to.setTissueType(from.getTissueType());
    to.setNegateTissueType(from.isNegateTissueType());
    to.setExtractionClass(from.getExtractionClass());
    to.setLibraryDesignCode(from.getLibraryDesignCode());
    to.setLibraryQualificationMethod(from.getLibraryQualificationMethod());
    to.setLibraryQualificationDesignCode(from.getLibraryQualificationDesignCode());
    to.setRepeatPerTimepoint(from.isRepeatPerTimepoint());
  }

  @Override
  public ValidationResult validateDeletion(AssayTest object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = assayTestDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.assays(usage)));
    }
    return result;
  }

  @Override
  protected void authorizeUpdate(AssayTest object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }
}
