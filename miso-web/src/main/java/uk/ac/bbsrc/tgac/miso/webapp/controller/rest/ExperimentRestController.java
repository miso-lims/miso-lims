package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.core.service.KitService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ExperimentDto;
import uk.ac.bbsrc.tgac.miso.dto.KitConsumableDto;

@Controller
@RequestMapping("/rest/experiments")
public class ExperimentRestController extends RestController {

  @Autowired
  private ContainerService containerService;
  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private KitService kitService;
  @Autowired
  private RunService runService;

  @PostMapping(value = "/{experimentId}/addkit", produces = "application/json")
  public @ResponseBody KitConsumableDto addKit(@PathVariable Long experimentId, @RequestBody KitConsumableDto dto)
      throws IOException {
    Experiment experiment = experimentService.get(experimentId);
    if (experiment == null) {
      throw new RestException("No such experiment.", Status.NOT_FOUND);
    }
    Kit kit = kitService.getKitByLotNumber(dto.getLotNumber());
    if (kit == null) {
      kit = Dtos.to(dto);
      kitService.saveKit(kit);
    } else {
      if (kit.getKitDescriptor().getId() != (dto.getDescriptor().getId())) {
        throw new RestException("Kit exists, but kit type does not match.", Status.BAD_REQUEST);
      }
    }
    long savedKitId = kit.getId();
    if (experiment.getKits().stream().noneMatch(k -> k.getId() == savedKitId)) {
      experiment.addKit(kit);
    }
    experimentService.update(experiment);
    return Dtos.asDto(kit);
  }

  @PostMapping(value = "/{experimentId}/add", produces = "application/json")
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
      experimentService.update(experiment);
    }
    return get(experimentId);
  }

  @PostMapping(produces = "application/json")
  public @ResponseBody ExperimentDto create(@RequestBody ExperimentDto dto) throws IOException {
    return RestUtils.createObject("Experiment", dto, Dtos::to, experimentService, Dtos::asDto);
  }

  @PutMapping("/{experimentId}")
  public @ResponseBody ExperimentDto update(@PathVariable long experimentId, @RequestBody ExperimentDto dto)
      throws IOException {
    return RestUtils.updateObject("Experiment", experimentId, dto, Dtos::to, experimentService, Dtos::asDto);
  }

  @GetMapping(value = "/{experimentId}", produces = "application/json")
  public @ResponseBody ExperimentDto get(@PathVariable Long experimentId) throws IOException {
    return RestUtils.getObject("Experiment", experimentId, experimentService, Dtos::asDto);
  }

  @GetMapping(produces = "application/json")
  public @ResponseBody List<ExperimentDto> list() throws IOException {
    Collection<Experiment> experiments = experimentService.list();
    return experiments.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Experiment", ids, experimentService);
  }

}
