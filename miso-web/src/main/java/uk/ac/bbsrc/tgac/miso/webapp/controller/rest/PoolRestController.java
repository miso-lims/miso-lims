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

import com.eaglegenomics.simlims.core.User;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.UserInfoMixin;
import uk.ac.bbsrc.tgac.miso.webapp.util.RestUtils;

import java.util.Collections;
import java.util.List;

/**
 * A controller to handle all REST requests for Pools
 *
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 17-Aug-2011
 * Time: 11:34:04
 */
@Controller
@RequestMapping("/rest/pool")
@SessionAttributes("pool")
public class PoolRestController {
  protected static final Logger log = LoggerFactory.getLogger(LibraryRestController.class);

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "{poolId}", method = RequestMethod.GET)
  public @ResponseBody String getPoolById(@PathVariable Long poolId) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      Pool p = requestManager.getPoolById(poolId);
      if (p != null) {
        mapper.getSerializationConfig().addMixInAnnotations(User.class, UserInfoMixin.class);
        return mapper.writeValueAsString(p);
      }
      return mapper.writeValueAsString(RestUtils.error("No such pool with that ID.", "poolId", poolId.toString()));
    }
    catch (IOException ioe) {
      return mapper.writeValueAsString(RestUtils.error("Cannot retrieve pool: " + ioe.getMessage(), "poolId", poolId.toString()));
    }
  }

  @RequestMapping(value = "/wizard/librarydilutions", method = RequestMethod.GET)
  public @ResponseBody JSONObject ldRest() throws IOException {
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

  @RequestMapping(value = "/wizard/platformtypes", method = RequestMethod.GET)
  public @ResponseBody String platformTypesRest() throws IOException {
    List<String> names = new ArrayList<String>();
    List<String> types = new ArrayList<String>(requestManager.listDistinctPlatformNames());
    for (String name : types) {
      names.add("\"" + name + "\"");
    }
    return "{"+LimsUtils.join(names, ",")+"}";
  }
}
