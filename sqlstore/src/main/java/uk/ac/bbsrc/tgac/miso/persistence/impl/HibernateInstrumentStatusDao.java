package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPositionRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPositionRunPool;
import uk.ac.bbsrc.tgac.miso.persistence.InstrumentStatusStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateInstrumentStatusDao implements InstrumentStatusStore {

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public List<InstrumentStatus> list() throws IOException {
    @SuppressWarnings("unchecked")
    List<InstrumentStatus> instruments = currentSession().createCriteria(InstrumentStatus.class)
        .list();

    for (InstrumentStatus instrument : instruments) {
      for (InstrumentStatusPosition position : instrument.getPositions()) {
        InstrumentStatusPositionRun run = (InstrumentStatusPositionRun) currentSession()
            .createCriteria(InstrumentStatusPositionRun.class)
            .add(Restrictions.eq("instrumentId", instrument.getId()))
            .add(Restrictions.eq("positionId", position.getPositionId()))
            .setMaxResults(1)
            .uniqueResult();

        if (run != null) {
          position.setRun(run);

          @SuppressWarnings("unchecked")
          List<InstrumentStatusPositionRunPool> pools = currentSession()
              .createCriteria(InstrumentStatusPositionRunPool.class)
              .add(Restrictions.eq("runId", run.getRunId()))
              .add(Restrictions.eq("positionId", position.getPositionId()))
              .list();
          run.setPools(pools);
        }
      }
    }

    return instruments;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
