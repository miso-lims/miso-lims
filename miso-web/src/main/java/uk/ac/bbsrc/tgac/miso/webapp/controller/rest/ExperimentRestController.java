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

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ExperimentDto;
import uk.ac.bbsrc.tgac.miso.dto.KitConsumableDto;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.KitService;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;

@Controller
public class ExperimentRestController extends RestController {

  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private KitService kitService;
  @Autowired
  private RunService runService;

  @RequestMapping(value = "/rest/experiment/{experimentId}/addkit", method = RequestMethod.POST, produces = "application/json")
  public @ResponseBody KitConsumableDto addKit(@PathVariable Long experimentId, @RequestBody KitConsumableDto dto) throws IOException {
    Experiment experiment = experimentService.get(experimentId);
    if (experiment == null) {
      throw new RestException("No such experiment.", Status.NOT_FOUND);
    }
    Kit kit = kitService.getKitByLotNumber(dto.getLotNumber());
    if (kit == null) {
      kit = Dtos.to(dto);
      kitService.saveKit(kit);
    } else {
      if (!kit.getKitDescriptor().getId().equals(dto.getDescriptor().getId())) {
        throw new RestException("Kit exists, but kit type does not match.", Status.BAD_REQUEST);
      }
    }
    long savedKitId = kit.getId();
    if (experiment.getKits().stream().noneMatch(k -> k.getId() == savedKitId)) {
      experiment.addKit(kit);
    }
    experimentService.save(experiment);
    return Dtos.asDto(kit);
  }

  @RequestMapping(value = "/rest/experiment/{experimentId}/add", method = RequestMethod.POST, produces = "application/json")
  public @ResponseBody ExperimentDto addPartition(@PathVariable Long experimentId, @RequestParam("runId") Long runId,
      @RequestParam("partitionId") Long partitionId) throws IOException {
    Experiment experiment = experimentService.get(experimentId);
    if (experiment == null) {
      throw new RestException("No such experiment.", Status.NOT_FOUND);
    }
    Run run = runService.get(runId);
    if (run == null) {
      throw new RestException("No such run.", Status.NOT_FOUND);
    }
    Partition partition = containerService.getPartition(partitionId);
    if (partition == null) {
      throw new RestException("No such partition.", Status.NOT_FOUND);
    }
    if (run.getSequencerPartitionContainers().stream().flatMap(container -> container.getPartitions().stream())
        .noneMatch(p -> p.getId() == partitionId)) {
      throw new RestException("Partition not in run.", Status.BAD_REQUEST);
    }
    if (experiment.getRunPartitions().stream()
        .noneMatch(rp -> rp.getPartition().getId() == partition.getId() && rp.getRun().getId() == run.getId())) {
      RunPartition rp = new RunPartition();
      rp.setExperiment(experiment);
      rp.setPartition(partition);
      rp.setRun(run);

      experiment.getRunPartitions().add(rp);
      experimentService.save(experiment);
    }
    return get(experimentId);
  }

  @RequestMapping(value = "/rest/experiment", method = RequestMethod.POST, produces = "application/json")
  public @ResponseBody ExperimentDto create(@RequestBody ExperimentDto dto) throws IOException {
    Experiment experiment = Dtos.to(dto);
    experiment.setId(Experiment.UNSAVED_ID);
    long id = experimentService.save(experiment);
    return get(id);
  }

  @RequestMapping(value = "/rest/experiment/{experimentId}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody ExperimentDto get(@PathVariable Long experimentId) throws IOException {
    Experiment experiment = experimentService.get(experimentId);
    if (experiment == null) {
      throw new RestException("No such experiment.", Status.NOT_FOUND);
    }
    return Dtos.asDto(experiment);
  }

  @RequestMapping(value = "/rest/experiments", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody Collection<Experiment> list() throws IOException {
    return experimentService.listAll();
  }

  @RequestMapping(value = "/rest/experiments/changes", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody Collection<ChangeLog> listChanges() throws IOException {
    return changeLogService.listAll("Experiment");
  }

}