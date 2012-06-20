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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidPool;

import java.io.IOException;
import java.util.*;

/**
 * com.eaglegenomics.miso.web
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Controller
public class ListPoolsController {
  protected static final Logger log = LoggerFactory.getLogger(ListPoolsController.class);

  @Autowired
  private SecurityManager securityManager;

  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping("/pools")
  public ModelAndView listPools(ModelMap model) throws IOException {
    try {
      List<Pool<? extends Poolable>> ipools = requestManager.listAllIlluminaPools();
      List<Pool<? extends Poolable>> lpools = requestManager.listAll454Pools();
      List<Pool<? extends Poolable>> spools = requestManager.listAllSolidPools();
      model.addAttribute("illuminaPools", ipools);
      model.addAttribute("ls454Pools", lpools);
      model.addAttribute("solidPools", spools);

      return new ModelAndView("/pages/listPools.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list pools", ex);
      }
      throw ex;
    }
  }

  @RequestMapping("/pools/ready")
  public ModelAndView listReadyPools(ModelMap model) throws IOException {
    try {
      List<Pool> ipools = new ArrayList<Pool>();
      List<Pool> ipoolsUsed = new ArrayList<Pool>();
      for (Pool p: requestManager.listReadyIlluminaPools()) {
        if (requestManager.listRunsByPoolId(p.getPoolId()).isEmpty()) {
          ipools.add(p);
        }
        else {
          ipoolsUsed.add(p);
        }
      }

      List<Pool> lpools = new ArrayList<Pool>();
      List<Pool> lpoolsUsed = new ArrayList<Pool>();
      for (Pool p: requestManager.listReady454Pools()) {
        if (requestManager.listRunsByPoolId(p.getPoolId()).isEmpty()) {
          lpools.add(p);
        }
        else {
          lpoolsUsed.add(p);
        }
      }

      List<Pool> spools = new ArrayList<Pool>();
      List<Pool> spoolsUsed = new ArrayList<Pool>();
      for (Pool p: requestManager.listReadySolidPools()) {
        if (requestManager.listRunsByPoolId(p.getPoolId()).isEmpty()) {
          spools.add(p);
        }
        else {
          spoolsUsed.add(p);
        }
      }

      model.addAttribute("ready", true);
      model.addAttribute("illuminaPools", ipools);
      model.addAttribute("illuminaPoolsUsed", ipoolsUsed);
      model.addAttribute("ls454Pools", lpools);
      model.addAttribute("ls454PoolsUsed", lpoolsUsed);
      model.addAttribute("solidPools", spools);
      model.addAttribute("solidPoolsUsed", spoolsUsed);

      return new ModelAndView("/pages/readyPools.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list pools", ex);
      }
      throw ex;
    }
  }
}
