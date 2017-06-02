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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;

@Controller
@RequestMapping("/container")
@SessionAttributes("container")
public class EditSequencerPartitionContainerController {
  protected static final Logger log = LoggerFactory.getLogger(EditSequencerPartitionContainerController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private RunService runService;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  @RequestMapping(value = "/rest/changes", method = RequestMethod.GET)
  public @ResponseBody Collection<ChangeLog> jsonRestChanges() throws IOException {
    return changeLogService.listAll("SequencerPartitionContainer");
  }

  @ModelAttribute("platformTypes")
  public Collection<String> populatePlatformTypes() {
    return PlatformType.getKeys();
  }

  @ModelAttribute("platforms")
  public Collection<Platform> populatePlatforms() throws IOException {
    return requestManager.listAllPlatforms();
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView setupForm(ModelMap model) throws IOException {
    return setupForm(SequencerPartitionContainerImpl.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/{containerId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long containerId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      SequencerPartitionContainer container = null;
      if (containerId == SequencerPartitionContainerImpl.UNSAVED_ID) {
        container = new SequencerPartitionContainerImpl(user);
        model.put("title", "New Container");
      } else {
        container = requestManager.getSequencerPartitionContainerById(containerId);
        model.put("title", container.getPlatform().getPlatformType().getContainerName() + " " + containerId);
      }

      model.put("formObj", container);
      model.put("container", container);
      model.put("containerRuns", runService.listByContainerId(container.getId()));
      return new ModelAndView("/pages/editSequencerPartitionContainer.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show container", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("container") SequencerPartitionContainer container, ModelMap model, SessionStatus session)
      throws IOException, MalformedRunException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!container.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }

      for (Partition partition : container.getPartitions()) {
        if (partition.getPool() != null) {
          Pool pool = partition.getPool();
          pool.setLastModifier(user);
        }
      }
      container.setLastModifier(user);
      long containerId = requestManager.saveSequencerPartitionContainer(container);
      session.setComplete();
      model.clear();
      return "redirect:/miso/container/" + containerId;
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save container", ex);
      }
      throw ex;
    }
  }
}
