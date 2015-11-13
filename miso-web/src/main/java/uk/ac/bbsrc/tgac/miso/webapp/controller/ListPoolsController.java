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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
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

  @ModelAttribute("platformTypes")
  public Collection<String> populatePlatformTypes() {
    return PlatformType.getKeys();
  }

  @RequestMapping("/pools")
  public ModelAndView listPools() throws IOException {
    return new ModelAndView("/pages/listPools.jsp");
  }

  @RequestMapping("/pools/ready")
  public ModelAndView listReadyPools(ModelMap model) throws IOException {
    try {

      Map<String, List<Pool>> poolMap = new HashMap<>();
      Map<String, List<Pool>> usedPoolMap = new HashMap<>();

      for (PlatformType pt : PlatformType.values()) {
        List<Pool> pools = new ArrayList<Pool>();
        List<Pool> poolsUsed = new ArrayList<Pool>();
        for (Pool p : requestManager.listReadyPoolsByPlatform(pt)) {
          if (requestManager.listRunsByPoolId(p.getId()).isEmpty()) {
            pools.add(p);
          } else {
            poolsUsed.add(p);
          }
        }
        String ident = pt.getKey();

        poolMap.put(ident, pools);
        usedPoolMap.put(ident, poolsUsed);
      }

      model.addAttribute("pools", poolMap);
      model.addAttribute("usedpools", usedPoolMap);

      model.addAttribute("ready", true);
      return new ModelAndView("/pages/readyPools.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list pools", ex);
      }
      throw ex;
    }
  }
}
