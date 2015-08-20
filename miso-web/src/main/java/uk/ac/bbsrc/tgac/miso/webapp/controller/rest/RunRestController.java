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

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.RunProcessingUtils;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.ContainerRecursionAvoidanceMixin;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.UserInfoMixin;
import uk.ac.bbsrc.tgac.miso.webapp.util.RestUtils;

import java.io.IOException;
import java.util.Collection;

/**
 * A controller to handle all REST requests for Runs
 *
 * @author Rob Davey
 * @date 01-Sep-2011
 * @since 0.1.0
 */
@Controller
@RequestMapping("/rest/run")
@SessionAttributes("run")
public class RunRestController {
  protected static final Logger log = LoggerFactory.getLogger(RunRestController.class);
  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "{runId}", method = RequestMethod.GET)
  public @ResponseBody String getRunById(@PathVariable Long runId) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      Run r = requestManager.getRunById(runId);
      if (r != null) {
        mapper.getSerializationConfig().addMixInAnnotations(SequencerPartitionContainer.class, ContainerRecursionAvoidanceMixin.class);
        mapper.getSerializationConfig().addMixInAnnotations(User.class, UserInfoMixin.class);
        return mapper.writeValueAsString(r);
      }
      return mapper.writeValueAsString(RestUtils.error("No such run with that ID.", "runId", runId.toString()));
    }
    catch (IOException ioe) {
      return mapper.writeValueAsString(RestUtils.error("Cannot retrieve run: " + ioe.getMessage(), "runId", runId.toString()));
    }
  }

  @RequestMapping(value = "/alias/{runAlias}", method = RequestMethod.GET)
  public @ResponseBody String getRunByAlias(@PathVariable String runAlias) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.getSerializationConfig().addMixInAnnotations(SequencerPartitionContainer.class, ContainerRecursionAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(User.class, UserInfoMixin.class);
    try {
      Run r = requestManager.getRunByAlias(runAlias);
      if (r != null) {
        return mapper.writeValueAsString(r);
      }
      return mapper.writeValueAsString(RestUtils.error("No such run with that alias.", "runAlias", runAlias.toString()));
    }
    catch (IOException ioe) {
      return mapper.writeValueAsString(RestUtils.error("Cannot retrieve run: " + ioe.getMessage(), "runAlias", runAlias));
    }
  }

  @RequestMapping(value = "{runAlias}/samplesheet", method = RequestMethod.GET)
  public @ResponseBody String getSampleSheetForRun(@PathVariable String runAlias) throws IOException {
    Run r = requestManager.getRunByAlias(runAlias);
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    if (r != null) {
      Collection<SequencerPartitionContainer<SequencerPoolPartition>> conts = r.getSequencerPartitionContainers();
      if (!conts.isEmpty() && conts.size() == 1) {
        return RunProcessingUtils.buildIlluminaDemultiplexCSV(r, conts.iterator().next(), "1.8", user.getLoginName());
      }
    }
    return RestUtils.error("No such run with that alias.", "runAlias", runAlias.toString()).toString();
  }
  
  @RequestMapping(method = RequestMethod.GET)
  public @ResponseBody String listAllRuns() throws IOException {
    Collection<Run> lr = requestManager.listAllRuns();
    ObjectMapper mapper = new ObjectMapper();
    mapper.getSerializationConfig().addMixInAnnotations(SequencerPartitionContainer.class, ContainerRecursionAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(User.class, UserInfoMixin.class);
    return mapper.writeValueAsString(lr);
  }
}