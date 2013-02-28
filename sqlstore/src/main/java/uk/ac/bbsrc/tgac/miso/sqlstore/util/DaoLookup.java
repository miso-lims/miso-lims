package uk.ac.bbsrc.tgac.miso.sqlstore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.store.Store;

import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore.util
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 04/01/13
 * @since 0.1.9
 */
public class DaoLookup {
  protected static final Logger log = LoggerFactory.getLogger(DaoLookup.class);
  private Map<Class<?>, Store<?>> daos = new HashMap<Class<?>, Store<?>>();

  public void setDaos(Map<Class<?>, Store<?>> daos) {
    this.daos = daos;
  }

  public <T> Store<T> lookup(Class<? extends T> clz) {
    for (Class<?> type : daos.keySet()) {
      if (type.isAssignableFrom(clz)) {
        return (Store<T>)daos.get(type);
      }
    }
    return null;
  }
  /*
  public <T> Set<Store<? extends T>> lookup(Class<?> clz) {
    if (daos == null) {
      ServiceLoader<Store> consumerLoader = ServiceLoader.load(Store.class);
      Iterator<Store> consumerIterator = consumerLoader.iterator();

      daos = new HashMap<Class<?>, Store<?>>();
      while (consumerIterator.hasNext()) {
        Store p = consumerIterator.next();

        if (!daos.containsKey(p.getClass())) {
          daos.put(p.getClass(), p);
        }
        else {
          if (daos.get(p.getClass()) != p) {
            String msg = "Multiple different Stores with the same persistable type " +
                         "('" + p.getClass() + "') are present on the classpath. Store types must be unique.";
            log.error(msg);
            throw new ServiceConfigurationError(msg);
          }
        }
      }

      log.info("Loaded " + daos.values().size() + " known stores");
    }

    Set<Store<? extends T>> found = new HashSet<Store<? extends T>>();
    for (Class<?> type : daos.keySet()) {
      if (type.isAssignableFrom(clz)) {
        found.add(daos.get(type));
      }
    }
    return found;
  }
  */
}