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

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerServiceRecordImpl;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerServiceRecordStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencerServiceRecordDao implements SequencerServiceRecordStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSequencerServiceRecordDao.class);

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
  public long save(SequencerServiceRecord ssr) throws IOException {
    long id;
    if (ssr.getId() == AbstractSequencerServiceRecord.UNSAVED_ID) {
      id = (long) currentSession().save(ssr);
    } else {
      currentSession().update(ssr);
      id = ssr.getId();
    }
    return id;
  }

  @Override
  public SequencerServiceRecord get(long id) throws IOException {
    return (SequencerServiceRecord) currentSession().get(SequencerServiceRecordImpl.class, id);
  }

  @Override
  public SequencerServiceRecord lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public List<SequencerServiceRecord> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(SequencerServiceRecordImpl.class);
    @SuppressWarnings("unchecked")
    List<SequencerServiceRecord> records = criteria.list();
    return records;
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(SequencerServiceRecordImpl.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public boolean remove(SequencerServiceRecord ssr) throws IOException {
    if (ssr.isDeletable()) {
      currentSession().delete(ssr);
      SequencerServiceRecord testIfExists = get(ssr.getId());
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
      for (String filename : misoFilesManager.getFileNames(SequencerServiceRecord.class, String.valueOf(recordId))) {
        try {
          misoFilesManager.deleteFile(SequencerServiceRecord.class, String.valueOf(recordId), filename);
        } catch (IOException e) {
          log.error("Deleted service record " + recordId + ", but failed to delete attachment: " + filename, e);
        }
      }
    } catch (IOException e) {
      log.error("Deleted service record " + recordId + ", but failed to delete attachments", e);
    }
  }

  @Override
  public List<SequencerServiceRecord> listBySequencerId(long referenceId) {
    Criteria criteria = currentSession().createCriteria(SequencerServiceRecordImpl.class);
    criteria.add(Restrictions.eq("sequencerReference.id", referenceId));
    @SuppressWarnings("unchecked")
    List<SequencerServiceRecord> records = criteria.list();
    return records;
  }

  @Override
  public Map<String, Integer> getServiceRecordColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, "SequencerServiceRecord");
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public JdbcTemplate getTemplate() {
    return template;
  }

  public void setTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public MisoFilesManager getMisoFilesManager() {
    return misoFilesManager;
  }

  public void setMisoFilesManager(MisoFilesManager misoFilesManager) {
    this.misoFilesManager = misoFilesManager;
  }

}
