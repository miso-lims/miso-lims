package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.persistence.IdentityDao;

@Repository
@Transactional
public class HibernateIdentityDao implements IdentityDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateIdentityDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<Identity> getIdentity() {
    Query query = currentSession().createQuery("from IdentityImpl");
    @SuppressWarnings("unchecked")
    List<Identity> records = query.list();
    return records;
  }

  @Override
  public Identity getIdentity(Long id) {
    return (Identity) currentSession().get(IdentityImpl.class, id);
  }

  @Override
  public Long addIdentity(Identity identity) {
    Date now = new Date();
    identity.setCreationDate(now);
    identity.setLastUpdated(now);
    return (Long) currentSession().save(identity);
  }

  @Override
  public void deleteIdentity(Identity identity) {
    currentSession().delete(identity);

  }

  @Override
  public void update(Identity identity) {
    Date now = new Date();
    identity.setLastUpdated(now);
    currentSession().update(identity);
  }

}
