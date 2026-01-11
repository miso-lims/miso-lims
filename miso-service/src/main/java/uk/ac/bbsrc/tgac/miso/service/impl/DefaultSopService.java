package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.SopDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.isChanged;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultSopService extends AbstractSaveService<Sop> implements SopService {

  private static final int SOPFIELD_NAME_MAX = 255;
  private static final int SOPFIELD_UNITS_MAX = 50;

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
      errors.add(new ValidationError("category", "Category is required"));
    }

    // Duplicate check when any uniqueness component changes
    if (sop.getCategory() != null
        && (isChanged(Sop::getCategory, sop, beforeChange)
            || isChanged(Sop::getAlias, sop, beforeChange)
            || isChanged(Sop::getVersion, sop, beforeChange))) {

      Sop existing = sopDao.get(sop.getCategory(), sop.getAlias(), sop.getVersion());
      if (existing != null && existing.getId() != sop.getId()) {
        errors.add(ValidationError.forDuplicate("SOP", null, "alias and version"));
      }
    }

    validateSopFields(sop, errors);
  }

  @Override
  protected void applyChanges(Sop to, Sop from) throws IOException {
    to.setAlias(from.getAlias());
    to.setVersion(from.getVersion());
    to.setCategory(from.getCategory());

    to.setUrl(from.getUrl());
    to.setArchived(from.isArchived());

    to.setSopFields(cloneSopFieldsForSave(from, to));
  }

  private Set<SopField> cloneSopFieldsForSave(Sop from, Sop targetSop) {
    Set<SopField> result = new HashSet<>();
    if (from.getSopFields() == null || from.getSopFields().isEmpty()) {
      return result;
    }

    Map<Long, SopField> existingById = new HashMap<>();
    Map<String, SopField> existingByName = new HashMap<>();

    if (targetSop.getSopFields() != null) {
      for (SopField existing : targetSop.getSopFields()) {
        if (existing == null)
          continue;

        if (existing.getId() != null) {
          existingById.put(existing.getId(), existing);
        }

        if (existing.getName() != null) {
          String key = existing.getName().trim().toLowerCase(Locale.ROOT);
          if (!key.isEmpty()) {
            existingByName.put(key, existing);
          }
        }
      }
    }

    // Prevent duplicates in incoming payload (by normalized name)
    Set<String> seenIncomingNames = new HashSet<>();

    for (SopField src : from.getSopFields()) {
      if (src == null)
        continue;

      String name = src.getName() == null ? "" : src.getName().trim();
      if (name.isEmpty())
        continue;

      String nameKey = name.toLowerCase(Locale.ROOT);
      if (!seenIncomingNames.add(nameKey)) {
        // skip duplicates (validation also covers this)
        continue;
      }

      SopField dest = new SopField();
      Long id = src.getId();
      if (id != null) {
        SopField existing = existingById.get(id);
        if (existing != null) {
          id = existing.getId();
        }
      } else {
        SopField existing = existingByName.get(nameKey);
        if (existing != null) {
          id = existing.getId();
        }
      }

      dest.setId(id);
      dest.setName(name);
      dest.setUnits(src.getUnits());
      dest.setFieldType(src.getFieldType());
      dest.setSop(targetSop);

      result.add(dest);
    }

    return result;
  }

  private void validateSopFields(Sop sop, List<ValidationError> errors) {
    if (sop.getSopFields() == null || sop.getSopFields().isEmpty()) {
      return;
    }

    Set<String> fieldNames = new HashSet<>();
    for (SopField field : sop.getSopFields()) {
      if (field == null) {
        errors.add(new ValidationError("sopFields", "SOP field cannot be null"));
        continue;
      }

      String name = field.getName() == null ? null : field.getName().trim();
      if (name == null || name.isEmpty()) {
        errors.add(new ValidationError("sopFields.name", "SOP field name cannot be empty"));
        continue;
      }
      if (name.length() > SOPFIELD_NAME_MAX) {
        errors.add(new ValidationError("sopFields.name",
            "SOP field name is too long (max " + SOPFIELD_NAME_MAX + "): " + name));
      }

      String normalizedName = name.toLowerCase(Locale.ROOT);
      if (!fieldNames.add(normalizedName)) {
        errors.add(new ValidationError("sopFields.name", "Duplicate field name: " + name));
      }

      if (field.getUnits() != null && field.getUnits().trim().length() > SOPFIELD_UNITS_MAX) {
        errors.add(new ValidationError("sopFields.units",
            "Units is too long for field '" + name + "' (max " + SOPFIELD_UNITS_MAX + ")"));
      }

      if (field.getFieldType() == null) {
        errors.add(new ValidationError("sopFields.fieldType", "Field type is required for field: " + name));
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
