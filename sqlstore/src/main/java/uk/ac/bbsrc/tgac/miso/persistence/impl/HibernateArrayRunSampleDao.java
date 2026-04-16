package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample.ArrayRunSampleId;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample_;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun_;
import uk.ac.bbsrc.tgac.miso.core.data.Array_;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayRunSampleDao;


@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateArrayRunSampleDao implements ArrayRunSampleDao {

  @PersistenceContext
  private EntityManager entityManager;

  private Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  @Override
  public ArrayRunSample get(ArrayRun run, Array array, String position, Sample sample) throws IOException {
    if (run == null || array == null || sample == null || position == null) {
      return null;
    }

    ArrayRun persistedRun = currentSession().get(ArrayRun.class, run.getId());
    if (persistedRun == null || persistedRun.getArray() == null) {
      return null;
    }

    Array persistedArray = persistedRun.getArray();
    if (persistedArray.getId() != array.getId()) {
      return null;
    }

    if (!persistedArray.isPositionValid(position)) {
      return null;
    }

    Sample expectedSample = persistedArray.getSample(position);
    if (expectedSample == null || expectedSample.getId() != sample.getId()) {
      return null;
    }

    ArrayRunSampleId id = new ArrayRunSampleId();
    id.setArrayRun(persistedRun);
    id.setArray(persistedArray);
    id.setPosition(position);
    id.setSample(expectedSample);

    ArrayRunSample result = currentSession().get(ArrayRunSample.class, id);
    if (result == null) {
      result = new ArrayRunSample(persistedRun, persistedArray, position, expectedSample);
    }
    return result;
  }

  @Override
  public List<ArrayRunSample> listByRunId(long arrayRunId) throws IOException {
    ArrayRun persistedRun = currentSession().get(ArrayRun.class, arrayRunId);
    if (persistedRun == null || persistedRun.getArray() == null) {
      return new ArrayList<>();
    }

    Array persistedArray = persistedRun.getArray();
    Map<String, Sample> samples = persistedArray.getSamples();
    if (samples == null || samples.isEmpty()) {
      return new ArrayList<>();
    }

    List<ArrayRunSample> existing = getStoredByRunId(arrayRunId);

    List<ArrayRunSample> results = new ArrayList<>(samples.size());
    for (Map.Entry<String, Sample> entry : samples.entrySet()) {
      String position = entry.getKey();
      Sample expectedSample = entry.getValue();

      ArrayRunSample existingItem = existing.stream()
          .filter(item -> position.equals(item.getPosition()))
          .findFirst()
          .orElse(null);

      if (existingItem != null) {
        results.add(existingItem);
      } else {
        results.add(new ArrayRunSample(persistedRun, persistedArray, position, expectedSample));
      }
    }

    return results;
  }

  @Override
  public void save(ArrayRunSample arrayRunSample) throws IOException {
    currentSession().saveOrUpdate(arrayRunSample);
  }

  @Override
  public void delete(ArrayRunSample arrayRunSample) throws IOException {
    ArrayRunSample managed = currentSession().get(ArrayRunSample.class, new ArrayRunSampleId(
        arrayRunSample.getArrayRun(),
        arrayRunSample.getArray(),
        arrayRunSample.getPosition(),
        arrayRunSample.getSample()));

    if (managed != null) {
      currentSession().remove(managed);
      // Flush so this delete is written before the same transaction updates the parent row.
      currentSession().flush();
    }
  }

  @Override
  public void deleteByRunId(long arrayRunId) throws IOException {
    for (ArrayRunSample item : getStoredByRunId(arrayRunId)) {
      currentSession().remove(item);
    }
    // Flush so this delete is written before the same transaction updates the parent row.
    currentSession().flush();
  }

  private List<ArrayRunSample> getStoredByRunId(long arrayRunId) {
    QueryBuilder<ArrayRunSample, ArrayRunSample> builder =
        new QueryBuilder<>(currentSession(), ArrayRunSample.class, ArrayRunSample.class);
    Join<ArrayRunSample, ArrayRun> runJoin = builder.getJoin(builder.getRoot(), ArrayRunSample_.arrayRun);
    builder.addPredicate(builder.getCriteriaBuilder().equal(runJoin.get(ArrayRun_.id), arrayRunId));
    return builder.getResultList();
  }

  @Override
  public List<ArrayRunSample> listByArrayId(long arrayId) throws IOException {
    QueryBuilder<ArrayRunSample, ArrayRunSample> builder =
        new QueryBuilder<>(currentSession(), ArrayRunSample.class, ArrayRunSample.class);
    Join<ArrayRunSample, Array> arrayJoin = builder.getJoin(builder.getRoot(), ArrayRunSample_.array);
    builder.addPredicate(builder.getCriteriaBuilder().equal(arrayJoin.get(Array_.id), arrayId));
    return builder.getResultList();
  }

}
