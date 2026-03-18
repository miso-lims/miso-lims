package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.loadChildEntity;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.updateQcDetails;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.validateQcUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayRunSampleService;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.core.service.RunItemQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayRunSampleDao;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultArrayRunSampleService implements ArrayRunSampleService {

  @Autowired
  private ArrayRunSampleDao arrayRunSampleDao;

  @Autowired
  private ArrayRunService arrayRunService;

  @Autowired
  private RunItemQcStatusService runItemQcStatusService;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public ArrayRunSample get(ArrayRun run, String position) throws IOException {
    if (run == null || run.getArray() == null || position == null || !run.getArray().isPositionValid(position)) {
      return null;
    }
    Sample sample = run.getArray().getSample(position);
    if (sample == null) {
      return null;
    }
    return arrayRunSampleDao.get(run, run.getArray(), position, sample);
  }

  @Override
  public List<ArrayRunSample> listByRunId(long arrayRunId) throws IOException {
    ArrayRun run = arrayRunService.get(arrayRunId);
    if (run == null) {
      throw new IllegalArgumentException("Array run not found: " + arrayRunId);
    }
    if (run.getArray() == null) {
      return Collections.emptyList();
    }

    Array array = run.getArray();
    List<ArrayRunSample> existing = arrayRunSampleDao.listByRunId(arrayRunId);
    Map<String, Sample> samples = array.getSamples();
    if (samples == null || samples.isEmpty()) {
      return Collections.emptyList();
    }

    Map<String, ArrayRunSample> byPosition = existing.stream()
        .filter(item -> item.getArray() != null && item.getArray().getId() == array.getId())
        .collect(Collectors.toMap(ArrayRunSample::getPosition, sample -> sample));

    List<ArrayRunSample> results = new ArrayList<>(samples.size());
    for (Map.Entry<String, Sample> entry : samples.entrySet()) {
      ArrayRunSample item = byPosition.get(entry.getKey());
      if (item == null) {
        item = arrayRunSampleDao.get(run, array, entry.getKey(), entry.getValue());
      }
      results.add(item);
    }

    results.sort(Comparator.comparing(ArrayRunSample::getPosition));
    return results;
  }

  @Override
  public void delete(ArrayRunSample arrayRunSample) throws IOException {
    arrayRunSampleDao.delete(arrayRunSample);
  }

  @Override
  public void deleteByRunId(long arrayRunId) throws IOException {
    arrayRunSampleDao.deleteByRunId(arrayRunId);
  }

  @Override
  public void save(List<ArrayRunSample> arrayRunSamples) throws IOException {
    for (ArrayRunSample sample : arrayRunSamples) {
      save(sample);
    }
  }

  @Override
  public void save(ArrayRunSample arrayRunSample) throws IOException {
    loadChildEntities(arrayRunSample);
    validateChange(arrayRunSample);
    Sample expectedSample = arrayRunSample.getArrayRun().getArray().getSample(arrayRunSample.getPosition());
    ArrayRunSample managed = arrayRunSampleDao.get(arrayRunSample.getArrayRun(), arrayRunSample.getArrayRun().getArray(),
        arrayRunSample.getPosition(), expectedSample);
    User user = authorizationManager.getCurrentUser();
    updateQcDetails(arrayRunSample, managed, ArrayRunSample::getQcStatus, ArrayRunSample::getQcUser,
        ArrayRunSample::setQcUser, authorizationManager, ArrayRunSample::getQcDate, ArrayRunSample::setQcDate);

    List<ValidationError> errors = new ArrayList<>();
    validateQcUser(arrayRunSample.getQcStatus(), arrayRunSample.getQcUser(), errors);
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }

    applyChanges(arrayRunSample, managed, user);
    arrayRunSampleDao.save(managed);
  }

  private void loadChildEntities(ArrayRunSample arrayRunSample) throws IOException {
    loadChildEntity(arrayRunSample::setArrayRun, arrayRunSample.getArrayRun(), arrayRunService, "arrayRunId");
    loadChildEntity(arrayRunSample::setQcStatus, arrayRunSample.getQcStatus(), runItemQcStatusService, "qcStatusId");
  }

  private void validateChange(ArrayRunSample arrayRunSample) {
    List<ValidationError> errors = new ArrayList<>();
    ArrayRun run = arrayRunSample.getArrayRun();
    Array array = run.getArray();

    if (array == null) {
      errors.add(new ValidationError("arrayRunId", "Array run has no array"));
    } else {
      if (arrayRunSample.getArray() == null || arrayRunSample.getArray().getId() != array.getId()) {
        errors.add(new ValidationError("arrayId", "Array does not match array run"));
      }
      String position = arrayRunSample.getPosition();
      if (position == null || !array.isPositionValid(position)) {
        errors.add(new ValidationError("position", "Invalid array position"));
      } else {
        Sample expectedSample = array.getSample(position);
        if (expectedSample == null) {
          errors.add(new ValidationError("position", "No sample at this position"));
        } else if (arrayRunSample.getSample() == null || arrayRunSample.getSample().getId() != expectedSample.getId()) {
          errors.add(new ValidationError("sampleId", "Sample does not match array position"));
        }
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(ArrayRunSample from, ArrayRunSample to, User user) {
    to.setQcStatus(from.getQcStatus());
    to.setQcNote(from.getQcNote());
    to.setQcUser(from.getQcUser());
    to.setQcDate(from.getQcDate());
    to.setLastModifier(user);
  }
}
