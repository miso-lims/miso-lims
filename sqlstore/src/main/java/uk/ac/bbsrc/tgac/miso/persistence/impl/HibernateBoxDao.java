package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.hibernate.Criteria;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateChangeLogDao.ChangeLogType;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateBoxDao implements BoxStore {
  private static class ChangeLogEntry {
    public Box box;
    public String summary;
  }

  private static final Logger log = LoggerFactory.getLogger(HibernateBoxDao.class);

  private static String TABLE_NAME = "Box";

  private final Queue<ChangeLogEntry> changeLogQueue = new ConcurrentLinkedQueue<>();

  @Autowired
  private ChangeLogStore changeLogStore;

  private final Interceptor interceptor = new EmptyInterceptor() {

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames,
        Type[] types) {
      if (entity instanceof Box) {
        for (int i = 0; i < propertyNames.length; i++) {
          if (propertyNames[i].equals("boxables")) {
            Map<String, String> positions = new HashMap<>();
            Set<String> moved = new TreeSet<>();
            Set<String> originalNames = new HashSet<>();
            @SuppressWarnings("unchecked")
            Map<String, Boxable> originalBoxables = (Map<String, Boxable>) previousState[i];
            for (Entry<String, Boxable> entry : originalBoxables.entrySet()) {
              String name = entry.getValue().getName() + "::" + entry.getValue().getAlias();
              originalNames.add(name);
              positions.put(name, entry.getKey());
            }

            Set<String> newNames = new HashSet<>();
            @SuppressWarnings("unchecked")
            Map<String, Boxable> newBoxables = (Map<String, Boxable>) currentState[i];
            for (Entry<String, Boxable> entry : newBoxables.entrySet()) {
              String name = entry.getValue().getName() + "::" + entry.getValue().getAlias();
              newNames.add(name);
              if (positions.containsKey(name) && !positions.get(name).equals(entry.getKey())) {
                moved.add(name);
              }
            }

            Set<String> added = new TreeSet<>(newNames);
            added.removeAll(originalNames);
            Set<String> removed = new TreeSet<>(originalNames);
            removed.removeAll(newNames);
            if (!added.isEmpty() && !removed.isEmpty() && !moved.isEmpty()) {
              StringBuilder message = new StringBuilder();
              message.append(((Box) entity).getLastModifier().getFullName());
              if (!added.isEmpty()) {
                message.append(" added:");
                for (String name : added) {
                  message.append(" ");
                  message.append(name);
                }
              }
              if (!removed.isEmpty()) {
                message.append(" removed:");
                for (String name : removed) {
                  message.append(" ");
                  message.append(name);
                }
              }
              if (!moved.isEmpty()) {
                message.append(" moved:");
                for (String name : moved) {
                  message.append(" ");
                  message.append(name);
                }
              }
              ChangeLogEntry changeLog = new ChangeLogEntry();
              changeLog.box = (Box) entity;
              changeLog.summary = message.toString();
              changeLogQueue.add(changeLog);
            }
          }
        }
      }
      return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

  };

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private JdbcTemplate template;

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(BoxImpl.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  private Session currentSession() {
    return getSessionFactory().withOptions().interceptor(interceptor).openSession();
  }

  @Override
  public void discardAllTubes(Box box) throws IOException {
    List<Boxable> originalContents = new ArrayList<>(box.getBoxables().values());
    try {
      box.removeAllBoxables();
      save(box);
    } catch (IOException e) {
      log.debug("Error discarding box", e);
      throw new IOException("Error discarding box: " + e.getMessage());
    }

    for (Boxable boxable : originalContents) {
      boxable.setDiscarded(true);
      currentSession().save(boxable);
    }
  }

  @Override
  public void discardSingleTube(Box box, String position) throws IOException {
    Boxable target = box.getBoxable(position);
    target.setDiscarded(true);
    box.removeBoxable(position);
    save(box);
    currentSession().save(target);
  }

  @Override
  public Box get(long boxId) throws IOException {
    return (Box) currentSession().get(BoxImpl.class, boxId);
  }

  @Override
  public Box getBoxByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxImpl.class);
    criteria.add(Restrictions.eq("alias", alias));
    return (Box) criteria.uniqueResult();
  }

  @Override
  public Map<String, Integer> getBoxColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, TABLE_NAME);
  }

  @Override
  public Box getByBarcode(String barcode) throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxImpl.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    return (Box) criteria.uniqueResult();
  }

  public ChangeLogStore getChangeLogStore() {
    return changeLogStore;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public BoxSize getSizeById(long id) throws IOException {
    return (BoxSize) currentSession().get(BoxSize.class, id);
  }

  @Override
  public BoxUse getUseById(long id) throws IOException {
    return (BoxUse) currentSession().get(BoxUse.class, id);
  }

  @Override
  public Collection<Box> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxImpl.class);
    @SuppressWarnings("unchecked")
    List<Box> results = criteria.list();
    return results;
  }

  @Override
  public Collection<BoxSize> listAllBoxSizes() throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxSize.class);
    @SuppressWarnings("unchecked")
    List<BoxSize> results = criteria.list();
    return results;
  }

  @Override
  public Collection<BoxUse> listAllBoxUses() throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxUse.class);
    @SuppressWarnings("unchecked")
    List<BoxUse> results = criteria.list();
    return results;
  }

  @Override
  public List<String> listAllBoxUsesStrings() throws IOException {
    List<String> results = new ArrayList<>();
    for (BoxUse use : listAllBoxUses()) {
      results.add(use.getAlias());
    }
    return results;
  }

  @Override
  public Collection<Box> listWithLimit(long limit) throws IOException {
    Criteria criteria = currentSession().createCriteria(BoxImpl.class);
    criteria.setMaxResults((int) limit);
    @SuppressWarnings("unchecked")
    List<Box> results = criteria.list();
    return results;
  }

  @Override
  public boolean remove(Box box) throws IOException {
    if (box.getId() != AbstractBox.UNSAVED_ID) {
      currentSession().delete(box);
      return true;
    }
    return false;
  }

  @Override
  public void removeBoxableFromBox(Boxable boxable) throws IOException {
    Box box = get(boxable.getBox().getId());
    box.removeBoxable(boxable);
    currentSession().save(box);
  }

  @Override
  public long save(Box box) throws IOException {
    if (box.getId() == AbstractBox.UNSAVED_ID) {
      return (long) currentSession().save(box);
    } else {
      currentSession().update(box);
      currentSession().flush();
      ChangeLogEntry log;
      while ((log = changeLogQueue.poll()) != null) {
        changeLogStore.create(ChangeLogType.BOX.name(), log.box.getId(), "contents", log.summary, log.box.getLastModifier());
      }

      return box.getId();
    }
  }

  public void setChangeLogStore(ChangeLogStore changeLogStore) {
    this.changeLogStore = changeLogStore;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
