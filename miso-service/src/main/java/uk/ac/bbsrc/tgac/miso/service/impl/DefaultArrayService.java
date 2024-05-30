package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.ArrayChangeLog;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayModelService;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayService;
import uk.ac.bbsrc.tgac.miso.core.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayStore;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultArrayService implements ArrayService {

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private ArrayStore arrayStore;

  @Autowired
  private ArrayRunService arrayRunService;

  @Autowired
  private ArrayModelService arrayModelService;

  @Autowired
  private SampleService sampleService;

  @Autowired
  private ChangeLogService changeLogService;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public ArrayStore getArrayStore() {
    return arrayStore;
  }

  public void setArrayStore(ArrayStore arrayStore) {
    this.arrayStore = arrayStore;
  }

  public SampleService getSampleService() {
    return sampleService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return arrayStore.count(errorHandler, filter);
  }

  @Override
  public List<Array> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter)
      throws IOException {
    return arrayStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public List<Array> listBySampleId(long sampleId) throws IOException {
    return arrayStore.listBySampleId(sampleId);
  }

  @Override
  public Array get(long arrayId) throws IOException {
    return arrayStore.get(arrayId);
  }

  @Override
  public long create(Array array) throws IOException {
    loadChildEntities(array);
    array.setChangeDetails(authorizationManager.getCurrentUser());
    validateChange(array, null);
    return arrayStore.create(array);
  }

  @Override
  public long update(Array array) throws IOException {
    loadChildEntities(array);
    Array managed = get(array.getId());
    validateChange(array, managed);
    managed.setChangeDetails(authorizationManager.getCurrentUser());
    applyChanges(array, managed);
    return arrayStore.update(managed);
  }

  private void loadChildEntities(Array array) throws IOException {
    if (array.getArrayModel() != null) {
      array.setArrayModel(arrayModelService.get(array.getArrayModel().getId()));
    }
    Map<String, Sample> samples = new HashMap<>();
    for (Entry<String, Sample> entry : array.getSamples().entrySet()) {
      // samples not found will end up as null mapped to a position, which will cause validation error
      samples.put(entry.getKey(), sampleService.get(entry.getValue().getId()));
    }
    array.setSamples(samples);
  }

  /**
   * Checks submitted data for validity, throwing a ValidationException containing all of the errors
   * if invalid
   * 
   * @param array submitted Array to validate
   * @param beforeChange the already-persisted Array before changes
   * @throws IOException
   */
  private void validateChange(Array array, Array beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (array.isSaved() && beforeChange == null) {
      errors.add(new ValidationError("Array not found"));
    }
    if (isStringEmptyOrNull(array.getAlias())) {
      errors.add(new ValidationError("alias", "Alias cannot be blank"));
    }
    if (isStringEmptyOrNull(array.getSerialNumber())) {
      errors.add(new ValidationError("serialNumber", "Serial number cannot be blank"));
    }
    if (array.getArrayModel() == null) {
      // was either blank or not found during #loadChildEntities
      errors.add(new ValidationError("arrayModel", "Choose a valid model"));
    }

    validateUniqueFields(array, beforeChange, errors);
    validateSamples(array, errors);

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void validateUniqueFields(Array array, Array beforeChange, List<ValidationError> errors) throws IOException {
    if (beforeChange == null || !array.getAlias().equals(beforeChange.getAlias())) {
      Array duplicateAlias = arrayStore.getByAlias(array.getAlias());
      if (duplicateAlias != null) {
        errors.add(new ValidationError("alias", "There is already an array with this alias"));
      }
    }
    if (beforeChange == null || !array.getSerialNumber().equals(beforeChange.getSerialNumber())) {
      Array duplicateSerialNumber = arrayStore.getBySerialNumber(array.getSerialNumber());
      if (duplicateSerialNumber != null) {
        errors.add(new ValidationError("serialNumber", "There is already an array with this serial number"));
      }
    }
  }

  private void validateSamples(Array array, List<ValidationError> errors) {
    array.getSamples().forEach((pos, sam) -> {
      if (sam == null) {
        // wasn't found during #loadChildEntities
        errors.add(new ValidationError("Invalid Sample in position " + pos));
      } else if (isDetailedSample(sam) && !isAliquotSample(sam)) {
        errors.add(new ValidationError("Sample in position " + pos + " must be an aliquot"));
      }
    });
  }

  private void applyChanges(Array from, Array to) throws IOException {
    to.setAlias(from.getAlias());
    to.setSerialNumber(from.getSerialNumber());
    to.setDescription(from.getDescription());

    // have to add/remove samples individually to avoid unnecessary "sample removed; sample added"
    // changelogs for non-changes
    Map<String, Sample> toSamples = to.getSamples();
    Set<String> removePositions = toSamples.keySet().stream().filter(key -> !from.getSamples().containsKey(key))
        .collect(Collectors.toSet());
    for (String key : removePositions) {
      recordRemoval(to, toSamples.get(key).getName(), key);
      toSamples.remove(key);
    }

    from.getSamples().forEach((key, val) -> {
      Sample current = toSamples.get(key);
      if (toSamples.get(key) == null || current.getId() != val.getId()) {
        toSamples.put(key, val);
      }
    });
  }

  private void recordRemoval(Array array, String sampleName, String position) throws IOException {
    ArrayChangeLog changeLog = new ArrayChangeLog();
    changeLog.setArray(array);
    changeLog.setTime(new Date());
    changeLog.setColumnsChanged("");
    changeLog.setUser(authorizationManager.getCurrentUser());
    changeLog.setSummary(String.format("%s removed from %s", sampleName, position));
    changeLogService.create(changeLog);
  }

  @Override
  public List<Sample> getArrayableSamplesBySearch(String search) throws IOException {
    return arrayStore.getArrayableSamplesBySearch(search);
  }

  @Override
  public List<Array> getArraysBySearch(String search) throws IOException {
    return arrayStore.getArraysBySearch(search);
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public void authorizeDeletion(Array object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  @Override
  public ValidationResult validateDeletion(Array object) throws IOException {
    ValidationResult result = new ValidationResult();
    int usage = arrayRunService.listByArrayId(object.getId()).size();
    if (usage > 0) {
      result.addError(new ValidationError(String.format("Array %s is used in %d array %s",
          object.getAlias(), usage, Pluralizer.runs(usage))));
    }
    return result;
  }

}
