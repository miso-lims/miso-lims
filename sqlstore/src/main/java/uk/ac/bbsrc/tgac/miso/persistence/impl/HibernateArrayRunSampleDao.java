package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

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
    if (persistedRun.getArray().getId() != array.getId()) {
      return null;
    }
    if (!array.isPositionValid(position)) {
      return null;
    }

    Sample expectedSample = array.getSample(position);
    if (expectedSample == null || expectedSample.getId() != sample.getId()) {
      return null;
    }

    ArrayRunSampleId id = new ArrayRunSampleId();
    id.setArrayRun(persistedRun);
    id.setArray(array);
    id.setPosition(position);
    id.setSample(sample);

    ArrayRunSample result = currentSession().get(ArrayRunSample.class, id);
    if (result == null) {
      result = new ArrayRunSample(persistedRun, array, position, sample);
    }
    return result;
  }

  @Override
  public List<ArrayRunSample> listByRunId(long arrayRunId) throws IOException {
    QueryBuilder<ArrayRunSample, ArrayRunSample> builder =
        new QueryBuilder<>(currentSession(), ArrayRunSample.class, ArrayRunSample.class);
    Join<ArrayRunSample, ArrayRun> runJoin = builder.getJoin(builder.getRoot(), ArrayRunSample_.arrayRun);
    builder.addPredicate(builder.getCriteriaBuilder().equal(runJoin.get(ArrayRun_.id), arrayRunId));
    return builder.getResultList();
  }

  @Override
  public void save(ArrayRunSample arrayRunSample) throws IOException {
    currentSession().saveOrUpdate(arrayRunSample);
  }

  @Override
  public void delete(ArrayRunSample arrayRunSample) throws IOException {
    currentSession().createMutationQuery(
        "delete from ArrayRunSample where arrayRun.id = :runId and array.id = :arrayId and position = :position and sample.id = :sampleId")
        .setParameter("runId", arrayRunSample.getArrayRun().getId())
        .setParameter("arrayId", arrayRunSample.getArray().getId())
        .setParameter("position", arrayRunSample.getPosition())
        .setParameter("sampleId", arrayRunSample.getSample().getId())
        .executeUpdate();
  }

  @Override
  public void deleteByRunId(long arrayRunId) throws IOException {
    currentSession().createMutationQuery("delete from ArrayRunSample where arrayRun.id = :runId")
        .setParameter("runId", arrayRunId)
        .executeUpdate();
  }
}
