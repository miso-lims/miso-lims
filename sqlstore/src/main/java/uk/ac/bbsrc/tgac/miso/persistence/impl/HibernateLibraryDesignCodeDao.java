package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignCodeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryDesignCodeDao implements LibraryDesignCodeDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateLibraryDesignDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public LibraryDesignCode getLibraryDesignCode(Long id) throws IOException {
    return (LibraryDesignCode) currentSession().get(LibraryDesignCode.class, id);
  }

  @Override
  public List<LibraryDesignCode> getLibraryDesignCodes() throws IOException {
    Query query = currentSession().createQuery("from LibraryDesignCode");
    @SuppressWarnings("unchecked")
    List<LibraryDesignCode> libraryDesignCodes = query.list();
    return libraryDesignCodes;
  }
}
