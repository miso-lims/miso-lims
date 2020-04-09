package uk.ac.bbsrc.tgac.miso.persistence.impl.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HibernateSessionManager {

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  /**
   * Flushes and clears the current session, ensuring that all data is written to the database
   * and subsequent reads will retrieve fresh data from the database. May be required at times when
   * writing and re-reading within the same transaction, but avoid using when possible
   */
  public void flushAndClear() {
    Session session = currentSession();
    session.flush();
    session.clear();
  }

}
