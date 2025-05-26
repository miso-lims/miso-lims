package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayRunStore;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultArrayRunService implements ArrayRunService {

  private static final String FIELD_COMPLETIONDATE = "completionDate";

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private ArrayRunStore arrayRunStore;

  @Autowired
  private ArrayService arrayService;

  @Autowired
  private InstrumentService instrumentService;

  @Autowired
  private SampleService sampleService;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public ArrayRunStore getArrayRunStore() {
    return arrayRunStore;
  }

  public void setArrayRunStore(ArrayRunStore arrayRunStore) {
    this.arrayRunStore = arrayRunStore;
  }

  public ArrayService getArrayService() {
    return arrayService;
  }

  public void setArrayService(ArrayService arrayService) {
    this.arrayService = arrayService;
  }

  public InstrumentService getInstrumentService() {
    return instrumentService;
  }

  public void setInstrumentService(InstrumentService instrumentService) {
    this.instrumentService = instrumentService;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return arrayRunStore.count(errorHandler, filter);
  }

  @Override
  public List<ArrayRun> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return arrayRunStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public List<ArrayRun> listByArrayId(long arrayId) throws IOException {
    return arrayRunStore.listByArrayId(arrayId);
  }

  @Override
  public List<ArrayRun> listBySampleId(long sampleId) throws IOException {
    return arrayRunStore.listBySampleId(sampleId);
  }

  @Override
  public ArrayRun get(long arrayRunId) throws IOException {
    return arrayRunStore.get(arrayRunId);
  }

  @Override
  public long create(ArrayRun arrayRun) throws IOException {
    loadChildEntities(arrayRun);
    arrayRun.setChangeDetails(authorizationManager.getCurrentUser());
    validateChange(arrayRun, null);
    return arrayRunStore.create(arrayRun);
  }

  @Override
  public long update(ArrayRun arrayRun) throws IOException {
    loadChildEntities(arrayRun);
    ArrayRun managed = get(arrayRun.getId());
    validateChange(arrayRun, managed);
    applyChanges(arrayRun, managed);
    managed.setChangeDetails(authorizationManager.getCurrentUser());
    return arrayRunStore.update(managed);
  }

  @Override
  public List<ArrayRun> listBySamplesIds(List<Long> sampleIds) throws IOException {
    return arrayRunStore.listBySamplesIds(sampleIds);
  }



  /**
   * Checks submitted data for validity, throwing a ValidationException containing all of the errors
   * if invalid
   * 
   * @param arrayRun submitted Array Run to validate
   * 
   * @param beforeChange the already-persisted Array Run before changes
   * 
   * @throws IOException
   */
  private void validateChange(ArrayRun arrayRun, ArrayRun beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (arrayRun.isSaved() && beforeChange == null) {
      errors.add(new ValidationError("Array Run not found"));
    }
    if (isStringEmptyOrNull(arrayRun.getAlias())) {
      errors.add(new ValidationError("alias", "Alias cannot be blank"));
    }
    if (arrayRun.getInstrument() == null) {
      errors.add(new ValidationError("instrument", "An instrument must be selected"));
    } else if (arrayRun.getInstrument().getInstrumentModel().getInstrumentType() != InstrumentType.ARRAY_SCANNER) {
      errors.add(new ValidationError("instrument", "Instrument must be an array scanner"));
    }

    if (beforeChange == null || !arrayRun.getAlias().equals(beforeChange.getAlias())) {
      ArrayRun duplicateAlias = arrayRunStore.getByAlias(arrayRun.getAlias());
      if (duplicateAlias != null) {
        errors.add(new ValidationError("alias", "There is already an array run with this alias"));
      }
    }

    if (arrayRun.getArray() != null && arrayRun.getArray().getAlias() == null) {
      errors.add(new ValidationError("array", "Array not found"));
    }

    validateStartDate(arrayRun, beforeChange, errors);
    validateStatusAndCompletion(arrayRun, beforeChange, errors);

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void validateStartDate(ArrayRun arrayRun, ArrayRun beforeChange, List<ValidationError> errors)
      throws IOException {
    if (arrayRun.getStartDate() == null) {
      errors.add(new ValidationError("startDate", "Start date cannot be blank"));
    } else if (!authorizationManager.isAdminUser() && beforeChange != null && beforeChange.getStartDate() != null
        && !arrayRun.getStartDate().equals(beforeChange.getStartDate())) {
      errors.add(new ValidationError("startDate", "Only admin may change start date"));
    }
  }

  private void validateStatusAndCompletion(ArrayRun arrayRun, ArrayRun beforeChange, List<ValidationError> errors)
      throws IOException {
    if (arrayRun.getHealth() == null) {
      errors.add(new ValidationError("status", "A status must be selected"));
    } else if (arrayRun.getHealth().isDone()) {
      if (arrayRun.getCompletionDate() == null) {
        errors.add(new ValidationError(FIELD_COMPLETIONDATE, "Completion date must be entered for a completed run"));
      } else if (!authorizationManager.isAdminUser() && beforeChange != null && beforeChange.getCompletionDate() != null
          && !arrayRun.getCompletionDate().equals(beforeChange.getCompletionDate())) {
        errors
            .add(new ValidationError(FIELD_COMPLETIONDATE, "Only admin may change completion date of a completed run"));
      }
    } else if (arrayRun.getCompletionDate() != null) {
      errors.add(new ValidationError(FIELD_COMPLETIONDATE, "Cannot set completion date for incomplete run"));
    }
  }

  private void loadChildEntities(ArrayRun arrayRun) throws IOException {
    if (arrayRun.getInstrument() != null) {
      arrayRun.setInstrument(instrumentService.get(arrayRun.getInstrument().getId()));
    }

    if (arrayRun.getArray() != null) {
      Array array = arrayService.get(arrayRun.getArray().getId());
      if (array == null) {
        // providing a way to detect "array not found" in validation
        arrayRun.getArray().setAlias(null);
      } else {
        arrayRun.setArray(array);
      }
    }
  }

  private void applyChanges(ArrayRun from, ArrayRun to) {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setFilePath(from.getFilePath());
    to.setArray(from.getArray());
    to.setHealth(from.getHealth());
    to.setStartDate(from.getStartDate());
    to.setCompletionDate(from.getCompletionDate());
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public void authorizeDeletion(ArrayRun object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  // maybe change type, we're getting the children type (should be fine, we just want the IDs)
  // pass in the sample ID and the requisition's ID --- this is the effective requisition for the
  // children, if this approach doesn't work fix later
  // this can be simplified --- use getchildren
  public List<Long> getSamplesDescendantslList(List<Long> sampleIDs, long requisitonId) throws IOException {

    // this is only used for detailed sample mode -- you don't need it otherwise


    // REVIEW THIS
    // this may need to be modified to only get the aliquots, and add the sample IDs only if they are
    // aliquots as well


    // get kids
    // for each kid, get it's kids recursively until no more kids down the chain
    // add all to a single list
    ArrayList<Long> familyTree = new ArrayList<Long>();


    // adds any current samples that are aliquots
    for (long id : sampleIDs) {
      Sample currSample = sampleService.get(id);
      if (currSample.getSampleType().equals("Aliquot"))
        familyTree.add(id);
    }

    // gets the derived aliquots from the samples
    List<Sample> aliquots = sampleService.getChildren(sampleIDs, "Aliquot", requisitonId);
    // use the getChildren method -- quite a lot of db querying has already been done for you


    familyTree.addAll(aliquots.stream().map(Sample::getId).collect(Collectors.toList())); // adds all the aliquots to
                                                                                          // the family tree

    return familyTree;

  }

}
