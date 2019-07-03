package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPositionStatus;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentStatus;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.persistence.InstrumentStatusStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateInstrumentStatusDao implements InstrumentStatusStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateQcTypeDao.class);

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
    Criteria criteria = currentSession().createCriteria(InstrumentPositionStatus.class);
    @SuppressWarnings("unchecked")
    List<InstrumentPositionStatus> positions = criteria.list();
    return map(positions);
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private List<InstrumentStatus> map(List<InstrumentPositionStatus> positions) {
    Map<Long, InstrumentStatus> statusByInstrumentId = new HashMap<>();
    for (InstrumentPositionStatus position : positions) {
      if (!statusByInstrumentId.containsKey(position.getInstrument().getId())) {
        InstrumentStatus status = new InstrumentStatus();
        status.setInstrument(position.getInstrument());
        statusByInstrumentId.put(position.getInstrument().getId(), status);
      }
      InstrumentStatus existing = statusByInstrumentId.get(position.getInstrument().getId());
      if (position.getPosition() == null) {
        InstrumentPosition nullPosition = new InstrumentPosition();
        nullPosition.setAlias("n/a");
        nullPosition.setInstrumentModel(position.getInstrument().getInstrumentModel());
        position.setPosition(nullPosition);
      }
      if (existing.getPositions().containsKey(position.getPosition())) {
        // there are likely multiple runs with the same start date
        Run moreRecent = getMoreRecentRun(existing.getPositions().get(position.getPosition()), position.getRun());
        existing.getPositions().put(position.getPosition(), moreRecent);
      } else {
        existing.getPositions().put(position.getPosition(), position.getRun());
      }
    }
    return new ArrayList<>(statusByInstrumentId.values());
  }

  /**
   * Determines which of two Runs is more recent
   * 
   * @param run1
   * @param run2
   * @return the more recent Run, determined by comparing startDate, then completionDate, then lastModified
   */
  private Run getMoreRecentRun(Run run1, Run run2) {
    // compare startDates
    if (run1.getStartDate().before(run2.getStartDate())) {
      return run2;
    } else if (run1.getStartDate().after(run2.getStartDate())) {
      return run1;
    }
    // same start date. check if one is active
    if (run1.getCompletionDate() == null) {
      if (run2.getCompletionDate() == null) {
        // neither complete. compare lastModified
        return lastModified(run1, run2);
      } else {
        return run1;
      }
    } else if (run2.getCompletionDate() == null) {
      return run2;
    }
    // both complete. compare completionDates
    if (run1.getCompletionDate().before(run2.getCompletionDate())) {
      return run2;
    } else if (run1.getCompletionDate().after(run2.getCompletionDate())) {
      return run1;
    }
    // same completion dates. compare lastModified
    return lastModified(run1, run2);
  }

  /**
   * Determines which of two Runs was more recently modified
   * 
   * @param run1
   * @param run2
   * @return the Run with the later lastModified date. Arbitrarily returns run1 if they are equal
   */
  private Run lastModified(Run run1, Run run2) {
    return run1.getLastModified().before(run2.getLastModified()) ? run2 : run1;
  }

}
