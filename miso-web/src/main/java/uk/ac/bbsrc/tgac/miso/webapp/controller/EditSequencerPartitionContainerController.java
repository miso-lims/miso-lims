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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.PlatformService;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;

@Controller
@RequestMapping("/container")
@SessionAttributes("container")
public class EditSequencerPartitionContainerController {
  protected static final Logger log = LoggerFactory.getLogger(EditSequencerPartitionContainerController.class);

  @Autowired
  private ContainerService containerService;
  @Autowired
  private PlatformService platformService;
  @Autowired
  private RunService runService;
  @Autowired
  private SecurityManager securityManager;

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
      SequencerPartitionContainer saved = containerService.create(container);
      session.setComplete();
      model.clear();
      return "redirect:/miso/container/" + saved.getId();
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save container", ex);
      }
      throw ex;
    }
  }

  public void setContainerService(ContainerService containerService) {
    this.containerService = containerService;
  }

  public void setPlatformService(PlatformService platformService) {
    this.platformService = platformService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @RequestMapping(value = "/new/{platformId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable("platformId") Long platformId, @RequestParam("count") int partitionCount, ModelMap model)
      throws IOException {
    SequencerPartitionContainer container = new SequencerPartitionContainerImpl();
    container.setPlatform(platformService.get(platformId));

    model.put("title", "New " + container.getPlatform().getPlatformType().getContainerName());

    if (!container.getPlatform().getPartitionSizes().contains(partitionCount)) {
      throw new IllegalArgumentException("Invalid number of partitions: " + partitionCount);
    }
    container.setPartitions(
        IntStream.range(0, partitionCount).mapToObj(number -> new PartitionImpl(container, number + 1)).collect(Collectors.toList()));
    return setupForm(container, model);
  }

  @RequestMapping(value = "/{containerId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long containerId, ModelMap model) throws IOException {
    SequencerPartitionContainer container = containerService.get(containerId);
    model.put("title", container.getPlatform().getPlatformType().getContainerName() + " " + containerId);
    return setupForm(container, model);
  }

  private ModelAndView setupForm(SequencerPartitionContainer container, ModelMap model) throws IOException {
    model.put("container", container);
    model.put("containerPartitions", container.getPartitions().stream().map(Dtos::asDto).collect(Collectors.toList()));
    model.put("containerRuns", runService.listByContainerId(container.getId()).stream().map(Dtos::asDto).collect(Collectors.toList()));
    return new ModelAndView("/pages/editSequencerPartitionContainer.jsp", model);
  }
}
