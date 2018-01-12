package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.store.ServiceRecordStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateServiceRecordDao implements ServiceRecordStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateServiceRecordDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private JdbcTemplate template;

  @Autowired
  private MisoFilesManager misoFilesManager;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(ServiceRecord ssr) throws IOException {
    long id;
    if (ssr.getId() == ServiceRecord.UNSAVED_ID) {
      if (ssr.getInstrument().getDateDecommissioned() != null)
        throw new IOException("Cannot add service records to a retired instrument!");

      id = (long) currentSession().save(ssr);
    } else {
      currentSession().update(ssr);
      id = ssr.getId();
    }
    return id;
  }

  @Override
  public ServiceRecord get(long id) throws IOException {
    return (ServiceRecord) currentSession().get(ServiceRecord.class, id);
  }

  @Override
  public List<ServiceRecord> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(ServiceRecord.class);
    @SuppressWarnings("unchecked")
    List<ServiceRecord> records = criteria.list();
    return records;
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(ServiceRecord.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public boolean remove(ServiceRecord ssr) throws IOException {
    if (ssr.isDeletable()) {
      currentSession().delete(ssr);
      ServiceRecord testIfExists = get(ssr.getId());
      if (testIfExists != null) return false;
      removeAttachments(ssr.getId());
      return true;
    } else {
      return false;
    }
  }

  /**
   * Attempts to delete all attachments associated with a service record. This method does not throw an exception upon failure to
   * delete a file, but will log any failures
   * 
   * @param recordId ID of service record to delete attachments from
   */
  private void removeAttachments(long recordId) {
    try {
      for (String filename : misoFilesManager.getFileNames(ServiceRecord.class, String.valueOf(recordId))) {
        try {
          misoFilesManager.deleteFile(ServiceRecord.class, String.valueOf(recordId), filename);
        } catch (IOException e) {
          log.error("Deleted service record " + recordId + ", but failed to delete attachment: " + filename, e);
        }
      }
    } catch (IOException e) {
      log.error("Deleted service record " + recordId + ", but failed to delete attachments", e);
    }
  }

  @Override
  public List<ServiceRecord> listByInstrumentId(long instrumentId) {
    Criteria criteria = currentSession().createCriteria(ServiceRecord.class);
    criteria.add(Restrictions.eq("instrument.id", instrumentId));
    @SuppressWarnings("unchecked")
    List<ServiceRecord> records = criteria.list();
    return records;
  }

  @Override
  public Map<String, Integer> getServiceRecordColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, "ServiceRecord");
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public void setTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setMisoFilesManager(MisoFilesManager misoFilesManager) {
    this.misoFilesManager = misoFilesManager;
  }

}
