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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ExperimentDto;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;

@Controller
@RequestMapping("/experiment")
@SessionAttributes("experiment")
public class EditExperimentController {
  protected static final Logger log = LoggerFactory.getLogger(EditExperimentController.class);

  @Autowired
  private ExperimentService experimentService;

  @Autowired
  private SecurityManager securityManager;

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return experimentService.getColumnSizes();
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("experiment") Experiment experiment, ModelMap model, SessionStatus session)
      throws IOException, MalformedExperimentException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!experiment.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }
      experiment.setLastModifier(user);
      experimentService.save(experiment);
      session.setComplete();
      model.clear();
      return "redirect:/miso/experiment/" + experiment.getId();
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save Experiment", ex);
      }
      throw ex;
    }
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @RequestMapping(value = "/{experimentId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long experimentId, ModelMap model) throws IOException {
    Experiment experiment = experimentService.get(experimentId);
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    if (experiment == null) {
      throw new SecurityException("No such Experiment");
    }
    if (!experiment.userCanRead(user)) {
      throw new SecurityException("Permission denied.");
    }
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode consumableConfig = mapper.createObjectNode();
    consumableConfig.put("experimentId", experiment.getId());
    Stream
        .<KitDescriptor> concat(//
            Stream.of(experiment.getLibrary().getKitDescriptor()), //
            experiment.getRunPartitions().stream()//
                .map(RunPartition::getPartition)//
                .flatMap(partition -> Stream.of(partition.getSequencerPartitionContainer().getClusteringKit(),
                    partition.getSequencerPartitionContainer().getMultiplexingKit())))//
        .filter(Objects::nonNull)//
        .distinct()//
        .map(Dtos::asDto)//
        .forEach(
            consumableConfig.putArray("allowedDescriptors")::addPOJO);

    model.put("formObj", experiment);
    model.put("experiment", experiment);
    model.put("consumables", experiment.getKits().stream().map(Dtos::asDto).collect(Collectors.toList()));
    model.put("consumableConfig", mapper.writeValueAsString(consumableConfig));
    model.put("runPartitions",
        experiment.getRunPartitions().stream()
            .map(entry -> new ExperimentDto.RunPartitionDto(Dtos.asDto(entry.getRun()), Dtos.asDto(entry.getPartition())))
            .collect(Collectors.toList()));
    model.put("owners", LimsSecurityUtils.getPotentialOwners(user, experiment, securityManager.listAllUsers()));
    model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, experiment, securityManager.listAllUsers()));
    model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, experiment, securityManager.listAllGroups()));
    model.put("title", "Edit Experiment");
    return new ModelAndView("/pages/editExperiment.jsp", model);
  }

}
