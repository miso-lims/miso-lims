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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunQCImpl;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunQcException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.manager
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 11/11/11
 * @since 0.1.3
 */
public class RunAlertManager {
  protected static final Logger log = LoggerFactory.getLogger(RunAlertManager.class);
  Map<Long, Run> runs = new HashMap<Long, Run>();

  private RequestManager misoRequestManager;
  private boolean enabled = true;

  @Autowired
  private SecurityManager securityManager;

  private MisoListener runListener;

  public MisoListener getRunListener() {
    return runListener;
  }

  public void setRunListener(MisoListener runListener) {
    this.runListener = runListener;
  }

  public void applyListeners(Run run) {
    run.addListener(getRunListener());
  }

  public void removeListeners(Run run) {
    run.removeListener(getRunListener());
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

  public void push(Run run) {
    if (enabled) {
      if (run != null) {
        Run clone = partialCopy(run);
        if (clone != null) {
          applyListeners(clone);
          if (runs.containsKey(run.getId())) {
            if (clone.getStatus() != null) {
              log.debug("Not replacing Run " + clone.getId() + ": " + clone.getStatus().getHealth().name());
            }
          } else {
            runs.put(run.getId(), clone);
            if (clone.getStatus() != null) {
              log.debug("Queued Run " + clone.getId() + ": " + clone.getStatus().getHealth().name());
            }
          }
        }
      }
    } else {
      log.warn("Alerting system disabled.");
    }
  }

  public void pop(Run run) {
    if (enabled) {
      if (run != null) {
        Run clone = runs.get(run.getId());
        if (clone != null) {
          removeListeners(clone);
          clone = null;
          runs.remove(run.getId());
          log.debug("Dequeued " + run.getId());
        }
      }
    } else {
      log.warn("Alerting system disabled.");
    }
  }

  public void update(Long runId) throws IOException {
    update(misoRequestManager.getRunById(runId));
  }
  
  public void updateQcs(Long runQcId) throws IOException {
    Run run = misoRequestManager.getRunQCById(runQcId).getRun();
    update(run);
  }

  private void update(Run r) throws IOException {
    if (enabled) {
      Run clone = runs.get(r.getId());
      if (clone == null) {
        log.debug("Update: no clone - pushing");
        // new run - add all RunWatchers!
        for (User u : securityManager.listUsersByGroupName("RunWatchers")) {
          r.addWatcher(u);
        }
        push(r);
      } else {
        log.debug("Update: got clone of " + clone.getId());
        if (r.getStatus() != null) {
          clone.setStatus(r.getStatus());
        }

        // run QC added
        if (r.getRunQCs().size() > clone.getRunQCs().size()) {
          Map<Long, RunQC> clonedQCs = new HashMap<>();
          for (RunQC qc : clone.getRunQCs()) {
            clonedQCs.put(qc.getId(), qc);
          }
          for (RunQC qc : r.getRunQCs()) {
            if (!clonedQCs.containsKey(qc.getId())) {
              try {
                clone.addQc(partialCopy(qc));
              } catch (MalformedRunQcException e) {
                throw new IOException(e);
              }
            }
          }
        }

        pop(clone);
        push(r);
      }
    }
  }

  public void addWatcher(Run run, Long userId) throws IOException {
    User user = securityManager.getUserById(userId);
    if (user != null) {
      Run clone = runs.get(run.getId());
      if (clone == null) {
        run.addWatcher(user);
        push(run);
      } else {
        clone.addWatcher(user);
      }
    }
  }

  public void removeWatcher(Run run, Long userId) throws IOException {
    User user = securityManager.getUserById(userId);
    if (user != null) {
      Run clone = runs.get(run.getId());
      if (clone == null) {
        run.removeWatcher(user);
        push(run);
      } else {
        clone.removeWatcher(user);
      }
    }
  }

  public void updateGroupWatcher(Long userId) throws IOException {
    User user = securityManager.getUserById(userId);
    if (user != null) {
      for (Run r : runs.values()) {
        if (user.getGroups() != null && user.getGroups().contains(securityManager.getGroupByName("RunWatchers"))) {
          addWatcher(r, userId);
        } else {
          if (r.getSecurityProfile() != null && r.getSecurityProfile().getOwner() != null
              && !r.getSecurityProfile().getOwner().equals(user)) {
            removeWatcher(r, userId);
          }
        }
      }
    }
  }
  
  /**
   * Creates a minimal copy of the run to be used for change tracking in the alerting system. Only relevant 
   * fields are copied
   * 
   * @param run the run to copy
   * @return the copy
   */
  private Run partialCopy(Run run) {
    Run clone = new RunImpl();
    clone.setId(run.getId());
    clone.setAlias(run.getAlias());
    clone.setStatus(run.getStatus());
    for (RunQC qc : run.getRunQCs()) {
      try {
        clone.addQc(partialCopy(qc));
      } catch (MalformedRunQcException e) {
        log.error("Can't track Malformed RunQC", e);
      }
    }
    for (User u : run.getWatchers()) {
      clone.addWatcher(u);
    }
    return clone;
  }
  
  /**
   * Creates a minimal copy of the RunQC to be used for change tracking in the alerting system. Only relevant
   * fields are copied
   * 
   * @param runQc the RunQC to copy
   * @return the copy
   */
  private RunQC partialCopy(RunQC runQc) {
    RunQC clone = new RunQCImpl();
    clone.setId(runQc.getId());
    clone.setQcDate(runQc.getQcDate());
    clone.setQcType(runQc.getQcType());
    clone.setQcCreator(runQc.getQcCreator());
    clone.setInformation(runQc.getInformation());
    return clone;
  }
}