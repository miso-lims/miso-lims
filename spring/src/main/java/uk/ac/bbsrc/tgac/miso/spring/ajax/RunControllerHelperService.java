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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.service.RunService;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class RunControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(RunControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RunService runService;

  public JSONObject watchRun(HttpSession session, JSONObject json) {
    Long runId = json.getLong("runId");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Run run = runService.get(runId);
      if (!run.getWatchers().contains(user)) {
        runService.addRunWatcher(run, user);
      }
      return JSONUtils.SimpleJSONResponse("OK");
    } catch (IOException e) {
      log.error("watch run", e);
    }
    return JSONUtils.SimpleJSONError("Unable to watch run");
  }

  public JSONObject unwatchRun(HttpSession session, JSONObject json) {
    Long runId = json.getLong("runId");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Run run = runService.get(runId);
      if (run.getWatchers().contains(user)) {
        runService.removeRunWatcher(run, user);
      }
      return JSONUtils.SimpleJSONResponse("OK");
    } catch (IOException e) {
      log.error("unwatch run", e);
    }
    return JSONUtils.SimpleJSONError("Unable to unwatch run");
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

}
