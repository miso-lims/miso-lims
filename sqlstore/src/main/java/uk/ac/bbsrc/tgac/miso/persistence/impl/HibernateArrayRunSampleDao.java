package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.checkerframework.checker.units.qual.A;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample.ArrayRunSampleId;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample_;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun_;
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
    public ArrayRunSample get(ArrayRun run, String position) throws IOException {
        ArrayRunSampleId id = new ArrayRunSampleId();
        id.setArrayRun(run);
        id.setPosition(position);
        ArrayRunSample result = (ArrayRunSample) currentSession().get(ArrayRunSample.class, id);
        if(result == null){
            result = new ArrayRunSample(run, position);
        }
        return result;
    }

    @Override
    public List<ArrayRunSample> listByRunId(long arrayRunId) throws IOException {
        QueryBuilder<ArrayRunSample, ArrayRunSample> builder = new QueryBuilder<>(currentSession(), ArrayRunSample.class, ArrayRunSample.class);
        Join<ArrayRunSample, ArrayRun> runJoin = builder.getJoin(builder.getRoot(), ArrayRunSample_.arrayRun);
        builder.addPredicate(builder.getCriteriaBuilder().equal(runJoin.get(ArrayRun_.id), arrayRunId));
        return builder.getResultList();
    }

    @Override
    public void save(ArrayRunSample arrayRunSample) throws IOException {
        currentSession().saveOrUpdate(arrayRunSample);
    }
}
