package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.isChanged;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.SopField;
import uk.ac.bbsrc.tgac.miso.core.data.SopField.FieldType;
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

  private static final int SOPFIELD_NAME_MAX = 255;
  private static final int SOPFIELD_UNITS_MAX = 50;
  private static final String FIELDS_PROPERTY = "fields";

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
    if (sop.getCategory() == null) {
      errors.add(ValidationError.forRequired("category"));
      return;
    }

    if (beforeChange != null) {
      if (isChanged(Sop::getCategory, sop, beforeChange)) {
        errors.add(new ValidationError("category", "Category cannot be changed"));
      }
      if (isChanged(Sop::getVersion, sop, beforeChange)) {
        errors.add(new ValidationError("version", "Version cannot be changed"));
      }
    }

    if (isChanged(Sop::getAlias, sop, beforeChange) || isChanged(Sop::getVersion, sop, beforeChange)) {
      Sop existing = sopDao.get(sop.getCategory(), sop.getAlias(), sop.getVersion());
      if (existing != null && existing.getId() != sop.getId()) {
        errors.add(ValidationError.forDuplicate("SOP", null, "alias and version"));
      }
    }

    validateSopFields(sop, beforeChange, errors);
  }

  private void validateSopFields(Sop sop, Sop beforeChange, List<ValidationError> errors) {
    Set<SopField> fields = sop.getSopFields();

    if (sop.getCategory() != SopCategory.RUN) {
      if (fields != null && !fields.isEmpty()) {
        errors.add(new ValidationError(FIELDS_PROPERTY, "Only Run SOPs may have fields"));
      }
      return;
    }

    if (fields == null || fields.isEmpty()) {
      return;
    }

    Map<String, SopField> byName = new HashMap<>();
    Map<Long, SopField> beforeById = new HashMap<>();
    if (beforeChange != null && beforeChange.getSopFields() != null) {
      for (SopField beforeField : beforeChange.getSopFields()) {
        beforeById.put(beforeField.getId(), beforeField);
        byName.put(beforeField.getName().toLowerCase(), beforeField);
      }
    }

    for (SopField field : fields) {
      String name = field.getName();
      if (name == null || name.isEmpty()) {
        errors.add(new ValidationError(FIELDS_PROPERTY, "Field name is required"));
      }
      if (name.length() > SOPFIELD_NAME_MAX) {
        errors.add(
            new ValidationError(FIELDS_PROPERTY, "Field name must be at most " + SOPFIELD_NAME_MAX + " characters"));
      }

      String key = name.toLowerCase();
      SopField existingWithName = byName.get(key);
      if (existingWithName != null) {
        if (!field.isSaved() || existingWithName.getId() != field.getId()) {
          errors.add(new ValidationError(FIELDS_PROPERTY, "Field names must be unique"));
        }
      } else {
        byName.put(key, field);
      }

      String units = field.getUnits();
      if (units != null && units.length() > SOPFIELD_UNITS_MAX) {
        errors.add(new ValidationError(FIELDS_PROPERTY, "Units must be at most " + SOPFIELD_UNITS_MAX + " characters"));
      }

      FieldType fieldType = field.getFieldType();
      if (fieldType == null) {
        errors.add(new ValidationError(FIELDS_PROPERTY, "Field type is required"));
      }

      if (beforeChange != null && field.isSaved()) {
        SopField before = beforeById.get(field.getId());
        if (before != null) {
          FieldType beforeType = before.getFieldType();
          FieldType afterType = field.getFieldType();
          if (!beforeType.equals(afterType)) {
            errors.add(new ValidationError(FIELDS_PROPERTY, "Field type cannot be changed for existing fields"));
          }
        }
      }
    }
  }

  @Override
  protected void applyChanges(Sop to, Sop from) throws IOException {
    to.setAlias(from.getAlias());
    to.setUrl(from.getUrl());
    to.setArchived(from.isArchived());

    syncSopFields(to, from);
  }

  private void syncSopFields(Sop to, Sop from) {
    Set<SopField> toFields = to.getSopFields();
    Set<SopField> fromFields = from.getSopFields();
    if (fromFields == null || fromFields.isEmpty()) {
      toFields.clear();
      return;
    }

    Set<Long> fromFieldIds = new HashSet<>();
    for (SopField fromField : fromFields) {
      if (fromField.isSaved()) {
        fromFieldIds.add(fromField.getId());
      }
    }

    Map<Long, SopField> toFieldById = new HashMap<>();
    for (SopField toField : toFields) {
      if (toField.isSaved()) {
        toFieldById.put(toField.getId(), toField);
      }
    }

    for (Iterator<SopField> it = toFields.iterator(); it.hasNext();) {
      SopField toField = it.next();
      if (toField.isSaved() && !fromFieldIds.contains(toField.getId())) {
        it.remove();
      }
    }

    for (SopField fromField : fromFields) {
      if (fromField.isSaved()) {
        SopField toField = toFieldById.get(fromField.getId());
        if (toField != null) {
          toField.setName(fromField.getName());
          toField.setUnits(fromField.getUnits());
          toField.setSop(to);
        }
      } else {
        SopField newField = new SopField();
        newField.setSop(to);
        newField.setName(fromField.getName());
        newField.setUnits(fromField.getUnits());
        newField.setFieldType(fromField.getFieldType());
        toFields.add(newField);
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
      PaginationFilter... filter) throws IOException {
    return sopDao.list(offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }
}
