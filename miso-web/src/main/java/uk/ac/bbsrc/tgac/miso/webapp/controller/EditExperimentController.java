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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ExperimentDto;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;

@Controller
@RequestMapping("/experiment")
public class EditExperimentController {

  @Autowired
  private ExperimentService experimentService;

  @GetMapping(value = "/{experimentId}")
  public ModelAndView setupForm(@PathVariable Long experimentId, ModelMap model) throws IOException {
    Experiment experiment = experimentService.get(experimentId);
    if (experiment == null) throw new NotFoundException("No experiment found for ID " + experimentId.toString());
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

    model.put("experiment", experiment);
    model.put("experimentDto", mapper.writeValueAsString(Dtos.asDto(experiment)));
    model.put("consumables", experiment.getKits().stream().map(Dtos::asDto).collect(Collectors.toList()));
    model.put("consumableConfig", mapper.writeValueAsString(consumableConfig));
    model.put("runPartitions",
        experiment.getRunPartitions().stream()
            .map(entry -> new ExperimentDto.RunPartitionDto(Dtos.asDto(entry.getRun()), Dtos.asDto(entry.getPartition())))
            .collect(Collectors.toList()));
    model.put("title", "Edit Experiment");
    return new ModelAndView("/WEB-INF/pages/editExperiment.jsp", model);
  }

}
