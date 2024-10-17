package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPositionRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPositionRunPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPositionRunPool_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPositionRun_;
import uk.ac.bbsrc.tgac.miso.persistence.InstrumentStatusStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateInstrumentStatusDao implements InstrumentStatusStore {

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public List<InstrumentStatus> list() throws IOException {
    List<InstrumentStatus> instruments =
        new QueryBuilder<>(currentSession(), InstrumentStatus.class, InstrumentStatus.class).getResultList();

    for (InstrumentStatus instrument : instruments) {
      for (InstrumentStatusPosition position : instrument.getPositions()) {

        QueryBuilder<InstrumentStatusPositionRun, InstrumentStatusPositionRun> runBuilder =
            new QueryBuilder<>(currentSession(), InstrumentStatusPositionRun.class, InstrumentStatusPositionRun.class);
        runBuilder.addPredicate(runBuilder.getCriteriaBuilder()
            .equal(runBuilder.getRoot().get(InstrumentStatusPositionRun_.instrumentId), instrument.getId()));
        runBuilder.addPredicate(runBuilder.getCriteriaBuilder()
            .equal(runBuilder.getRoot().get(InstrumentStatusPositionRun_.positionId), position.getPositionId()));
        List<InstrumentStatusPositionRun> runs = runBuilder.getResultList(1, 0);

        if (!runs.isEmpty()) {
          InstrumentStatusPositionRun run = runs.get(0);
          position.setRun(run);

          QueryBuilder<InstrumentStatusPositionRunPool, InstrumentStatusPositionRunPool> poolBuilder =
              new QueryBuilder<>(currentSession(), InstrumentStatusPositionRunPool.class,
                  InstrumentStatusPositionRunPool.class);
          poolBuilder.addPredicate(poolBuilder.getCriteriaBuilder()
              .equal(poolBuilder.getRoot().get(InstrumentStatusPositionRunPool_.runId), run.getRunId()));
          poolBuilder.addPredicate(poolBuilder.getCriteriaBuilder()
              .equal(poolBuilder.getRoot().get(InstrumentStatusPositionRunPool_.positionId), position.getPositionId()));
          List<InstrumentStatusPositionRunPool> pools = poolBuilder.getResultList();
          run.setPools(pools);
        }
      }
    }

    return instruments;
  }

}
