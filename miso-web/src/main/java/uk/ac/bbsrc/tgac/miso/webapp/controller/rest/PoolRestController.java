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

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 17-Aug-2011
 * Time: 11:34:04
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/rest/poolwizard")
@SessionAttributes("poolwizard")
public class PoolRestController {
  protected static final Logger log = LoggerFactory.getLogger(LibraryRestController.class);

  @Autowired
  private SecurityManager securityManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "/librarydilutions", method = RequestMethod.GET)
  public
  @ResponseBody
  JSONObject ldRest() throws IOException {
    Collection<LibraryDilution> lds = requestManager.listAllLibraryDilutions();

    List<String> types = new ArrayList<String>(requestManager.listDistinctPlatformNames());
    Collections.sort(types);

    JSONArray platformTypeArray = new JSONArray();
    for (String type : types) {
      platformTypeArray.add(type);
    }

    JSONObject ldJSON = new JSONObject();
    JSONArray tableArray = new JSONArray();
    for (LibraryDilution ld : lds) {
      JSONArray rowArray = new JSONArray();
      rowArray.add("");
      rowArray.add(ld.getName());
      rowArray.add("");
      tableArray.add(rowArray);
    }

    ldJSON.put("aaData", tableArray);
    return ldJSON;
  }

  @RequestMapping(value = "/platformtypes", method = RequestMethod.GET)
  public
  @ResponseBody
  String platformTypesRest() throws IOException {
    List<String> names = new ArrayList<String>();
    List<String> types = new ArrayList<String>(requestManager.listDistinctPlatformNames());
    for (String name : types) {
     // names.add("\"" + name + "\"" + ":" + "\"" + name + "\"");
      names.add("\"" + name + "\"");
    }
    return "{"+LimsUtils.join(names, ",")+"}";
  }
}
