package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.BoxChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.PoolChangeLog;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;

/**
 * When “collection” entities (pools, boxes) mutate write custom change log entries
 *
 */
final class CollectionLoggingInterceptor extends EmptyInterceptor implements BeanFactoryAware {

  private interface MoveCallback {
    public void invoke(String position, String name);
  }

  private final Queue<ChangeLog> changeLogQueue = new ConcurrentLinkedQueue<>();

  private ChangeLogStore changeLogStore;

  private BeanFactory factory;

  private void appendSet(StringBuilder target, Set<String> items, String prefix) {
    if (items.isEmpty()) return;
    target.append(" ");
    target.append(prefix);
    target.append(": ");
    boolean first = true;
    for (String item : items) {
      if (first) {
        first = false;
      } else {
        target.append(", ");
      }
      target.append(item);
    }
  }

  private Set<String> extractBoxables(Object boxableMap, MoveCallback callback) {
    Set<String> names = new TreeSet<>();
    @SuppressWarnings("unchecked")
    Map<String, Boxable> boxables = (Map<String, Boxable>) boxableMap;
    for (Entry<String, Boxable> entry : boxables.entrySet()) {
      String name = entry.getValue().getName() + "::" + entry.getValue().getAlias();
      names.add(name);
      callback.invoke(entry.getKey(), name);
    }
    return names;
  }

  private Set<String> extractDilutionNames(Object dilutionSet) {
    @SuppressWarnings("unchecked")
    Set<Dilution> dilutions = (Set<Dilution>) dilutionSet;
    Set<String> original = new HashSet<>();
    for (Dilution dilution : dilutions) {
      original.add(dilution.getName());
    }
    return original;
  }

  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames,
      Type[] types) {
    if (entity instanceof Box) {
      Box box = (Box) entity;
      for (int i = 0; i < propertyNames.length; i++) {
        if (propertyNames[i].equals("boxables")) {
          final Map<String, String> positions = new HashMap<>();
          final Set<String> moved = new TreeSet<>();

          Set<String> originalNames = extractBoxables(previousState[i], new MoveCallback() {
            @Override
            public void invoke(String position, String name) {
              positions.put(name, position);
            }
          });

          Set<String> newNames = extractBoxables(currentState[i], new MoveCallback() {
            @Override
            public void invoke(String position, String name) {
              if (positions.containsKey(name) && !positions.get(name).equals(position)) {
                moved.add(name);
              }
            }
          });

          Set<String> added = new TreeSet<>(newNames);
          added.removeAll(originalNames);
          Set<String> removed = new TreeSet<>(originalNames);
          removed.removeAll(newNames);
          if (!added.isEmpty() && !removed.isEmpty() && !moved.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Items");
            appendSet(message, added, "added");
            appendSet(message, removed, "removed");
            appendSet(message, moved, "moved");

            BoxChangeLog changeLog = new BoxChangeLog();
            changeLog.setBox(box);
            changeLog.setTime(new Date());
            changeLog.setColumnsChanged("contents");
            changeLog.setSummary(message.toString());
            changeLog.setUser(box.getLastModifier());
            changeLogQueue.add(changeLog);
          }
        }
      }
    } else if (entity instanceof Pool) {
      Pool pool = (Pool) entity;
      for (int i = 0; i < propertyNames.length; i++) {
        if (!propertyNames[i].equals("pooledElements")) continue;
        Set<String> original = extractDilutionNames(previousState[i]);
        Set<String> updated = extractDilutionNames(currentState[i]);

        Set<String> added = new TreeSet<>(updated);
        added.removeAll(original);
        Set<String> removed = new TreeSet<>(original);
        removed.removeAll(updated);

        if (!added.isEmpty() || !removed.isEmpty()) {
          StringBuilder message = new StringBuilder();
          message.append("Items");
          appendSet(message, added, "added");
          appendSet(message, removed, "removed");

          PoolChangeLog changeLog = new PoolChangeLog();
          changeLog.setPool(pool);
          changeLog.setColumnsChanged("contents");
          changeLog.setSummary(message.toString());
          changeLog.setTime(new Date());
          changeLog.setUser(pool.getLastModifier());
          changeLogQueue.add(changeLog);
        }

      }
    }
    return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void postFlush(Iterator entities) {
    super.postFlush(entities);
    if (changeLogStore == null) {
      changeLogStore = factory.getBean(ChangeLogStore.class);
    }
    ChangeLog log;
    while ((log = changeLogQueue.poll()) != null) {
      changeLogStore.create(log);
    }
  }

  @Override
  public void setBeanFactory(BeanFactory factory) throws BeansException {
    this.factory = factory;

  }

}