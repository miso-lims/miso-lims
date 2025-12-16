package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.isChanged;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.SopField;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.SopDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultSopService extends AbstractSaveService<Sop> implements SopService {

  @Autowired
  private SopDao sopDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public SaveDao<Sop> getDao() {
    return sopDao;
  }

  @Override
  public List<Sop> listByCategory(SopCategory category) throws IOException {
    return sopDao.listByCategory(category);
  }

  @Override
  public List<Sop> list() throws IOException {
    return sopDao.list();
  }

  @Override
  protected void authorizeUpdate(Sop object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(Sop sop, Sop beforeChange, List<ValidationError> errors) throws IOException {
    if ((isChanged(Sop::getAlias, sop, beforeChange) || isChanged(Sop::getVersion, sop, beforeChange))
        && sopDao.get(sop.getCategory(), sop.getAlias(), sop.getVersion()) != null) {
      errors.add(ValidationError.forDuplicate("SOP", null, "alias and version"));
    }

    // ADDED: Validate SOP fields (Ticket 1)
    validateSopFields(sop, errors);
  }

  @Override
  protected void applyChanges(Sop to, Sop from) throws IOException {
    to.setAlias(from.getAlias());
    to.setVersion(from.getVersion());
    to.setCategory(from.getCategory());
    to.setUrl(from.getUrl());
    to.setArchived(from.isArchived());

    // CORRECTED: Handle SOP fields for Ticket 1
    // Clear existing fields
    to.getSopFields().clear();

    // Add fields from the incoming object
    if (from.getSopFields() != null) {
      for (SopField fromField : from.getSopFields()) {
        SopField toField = new SopField();
        toField.setName(fromField.getName());
        toField.setUnits(fromField.getUnits());
        toField.setFieldType(fromField.getFieldType());
        to.addSopField(toField); // This sets the bidirectional relationship
      }
    }
  }


  private void validateSopFields(Sop sop, List<ValidationError> errors) {
    if (sop.getSopFields() == null || sop.getSopFields().isEmpty()) {
      return; // No fields to validate
    }

    Set<String> fieldNames = new HashSet<>();
    for (SopField field : sop.getSopFields()) {
      // Check field name is not empty
      if (field.getName() == null || field.getName().trim().isEmpty()) {
        errors.add(new ValidationError("sopFields", "SOP field name cannot be empty"));
        continue;
      }

      // Check for duplicate field names
      String normalizedName = field.getName().trim().toLowerCase();
      if (!fieldNames.add(normalizedName)) {
        errors.add(new ValidationError("sopFields", "Duplicate field name: " + field.getName()));
      }

      // Ensure field type is set
      if (field.getFieldType() == null) {
        errors.add(new ValidationError("sopFields",
            "Field type is required for field: " + field.getName()));
      }
    }
  }

  @Override
  public ValidationResult validateDeletion(Sop object) throws IOException {
    ValidationResult result = new ValidationResult();
    long sampleUsage = sopDao.getUsageBySamples(object);
    if (sampleUsage > 0) {
      result.addError(ValidationError.forDeletionUsage(object, sampleUsage, Pluralizer.samples(sampleUsage)));
    }
    long libUsage = sopDao.getUsageByLibraries(object);
    if (libUsage > 0) {
      result.addError(ValidationError.forDeletionUsage(object, libUsage, Pluralizer.libraries(libUsage)));
    }
    long runUsage = sopDao.getUsageByRuns(object);
    if (runUsage > 0) {
      result.addError(ValidationError.forDeletionUsage(object, runUsage, Pluralizer.runs(runUsage)));
    }
    return result;
  }

  @Override
  public List<Sop> listByIdList(List<Long> ids) throws IOException {
    return sopDao.listByIdList(ids);
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return sopDao.count(errorHandler, filter);
  }

  @Override
  public List<Sop> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter)
      throws IOException {
    return sopDao.list(offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

}
