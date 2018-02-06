package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.store.ArrayStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.ArrayService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultArrayService implements ArrayService {

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private ArrayStore arrayStore;
  
  @Autowired
  private SampleService sampleService;

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
  public List<Array> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol, PaginationFilter... filter)
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
  public long save(Array array) throws IOException {
    loadChildEntities(array);
    if (array.getId() == Array.UNSAVED_ID) {
      return create(array);
    } else {
      return update(array);
    }
  }

  private long create(Array array) throws IOException {
    setChangeDetails(array);
    validateChange(array, null);
    return arrayStore.save(array);
  }

  private long update(Array array) throws IOException {
    Array managed = get(array.getId());
    validateChange(array, managed);
    applyChanges(array, managed);
    setChangeDetails(managed);
    return arrayStore.save(managed);
  }

  private void loadChildEntities(Array array) throws IOException {
    if (array.getArrayModel() != null) {
      array.setArrayModel(getArrayModel(array.getArrayModel().getId()));
    }
    Map<String, Sample> samples = new HashMap<>();
    for (Entry<String, Sample> entry : array.getSamples().entrySet()) {
      // samples not found will end up as null mapped to a position, which will cause validation error
      samples.put(entry.getKey(), sampleService.get(entry.getValue().getId()));
    }
    array.setSamples(samples);
  }

  /**
   * Checks submitted data for validity, throwing a ValidationException containing all of the errors if invalid
   * 
   * @param array submitted Array to validate
   * @param beforeChange the already-persisted Array before changes
   * @throws IOException
   */
  private void validateChange(Array array, Array beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (array.getId() != Array.UNSAVED_ID && beforeChange == null) {
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

  private void applyChanges(Array from, Array to) {
    to.setAlias(from.getAlias());
    to.setSerialNumber(from.getSerialNumber());
    to.setDescription(from.getDescription());

    // have to add/remove samples individually to avoid unneccessary "sample removed; sample added" changelogs for non-changes
    Map<String, Sample> toSamples = to.getSamples();
    toSamples.entrySet().removeIf(entry -> !from.getSamples().containsKey(entry.getKey()));
    from.getSamples().forEach((key, val) -> {
      Sample current = toSamples.get(key);
      if (toSamples.get(key) == null || current.getId() != val.getId()) {
        toSamples.put(key, val);
      }
    });
  }

  /**
   * Updates all user data and timestamps associated with the change. Existing timestamps will be preserved
   * if the Array is unsaved, and they are already set
   * 
   * @param array the Array to update
   * @throws IOException
   */
  private void setChangeDetails(Array array) throws IOException {
    User user = authorizationManager.getCurrentUser();
    Date now = new Date();
    array.setLastModifier(user);

    if (array.getId() == Array.UNSAVED_ID) {
      array.setCreator(user);
      if (array.getCreationTime() == null) {
        array.setCreationTime(now);
      }
      if (array.getLastModified() == null) {
        array.setLastModified(now);
      }
    } else {
      array.setLastModified(now);
    }
  }

  @Override
  public ArrayModel getArrayModel(long id) throws IOException {
    return arrayStore.getArrayModel(id);
  }

  @Override
  public List<ArrayModel> listArrayModels() throws IOException {
    return arrayStore.listArrayModels();
  }

  @Override
  public Map<String, Integer> getColumnSizes() throws IOException {
    return arrayStore.getArrayColumnSizes();
  }

  @Override
  public List<Sample> getArrayableSamplesBySearch(String search) throws IOException {
    List<Sample> samples = arrayStore.getArrayableSamplesBySearch(search);
    return authorizationManager.filterUnreadable(samples);
  }

  @Override
  public List<Array> getArraysBySearch(String search) throws IOException {
    return arrayStore.getArraysBySearch(search);
  }

}
