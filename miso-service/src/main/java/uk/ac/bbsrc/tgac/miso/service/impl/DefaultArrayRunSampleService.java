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
    return arrayRunSampleDao.get(run, position);
  }

  @Override
  public List<ArrayRunSample> listByRunId(long arrayRunId) throws IOException {
    ArrayRun run = arrayRunService.get(arrayRunId);
    if (run == null || run.getArray() == null) {
      return Collections.emptyList();
    }

    Array array = run.getArray();
    List<ArrayRunSample> existing = arrayRunSampleDao.listByRunId(arrayRunId);
    Map<String, Sample> samples = array.getSamples();
    if (samples == null || samples.isEmpty()) {
      if (!existing.isEmpty()) {
        throw new IllegalStateException(
            String.format("Array run %d has saved samples, but array %d has no samples", arrayRunId, array.getId()));
      }
      return Collections.emptyList();
    }

    Map<String, ArrayRunSample> byPosition = existing.stream()
        .filter(item -> item.getArray() != null && item.getArray().getId() == array.getId())
        .collect(Collectors.toMap(ArrayRunSample::getPosition, sample -> sample));

    List<ArrayRunSample> results = new ArrayList<>(samples.size());
    for (Map.Entry<String, Sample> entry : samples.entrySet()) {
      ArrayRunSample item = byPosition.get(entry.getKey());
      if (item == null) {
        item = arrayRunSampleDao.get(run, entry.getKey());
      }
      item.setArray(array);
      item.setSample(entry.getValue());
      results.add(item);
    }

    results.sort(Comparator.comparing(ArrayRunSample::getPosition));
    return results;
  }

  @Override
  public void save(List<ArrayRunSample> arrayRunSamples) throws IOException {
    for (ArrayRunSample sample : arrayRunSamples) {
      save(sample);
    }
  }

  @Override
  public void save(ArrayRunSample arrayRunSample) throws IOException {
    loadChildEntity(arrayRunSample::setArrayRun, arrayRunSample.getArrayRun(), arrayRunService, "arrayRunId");
    ArrayRun run = arrayRunSample.getArrayRun();
    Array array = run.getArray();
    List<ValidationError> errors = new ArrayList<>();

    if (array == null) {
      errors.add(new ValidationError("arrayRunId", "Array Run has no array"));
    } else {
      String position = arrayRunSample.getPosition();
      if (position == null || !array.isPositionValid(position)) {
        errors.add(new ValidationError("position", "Invalid array position"));
      } else {
        Sample expectedSample = array.getSample(position);
        if (expectedSample == null) {
          errors.add(new ValidationError("position", "No Sample at this position"));
        } else if (arrayRunSample.getSample() == null || arrayRunSample.getSample().getId() != expectedSample.getId()) {
          errors.add(new ValidationError("sampleId", "Sample does not match array position"));
        }
      }
    }

    loadChildEntity(arrayRunSample::setQcStatus, arrayRunSample.getQcStatus(), runItemQcStatusService, "qcStatusId");

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }

    String position = arrayRunSample.getPosition();
    Sample expectedSample = array.getSample(position);
    ArrayRunSample managed = arrayRunSampleDao.get(run, position);
    managed.setArray(array);
    managed.setSample(expectedSample);

    User user = authorizationManager.getCurrentUser();
    updateQcDetails(arrayRunSample, managed, ArrayRunSample::getQcStatus, ArrayRunSample::getQcUser,
        ArrayRunSample::setQcUser, authorizationManager, ArrayRunSample::getQcDate, ArrayRunSample::setQcDate);

    errors = new ArrayList<>();
    validateQcUser(arrayRunSample.getQcStatus(), arrayRunSample.getQcUser(), errors);
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }

    managed.setQcStatus(arrayRunSample.getQcStatus());
    managed.setQcNote(arrayRunSample.getQcNote());
    managed.setQcUser(arrayRunSample.getQcUser());
    managed.setQcDate(arrayRunSample.getQcDate());
    managed.setLastModifier(user);
    arrayRunSampleDao.save(managed);
  }
}
