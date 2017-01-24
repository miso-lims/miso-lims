package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

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

  private Criterion idForAnyType(Criteria criteria, ChangeLogType changeLogType, long id) throws IOException {
    Criterion idByType;
    switch (changeLogType) {
    case BOX:
      idByType = Restrictions.eq("boxChangeLogId", id);
      break;
    case EXPERIMENT:
      idByType = Restrictions.eq("experimentChangeLogId", id);
      break;
    case KITDESCRIPTOR:
      idByType = Restrictions.eq("kitDescriptorChangeLogId", id);
      break;
    case LIBRARY:
      idByType = Restrictions.eq("libraryChangeLogId", id);
      break;
    case POOL:
      idByType = Restrictions.eq("poolChangeLogId", id);
      break;
    case RUN:
      idByType = Restrictions.eq("runChangeLogId", id);
      break;
    case SAMPLE:
      idByType = Restrictions.eq("sampleChangeLogId", id);
      break;
    case SEQUENCERPARTITIONCONTAINER:
      idByType = Restrictions.eq("containerChangeLogId", id);
      break;
    case STUDY:
      idByType = Restrictions.eq("studyChangeLogId", id);
      break;
    default:
      throw new IOException("Invalid changelog type");
    }
    return idByType;
  }

  @Override
  public List<ChangeLog> listAllById(String type, long id) throws IOException {
    ChangeLogType changeLogType = ChangeLogType.get(type);
    Criteria criteria = currentSession().createCriteria(changeLogType.getClazz());
    criteria.add(idForAnyType(criteria, changeLogType, id));
    @SuppressWarnings("unchecked")
    List<ChangeLog> results = criteria.list();
    return results;
  }

  @Override
  public void deleteAllById(String type, long id) {
    ChangeLogType changeLogType = ChangeLogType.get(type);
    Query query = currentSession().createQuery(String.format("delete %s where id = :ID", changeLogType.getTableName()));
    query.setParameter("ID", id);
    query.executeUpdate();
  }

  @Override
  public Long create(String type, long entityId, ChangeLog changeLog) {
    ChangeLogType changeLogType = ChangeLogType.get(type);
    ChangeLog result = changeLogType.create();
    result.setColumnsChanged(changeLog.getColumnsChanged());
    result.setId(entityId);
    result.setSummary(changeLog.getSummary());
    result.setTime(new Date());
    result.setUser(changeLog.getUser());
    return (Long) currentSession().save(result);
  }

  @Override
  public Long create(String type, long entityId, String columnsChanged, String summary, User user) {
    ChangeLogType changeLogType = ChangeLogType.get(type);
    ChangeLog result = changeLogType.create();
    result.setColumnsChanged(columnsChanged);
    result.setId(entityId);
    result.setSummary(summary);
    result.setTime(new Date());
    result.setUser(user);
    return (Long) currentSession().save(result);
  }

  public static enum ChangeLogType {
    BOX("BoxChangeLog", BoxChangeLog.class) {
      @Override
      ChangeLog create() {
        return new BoxChangeLog();
      }
    }, //
    EXPERIMENT("ExperimentChangeLog", ExperimentChangeLog.class) {
      @Override
      ChangeLog create() {
        return new ExperimentChangeLog();
      }
    }, //
    KITDESCRIPTOR("KitDescriptorChangeLog", KitDescriptorChangeLog.class) {
      @Override
      ChangeLog create() {
        return new KitDescriptorChangeLog();
      }
    }, //
    LIBRARY("LibraryChangeLog", LibraryChangeLog.class) {
      @Override
      ChangeLog create() {
        return new LibraryChangeLog();
      }
    }, //
    POOL("PoolChangeLog", PoolChangeLog.class) {
      @Override
      ChangeLog create() {
        return new PoolChangeLog();
      }
    }, //
    RUN("RunChangeLog", RunChangeLog.class) {
      @Override
      ChangeLog create() {
        return new RunChangeLog();
      }
    }, //
    SAMPLE("SampleChangeLog", SampleChangeLog.class) {
      @Override
      ChangeLog create() {
        return new SampleChangeLog();
      }
    }, //
    SEQUENCERPARTITIONCONTAINER("SequencerPartitionContainerChangeLog", SequencerPartitionContainerChangeLog.class) {
      @Override
      ChangeLog create() {
        return new SequencerPartitionContainerChangeLog();
      }
    }, //
    STUDY("StudyChangeLog", StudyChangeLog.class) {
      @Override
      ChangeLog create() {
        return new StudyChangeLog();
      }
    };//

    private final String tableName;
    private final Class<? extends ChangeLog> clazz;

    /** Construct an empty change log. */
    abstract ChangeLog create();

    private ChangeLogType(String tableName, Class<? extends ChangeLog> clazz) {
      this.tableName = tableName;
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
  }

}
