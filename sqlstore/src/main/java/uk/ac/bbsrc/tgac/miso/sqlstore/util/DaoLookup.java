package uk.ac.bbsrc.tgac.miso.sqlstore.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.store.Store;

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
    Class<?> type = getAssignableClassFromClass(clz);
    if (type != null) {
      return (Store<T>) daos.get(getAssignableClassFromClass(clz));
    }
    return null;
  }

  public Set<Class<?>> getDaoKeys() {
    return daos.keySet();
  }

  public Class<?> getAssignableClassFromClass(Class<?> clz) {
    for (Class<?> type : getDaoKeys()) {
      if (type.isAssignableFrom(clz)) {
        return type;
      }
    }
    return null;
  }
}
