package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.persistence.ReferenceGenomeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateReferenceGenomeDao implements ReferenceGenomeDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateReferenceGenomeDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public Collection<ReferenceGenome> listAllReferenceGenomeTypes() {
    Query query = currentSession().createQuery("from ReferenceGenomeImpl");
    List<ReferenceGenome> records = query.list();
    return records;
  }

}
