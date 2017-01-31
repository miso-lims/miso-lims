package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.BoxChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.ExperimentChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.KitDescriptorChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.LibraryChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.PoolChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.RunChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SampleChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SequencerPartitionContainerChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.StudyChangeLog;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateChangeLogDao implements ChangeLogStore {

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<ChangeLog> listAll(String type) {
    ChangeLogType changeLogType = ChangeLogType.get(type);
    Criteria criteria = currentSession().createCriteria(changeLogType.getClazz());
    @SuppressWarnings("unchecked")
    List<ChangeLog> results = criteria.list();
    return results;
  }

  @Override
  public List<ChangeLog> listAllById(String type, long id) throws IOException {
    ChangeLogType changeLogType = ChangeLogType.get(type);
    Criteria criteria = currentSession().createCriteria(changeLogType.getClazz());
    criteria.createAlias(changeLogType.getChangeLogEntityReferenceName(), "entityReference");
    criteria.add(Restrictions.eq("entityReference.id", id));
    @SuppressWarnings("unchecked")
    List<ChangeLog> results = criteria.list();
    return results;
  }

  @Override
  public void deleteAllById(String type, long id) {
    ChangeLogType changeLogType = ChangeLogType.get(type);
    Query query = currentSession().createQuery(
        String.format("delete %s where %s.id = :ID", changeLogType.getTableName(), changeLogType.getChangeLogEntityReferenceName()));
    query.setParameter("ID", id);
    query.executeUpdate();
  }

  @Override
  public Long create(ChangeLog changeLog) {
    checkNotNull(changeLog.getId(), "The entity this change log applies to must have an id.");
    checkNotNull(changeLog.getUser(), "The change log must be associated with a user.");
    checkNotNull(changeLog.getSummary(), "The change log must have a summary.");
    checkNotNull(changeLog.getColumnsChanged(), "The change log must have columns changed.");
    checkArgument(!LimsUtils.isStringEmptyOrNull(changeLog.getSummary()), "The change log summary must not be empty.");
    checkArgument(!LimsUtils.isStringEmptyOrNull(changeLog.getColumnsChanged()), "The change log columns changed must not be empty");
    if (changeLog.getTime() == null) {
      // Set the change log time stamp to the current time if it hasn't already been set. In the case of migration the time stamp
      // will be set to a historical time to accurately represent the time when the even occurred.
      changeLog.setTime(new Date());
    }
    return (Long) currentSession().save(changeLog);
  }

  public static enum ChangeLogType {
    BOX("BoxChangeLog", "box", BoxChangeLog.class), //
    EXPERIMENT("ExperimentChangeLog", "experiment", ExperimentChangeLog.class), //
    KITDESCRIPTOR("KitDescriptorChangeLog", "kitDescriptor", KitDescriptorChangeLog.class), //
    LIBRARY("LibraryChangeLog", "library", LibraryChangeLog.class), //
    POOL("PoolChangeLog", "pool", PoolChangeLog.class), //
    RUN("RunChangeLog", "run", RunChangeLog.class), //
    SAMPLE("SampleChangeLog", "sample", SampleChangeLog.class), //
    SEQUENCERPARTITIONCONTAINER("SequencerPartitionContainerChangeLog", "sequencerPartitionContainer", SequencerPartitionContainerChangeLog.class), //
    STUDY("StudyChangeLog", "study", StudyChangeLog.class);//

    private final String tableName;
    private final String changeLogEntityReferenceName;
    private final Class<? extends ChangeLog> clazz;

    private ChangeLogType(String tableName, String changeLogReferenceName, Class<? extends ChangeLog> clazz) {
      this.tableName = tableName;
      this.changeLogEntityReferenceName = changeLogReferenceName;
      this.clazz = clazz;
    }

    public String getTableName() {
      return tableName;
    }

    public Class<? extends ChangeLog> getClazz() {
      return clazz;
    }

    public static ChangeLogType get(String type) {
      return ChangeLogType.valueOf(type.toUpperCase());
    }

    /**
     * Name of change log member that provides a reference to entity the change log was written about.
     * 
     * @return Change log member name.
     */
    public String getChangeLogEntityReferenceName() {
      return changeLogEntityReferenceName;
    }
  }

}
