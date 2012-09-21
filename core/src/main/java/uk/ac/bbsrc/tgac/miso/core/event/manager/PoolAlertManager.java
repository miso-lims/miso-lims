/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.event.manager;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.rits.cloning.Cloner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractPool;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.manager
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 11/11/11
 * @since 0.1.6
 */
public class PoolAlertManager {
  protected static final Logger log = LoggerFactory.getLogger(PoolAlertManager.class);
  final Map<Long, Pool> pools = new HashMap<Long, Pool>();
  final Set<User> poolWatchers = new HashSet<User>();

  private RequestManager misoRequestManager;
  private Cloner cloner = new Cloner();
  private boolean enabled = true;

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private MisoListener poolListener;

  public MisoListener getPoolListener() {
    return poolListener;
  }

  public void setPoolListener(MisoListener poolListener) {
    this.poolListener = poolListener;
  }

  public void setRequestManager(RequestManager misoRequestManager) {
    this.misoRequestManager = misoRequestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  private Pool cloneAndAdd(Pool pool) {
    for (User u : poolWatchers) {
      pool.addWatcher(u);
    }

    Pool clone = cloner.deepClone(pool);
    ((PoolImpl)clone).addListener(getPoolListener());
    pools.put(clone.getId(), clone);
    return clone;
  }

  public void indexify() throws IOException {
    log.info("Setting up...");
    if (enabled) {
      log.info("Indexifying...");
      pools.clear();

      log.info("Setting pool listeners and watcher groups...");
      poolWatchers.clear();
      poolWatchers.addAll(securityManager.listUsersByGroupName("PoolWatchers"));

      Collection<Pool<? extends Poolable>> persistedPools = misoRequestManager.listAllPools();
      for (Pool p : persistedPools) {
        cloneAndAdd(p);
      }
    }
  }

  public void update(Long poolId) throws IOException {
    update(misoRequestManager.getPoolById(poolId));
  }

  private void update(Pool p) throws IOException {
    if (enabled) {
      Pool clone = pools.get(p.getId());
      if (clone == null) {
        log.info("New pool - adding all PoolWatchers and cloning!");
        clone = cloneAndAdd(p);

        //TODO EVIL EVIL EVIL FIX UPON PAIN OF DEATH
        if (clone.getReadyToRun()) {
          try {
            //fire event if pool has been saved initially to ready to run
            Method m = AbstractPool.class.getDeclaredMethod("firePoolReadyEvent");
            m.setAccessible(true);
            m.invoke(clone);
          }
          catch (Exception e) {
            log.debug("Cannot fire pool ready event: " + e.getMessage());
            e.printStackTrace();
          }
        }
      }
      else {
        log.info("Updating Pool " + clone.getId() + " ...");

        //find any watchable setters on the clone and call the respective getter from the clone parent
        //i.e. clone.setFoo(parent.getFoo()); where @WatchableSetter Class.setFoo(T t);
        /*
        for (Method setter : clone.getClass().getMethods()) {
          if (setter.getAnnotation(WatchableSetter.class)) {
            try {
              Method getter = clone.getClass().getMethod(setter.getName().replaceFirst("set", "get"));
              setter.invoke(clone, getter.invoke(p));
            }
            catch (NoSuchMethodException e) {
              e.printStackTrace();
            }
            catch (InvocationTargetException e) {
              e.printStackTrace();
            }
            catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          }
        }
        */
        //TODO the above will get rid of this necessity to call each method explicitly
        clone.setReadyToRun(p.getReadyToRun());
      }
      pools.put(p.getId(), clone);
    }
  }

  public void addWatcher(Pool pool, Long userId) throws IOException {
    User user = securityManager.getUserById(userId);
    if (user != null) {
      Pool clone = pools.get(pool.getId());
      if (clone == null) {
        clone = cloneAndAdd(pool);
      }
      clone.addWatcher(user);
    }
  }

  public void removeWatcher(Pool pool, Long userId) throws IOException {
    User user = securityManager.getUserById(userId);
    if (user != null && pool.getWatchers().contains(user)) {
      Pool clone = pools.get(pool.getId());
      if (clone == null) {
        clone = cloneAndAdd(pool);
      }
      clone.removeWatcher(user);
    }
  }

  public void updateGroupWatcher(Long userId) throws IOException {
    User user = securityManager.getUserById(userId);
    if (user != null) {
      poolWatchers.clear();
      poolWatchers.addAll(securityManager.listUsersByGroupName("PoolWatchers"));

      for (Pool p : pools.values()) {
        if (user.getGroups().contains(securityManager.getGroupByName("PoolWatchers"))) {
          addWatcher(p, userId);
        }
        else {
          if (p.getSecurityProfile() != null && p.getSecurityProfile().getOwner() != null && !p.getSecurityProfile().getOwner().equals(user)) {
            removeWatcher(p, userId);
          }
        }
      }
    }
  }
}