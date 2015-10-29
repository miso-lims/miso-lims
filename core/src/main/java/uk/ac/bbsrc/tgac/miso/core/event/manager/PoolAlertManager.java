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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.rits.cloning.Cloner;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractPool;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

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

  public void applyListeners(Pool pool) {
    pool.addListener(getPoolListener());
  }

  public void removeListeners(Pool pool) {
    pool.removeListener(getPoolListener());
  }

  public void push(Pool pool) {
    if (enabled) {
      if (pool != null) {
        log.debug("Attempting to clone pool " + pool.getId());
        try {
          Pool clone = cloner.deepClone(pool);
          if (clone != null) {
            applyListeners(clone);
            if (pools.containsKey(pool.getId())) {
              log.debug("Not replacing Pool " + clone.getId() + ": Ready? " + clone.getReadyToRun());
            } else {
              pools.put(pool.getId(), clone);
              log.debug("Queued Pool " + clone.getId() + ": Ready? " + clone.getReadyToRun());
            }
          }
        } catch (Exception e) {
          log.error("push", e);
        }
      }
    } else {
      log.warn("Alerting system disabled.");
    }
  }

  public void pop(Pool pool) {
    if (enabled) {
      if (pool != null) {
        Pool clone = pools.get(pool.getId());
        if (clone != null) {
          removeListeners(clone);
          clone = null;
          pools.remove(pool.getId());
          log.debug("Dequeued " + pool.getId());
        }
      }
    } else {
      log.warn("Alerting system disabled.");
    }
  }

  public void update(Long poolId) throws IOException {
    update(misoRequestManager.getPoolById(poolId));
  }

  private void update(Pool p) throws IOException {
    if (enabled) {
      Pool clone = pools.get(p.getId());
      if (clone == null) {
        log.debug("Update: no clone - pushing");
        // new run - add all PoolWatchers!
        for (User u : securityManager.listUsersByGroupName("PoolWatchers")) {
          p.addWatcher(u);
        }
        push(p);
      } else {
        log.debug("Update: got clone of " + clone.getId());
        // TODO EVIL EVIL EVIL FIX UPON PAIN OF DEATH
        if (clone.getReadyToRun()) {
          try {
            // fire event if pool has been saved initially to ready to run
            Method m = AbstractPool.class.getDeclaredMethod("firePoolReadyEvent");
            m.setAccessible(true);
            m.invoke(clone);
          } catch (Exception e) {
            log.error("Cannot fire pool ready event", e);
          }
        } else {
          log.debug("Updating Pool " + clone.getId() + " ...");
          clone.setReadyToRun(p.getReadyToRun());
        }

        pop(clone);
        push(p);
      }
    }
  }

  public void addWatcher(Pool pool, Long userId) throws IOException {
    User user = securityManager.getUserById(userId);
    if (user != null) {
      Pool clone = pools.get(pool.getId());
      if (clone == null) {
        pool.addWatcher(user);
        push(pool);
      } else {
        clone.addWatcher(user);
      }
    }
  }

  public void removeWatcher(Pool pool, Long userId) throws IOException {
    User user = securityManager.getUserById(userId);
    if (user != null && pool.getWatchers().contains(user)) {
      Pool clone = pools.get(pool.getId());
      if (clone == null) {
        pool.removeWatcher(user);
        push(pool);
      } else {
        clone.removeWatcher(user);
      }
    }
  }

  public void updateGroupWatcher(Long userId) throws IOException {
    User user = securityManager.getUserById(userId);
    if (user != null) {
      poolWatchers.clear();
      poolWatchers.addAll(securityManager.listUsersByGroupName("PoolWatchers"));

      for (Pool p : pools.values()) {
        if (user.getGroups() != null && user.getGroups().contains(securityManager.getGroupByName("PoolWatchers"))) {
          addWatcher(p, userId);
        } else {
          if (p.getSecurityProfile() != null && p.getSecurityProfile().getOwner() != null
              && !p.getSecurityProfile().getOwner().equals(user)) {
            removeWatcher(p, userId);
          }
        }
      }
    }
  }
}
