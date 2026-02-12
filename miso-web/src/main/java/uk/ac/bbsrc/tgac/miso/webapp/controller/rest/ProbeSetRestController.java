package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSet;
import uk.ac.bbsrc.tgac.miso.core.service.ProbeSetService;
import uk.ac.bbsrc.tgac.miso.dto.ProbeDto;
import uk.ac.bbsrc.tgac.miso.dto.ProbeSetDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/probesets")
public class ProbeSetRestController extends AbstractRestController {

  private static final String TYPE_LABEL = "Probe Set";

  @Autowired
  private ProbeSetService probeSetService;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  @GetMapping
  public @ResponseBody List<ProbeSetDto> query(@RequestParam String q) throws IOException {
    List<ProbeSet> results = probeSetService.searchByName(q);
    if (results == null) {
      return Collections.emptyList();
    } else {
      return results.stream().map(ProbeSetDto::from).toList();
    }
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody ProbeSetDto create(@RequestBody ProbeSetDto dto) throws IOException {
    return RestUtils.createObject(TYPE_LABEL, dto, ProbeSetRestController::makeProbeSetForCreate, probeSetService,
        ProbeSetDto::from);
  }

  private static ProbeSet makeProbeSetForCreate(ProbeSetDto dto) {
    // Probes may have come from a sample, which means they would have sample probe IDs, which we need
    // to clear so they can be saved as probe set probes
    if (dto.getProbes() != null) {
      for (ProbeDto probe : dto.getProbes()) {
        probe.setId(null);
      }
    }
    return dto.to();
  }

  @PutMapping("/{id}")
  public @ResponseBody ProbeSetDto update(@RequestBody ProbeSetDto dto, @PathVariable long id) throws IOException {
    return RestUtils.updateObject(TYPE_LABEL, id, dto, ProbeSetDto::to, probeSetService, ProbeSetDto::from);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete(TYPE_LABEL, ids, probeSetService);
  }

  @PutMapping("/{probeSetId}/probes")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode updateProbes(@PathVariable long probeSetId, @RequestBody List<ProbeDto> dtos)
      throws IOException {
    ProbeSet probeSet = RestUtils.retrieve(TYPE_LABEL, probeSetId, probeSetService);
    probeSet.setProbes(dtos.stream().map(ProbeDto::toProbeSetProbe).collect(Collectors.toSet()));
    List<ProbeSet> probeSets = new ArrayList<>();
    probeSets.add(probeSet);
    return asyncOperationManager.startAsyncBulkUpdate(TYPE_LABEL, probeSets, probeSetService);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, ProbeSet.class, probeSetService, ProbeSetDto::from);
  }

}
