package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.isChanged;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
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
  @Autowired
  private InstrumentModelService instrumentModelService;

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
  protected void loadChildEntities(Sop object) throws IOException {
    if (object.getFields() == null || object.getFields().isEmpty()) {
      return;
    }

    Map<Long, SopField> existingFieldsById = new HashMap<>();
    if (object.isSaved()) {
      Sop existing = sopDao.get(object.getId());
      if (existing != null) {
        for (SopField field : existing.getFields()) {
          existingFieldsById.put(field.getId(), field);
        }
      }
    }

    Set<SopField> loadedFields = new HashSet<>();
    for (SopField field : object.getFields()) {
      if (field.isSaved()) {
        SopField managedField = existingFieldsById.get(field.getId());
        if (managedField == null) {
          throw new ValidationException(
              new ValidationError(FIELDS_PROPERTY, "The submitted field ID does not match an existing field"));
        }
        loadedFields.add(managedField);
      } else {
        ValidationUtils.loadChildEntity(field::setInstrumentModel, field.getInstrumentModel(), instrumentModelService,
            FIELDS_PROPERTY);
        loadedFields.add(field);
      }
    }
    object.getFields().clear();
    object.getFields().addAll(loadedFields);
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
    Set<SopField> fields = sop.getFields();

    if (fields == null || fields.isEmpty()) {
      return;
    }
    if (sop.getCategory() != SopCategory.RUN) {
      errors.add(new ValidationError(FIELDS_PROPERTY, "Only Run SOPs may have fields"));
      return;
    }

    Set<String> fieldNames = new HashSet<>();
    for (SopField field : fields) {
      String name = field.getName();
      if (name == null || name.isEmpty()) {
        errors.add(new ValidationError(FIELDS_PROPERTY, "Field name is required"));
      } else {
        if (name.length() > SOPFIELD_NAME_MAX) {
          errors.add(
              new ValidationError(FIELDS_PROPERTY, "Field name can be at most " + SOPFIELD_NAME_MAX + " characters"));
        }
        String fieldName = name.toLowerCase(Locale.ROOT);
        if (fieldNames.contains(fieldName)) {
          errors.add(new ValidationError(FIELDS_PROPERTY, "Field names must be unique"));
        } else {
          fieldNames.add(fieldName);
        }
      }

      String units = field.getUnits();
      if (units != null && units.length() > SOPFIELD_UNITS_MAX) {
        errors.add(new ValidationError(FIELDS_PROPERTY, "Units can be at most " + SOPFIELD_UNITS_MAX + " characters"));
      }

      if (field.getFieldType() == null) {
        errors.add(new ValidationError(FIELDS_PROPERTY, "Field type is required"));
      } else if (field.getFieldType() == SopField.FieldType.INSTRUMENT) {
        if (field.getInstrumentModel() == null) {
          errors.add(new ValidationError(FIELDS_PROPERTY, "Instrument model is required for instrument fields"));
        }
      } else if (field.getInstrumentModel() != null) {
        errors.add(new ValidationError(FIELDS_PROPERTY, "Instrument model must not be set for non-instrument fields"));
      }
    }
  }

  @Override
  protected void applyChanges(Sop to, Sop from) throws IOException {
    to.setAlias(from.getAlias());
    to.setUrl(from.getUrl());
    to.setArchived(from.isArchived());

    applyFieldChanges(to, from);
  }

  private void applyFieldChanges(Sop to, Sop from) {
    Set<SopField> toFields = to.getFields();
    Set<SopField> fromFields = from.getFields();
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
      toFieldById.put(toField.getId(), toField);
    }

    for (Iterator<SopField> it = toFields.iterator(); it.hasNext();) {
      SopField toField = it.next();
      if (!fromFieldIds.contains(toField.getId())) {
        it.remove();
      }
    }

    for (SopField fromField : fromFields) {
      if (fromField.isSaved()) {
        SopField toField = toFieldById.get(fromField.getId());
        if (toField == null) {
          throw new ValidationException(
              new ValidationError(FIELDS_PROPERTY, "The submitted field ID does not match an existing field"));
        }
        toField.setName(fromField.getName());
        toField.setUnits(fromField.getUnits());
      } else {
        SopField newField = new SopField();
        newField.setSop(to);
        newField.setName(fromField.getName());
        newField.setUnits(fromField.getUnits());
        newField.setFieldType(fromField.getFieldType());
        newField.setInstrumentModel(fromField.getInstrumentModel());
        toFields.add(newField);
      }
    }
  }

  @Override
  protected void beforeSave(Sop object) throws IOException {
    object.getFields().forEach(field -> field.setSop(object));
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
