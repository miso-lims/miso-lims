/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.manager
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 11/11/11
 * @since 0.1.6
 */
@Service
public class PoolAlertManager {
  private static final Logger log = LoggerFactory.getLogger(PoolAlertManager.class);
  final Map<Long, Pool> pools = new ConcurrentHashMap<>();
  final Set<User> poolWatchers = ConcurrentHashMap.newKeySet();

  @Value("${miso.alerting.enabled}")
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
        Pool clone = partialCopy(pool);
        log.debug("Attempting to clone pool " + pool.getId());
        try {
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

  public void update(Pool p) throws IOException {
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
        clone.setReadyToRun(p.getReadyToRun());
        pop(clone);
        push(p);
      }
    }
  }

  public void addWatcher(Pool pool, User user) throws IOException {
      Pool clone = pools.get(pool.getId());
      if (clone == null) {
        pool.addWatcher(user);
        push(pool);
      } else {
        clone.addWatcher(user);
      }
  }

  public void removeWatcher(Pool pool, User user) throws IOException {
    if (pool.getWatchers().contains(user)) {
      Pool clone = pools.get(pool.getId());
      if (clone == null) {
        pool.removeWatcher(user);
        push(pool);
      } else {
        clone.removeWatcher(user);
      }
    }
  }

  public void updateGroupWatcher(User user) throws IOException {
    if (user != null) {
      poolWatchers.clear();
      poolWatchers.addAll(securityManager.listUsersByGroupName("PoolWatchers"));

      for (Pool p : pools.values()) {
        if (user.getGroups() != null && user.getGroups().contains(securityManager.getGroupByName("PoolWatchers"))) {
          addWatcher(p, user);
        } else {
          if (p.getSecurityProfile() != null && p.getSecurityProfile().getOwner() != null
              && !p.getSecurityProfile().getOwner().equals(user)) {
            removeWatcher(p, user);
          }
        }
      }
    }
  }

  /**
   * Creates a minimal copy of the pool to be used for change tracking in the alerting system. Only relevant fields are copied
   * 
   * @param pool
   *          the pool to copy
   * @return the copy
   */
  private Pool partialCopy(Pool pool) {
    Pool clone = new PoolImpl();
    clone.setId(pool.getId());
    clone.setAlias(pool.getAlias());
    clone.setName(pool.getName());
    clone.setReadyToRun(pool.getReadyToRun());
    return clone;
  }

}
