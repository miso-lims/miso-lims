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
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunQcException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

import java.io.IOException;
import java.util.*;

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
  private Cloner cloner = new Cloner();
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

  public void setRequestManager(RequestManager misoRequestManager) {
    this.misoRequestManager = misoRequestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  private Run cloneAndAddRun(Run run) {
    Run clone = cloner.deepClone(run);
    ((RunImpl)clone).addListener(getRunListener());
    runs.put(clone.getId(), clone);
    return clone;
  }

  public void indexify() throws IOException {
    log.info("Setting up...");
    if (enabled) {
      log.info("Indexifying...");
      runs.clear();

      log.info("Setting listeners and watcher groups...");
      Collection<Run> persistedRuns = misoRequestManager.listAllRuns();
      int count = 1;
      for (Run r : persistedRuns) {
        for (RunQC qc : misoRequestManager.listAllRunQCsByRunId(r.getId())) {
          try {
            r.addQc(qc);
          }
          catch (MalformedRunQcException e) {
            log.warn("Cannot add RunQC to Run " + r.getId());
          }
        }
        log.debug("Cloning run " +count+ " of " + persistedRuns.size() + " ("+r.getId()+")");
        cloneAndAddRun(r);
        count++;
      }
    }
  }

  public void update(Long runId) throws IOException {
    update(misoRequestManager.getRunById(runId));
  }

  private void update(Run r) throws IOException {
    if (enabled) {
      Run clone = runs.get(r.getId());
      if (clone == null) {
        //new run - add all RunWatchers!
        for (User u : securityManager.listUsersByGroupName("RunWatchers")) {
          r.addWatcher(u);
        }
        clone = cloneAndAddRun(r);
      }

      if (r.getStatus() != null) {
        clone.setStatus(cloner.deepClone(r.getStatus()));
      }

      //run QC added
      if (r.getRunQCs().size() > clone.getRunQCs().size()) {
        Set<RunQC> clonedQCs = new HashSet<RunQC>(clone.getRunQCs());
        for (RunQC qc : r.getRunQCs()) {
          if (!clonedQCs.contains(qc)) {
            log.info("Adding QC: " + qc.toString());
            try {
              clone.addQc(cloner.deepClone(qc));
            }
            catch (MalformedRunQcException e) {
              throw new IOException(e);
            }
          }
        }
      }

      runs.put(r.getId(), clone);
    }
  }

  public void addWatcher(Run run, Long userId) throws IOException {
    User user = securityManager.getUserById(userId);
    if (user != null) {
      Run clone = runs.get(run.getId());
      if (clone == null) {
        clone = cloneAndAddRun(run);
      }
      log.info("Added watcher " + userId + " to run " + run.getId());
      clone.addWatcher(user);
    }
  }

  public void removeWatcher(Run run, Long userId) throws IOException {
    User user = securityManager.getUserById(userId);
    if (user != null) {
      Run clone = runs.get(run.getId());
      if (clone == null) {
        clone = cloneAndAddRun(run);
      }
      log.info("Removed watcher " + userId + " from run " + run.getId());
      clone.removeWatcher(user);
    }
  }

  public void updateGroupWatcher(Long userId) throws IOException {
    User user = securityManager.getUserById(userId);
    if (user != null) {
      for (Run r : runs.values()) {
        if (user.getGroups().contains(securityManager.getGroupByName("RunWatchers"))) {
          addWatcher(r, userId);
        }
        else {
          if (r.getSecurityProfile() != null && r.getSecurityProfile().getOwner() != null && !r.getSecurityProfile().getOwner().equals(user)) {
            removeWatcher(r, userId);
          }
        }
      }
    }
  }
}