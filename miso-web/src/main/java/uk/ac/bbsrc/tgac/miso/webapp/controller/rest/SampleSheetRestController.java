package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response.Status;
import tools.jackson.databind.node.ObjectNode;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheet;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleSheetService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingContainerModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.SampleSheetInput;
import uk.ac.bbsrc.tgac.miso.core.util.SampleSheets;
import uk.ac.bbsrc.tgac.miso.dto.SampleSheetDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/rest/samplesheets")
public class SampleSheetRestController extends AbstractRestController {

  @Autowired
  private SampleSheetService sampleSheetService;
  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private SequencingContainerModelService sequencingContainerModelService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;

  @GetMapping
  @ResponseBody
  public List<SampleSheetDto> listByPlatform(@RequestParam String platform) throws IOException {
    PlatformType platformType = null;
    try {
      platformType = PlatformType.valueOf(platform);
    } catch (IllegalArgumentException e) {
      throw new RestException("Invalid platform: %s".formatted(platform), Status.BAD_REQUEST);
    }
    return sampleSheetService.listByPlatform(platformType).stream().map(SampleSheetDto::from).toList();
  }

  public record SampleSheetRequest(Long runId, Long instrumentModelId, Long containerModelId,
      Long sequencingParametersId, Map<String, Long> sequencingParametersIdsByInstrumentPosition,
      ObjectNode customParameters, Map<String, Map<Integer, Long>> poolIdsByInstrumentPositionAndPartition,
      Map<String, Boolean> includeSections) {
    // poolIdsByInstrumentPositionAndPartition will contain a single value with key "*" if the
    // instrument does not support multiple positions per run
  }

  @PostMapping(value = "/{sampleSheetId}/generate", produces = "application/octet-stream")
  @ResponseBody
  public HttpEntity<byte[]> generate(@PathVariable long sampleSheetId, @RequestBody SampleSheetRequest request,
      HttpServletResponse response) throws IOException {
    SampleSheet sampleSheet = RestUtils.retrieve("sample sheet", sampleSheetId, sampleSheetService, Status.NOT_FOUND);
    SampleSheetInput input = validateSampleSheetInput(sampleSheet, request);
    byte[] outputBytes = SampleSheets.generate(sampleSheet, input);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(new MediaType("text", "csv"));
    String filename = generateSampleSheetFilename(sampleSheet, input);
    MisoWebUtils.addAttachmentContentDisposition(response, filename);
    return new HttpEntity<>(outputBytes, headers);
  }

  private SampleSheetInput validateSampleSheetInput(SampleSheet sampleSheet, SampleSheetRequest request)
      throws IOException {
    SampleSheetInput input = request.runId() == null ? makeInputFromPools(request) : makeInputFromRun(request.runId());

    input.setCustomParameters(request.customParameters());

    if (request.includeSections() != null && !request.includeSections().isEmpty()) {
      request.includeSections().forEach((sectionName, include) -> {
        if (sampleSheet.getSections().stream().noneMatch(section -> Objects.equals(section.getName(), sectionName)
            && Objects.equals(section.getOptional(), Boolean.TRUE))) {
          throw new RestException("Invalid section for include config: %s".formatted(sectionName), Status.BAD_REQUEST);
        }
      });
      input.setIncludeSections(request.includeSections());
    }
    return input;
  }

  private SampleSheetInput makeInputFromRun(Long runId) throws IOException {
    Run run = RestUtils.retrieve("run", runId, runService, Status.BAD_REQUEST);
    SampleSheetInput input = new SampleSheetInput();

    InstrumentModel model = run.getSequencer().getInstrumentModel();
    input.setInstrumentModel(model);
    if (model.getPlatformType().hasContainerLevelParameters()) {
      Map<String, SequencingParameters> params = new HashMap<>();
      for (RunPosition runPos : run.getRunPositions()) {
        if (runPos.getContainer() != null) {
          params.put(runPos.getPosition().getAlias(), runPos.getSequencingParameters());
        }
      }
      input.setSequencingParametersByInstrumentPosition(params);
    } else {
      input.setSequencingParameters(run.getSequencingParameters());
    }

    Map<String, Map<Integer, Pool>> poolLayout = new HashMap<>();
    Map<String, SequencerPartitionContainer> containersByInstrumentPosition = new HashMap<>();
    if (model.getNumContainers() == 1 && run.getRunPositions().size() == 1) {
      RunPosition runPos = run.getRunPositions().iterator().next();
      if (runPos.getContainer() != null) {
        poolLayout.put("*", makePoolsByPartition(runPos));
        containersByInstrumentPosition.put("*", runPos.getContainer());
      }
    } else {
      for (RunPosition runPos : run.getRunPositions()) {
        if (runPos.getContainer() != null) {
          poolLayout.put(runPos.getPosition().getAlias(), makePoolsByPartition(runPos));
          containersByInstrumentPosition.put(runPos.getPosition().getAlias(), runPos.getContainer());
        }
      }
    }
    input.setPoolLayout(poolLayout);
    input.setContainersByInstrumentPosition(containersByInstrumentPosition);

    return input;
  }

  private static Map<Integer, Pool> makePoolsByPartition(RunPosition runPos) {
    Map<Integer, Pool> poolsByPartition = new HashMap<>();
    for (Partition partition : runPos.getContainer().getPartitions()) {
      poolsByPartition.put(partition.getPartitionNumber(), partition.getPool());
    }
    return poolsByPartition;
  }

  private SampleSheetInput makeInputFromPools(SampleSheetRequest request) throws IOException {
    SampleSheetInput input = new SampleSheetInput();

    InstrumentModel model =
        RestUtils.retrieve("instrument model", request.instrumentModelId(), instrumentModelService, Status.BAD_REQUEST);
    input.setInstrumentModel(model);
    if (model.getPlatformType().hasContainerLevelParameters()) {
      Map<String, SequencingParameters> params = new HashMap<>();
      for (Entry<String, Long> entry : request.sequencingParametersIdsByInstrumentPosition().entrySet()) {
        validateInstrumentPosition(model, entry.getKey());
        SequencingParameters positionParams =
            RestUtils.retrieve("sequencing parameters", entry.getValue(), sequencingParametersService,
                Status.BAD_REQUEST);
        params.put(entry.getKey(), positionParams);
      }
      input.setSequencingParametersByInstrumentPosition(params);
    } else {
      SequencingParameters params = RestUtils.retrieve("sequencing parameters", request.sequencingParametersId(),
          sequencingParametersService, Status.BAD_REQUEST);
      input.setSequencingParameters(params);
    }

    Map<String, Map<Integer, Pool>> poolLayout = new HashMap<>();
    SequencingContainerModel containerModel = RestUtils.retrieve("container model", request.containerModelId(),
        sequencingContainerModelService, Status.BAD_REQUEST);
    for (Entry<String, Map<Integer, Long>> positionAndMap : request.poolIdsByInstrumentPositionAndPartition()
        .entrySet()) {
      if (request.poolIdsByInstrumentPositionAndPartition().size() > 1
          || !request.poolIdsByInstrumentPositionAndPartition().containsKey("*")) {
        validateInstrumentPosition(model, positionAndMap.getKey());
      }
      Map<Integer, Pool> poolsByPartition = new HashMap<>();
      poolLayout.put(positionAndMap.getKey(), poolsByPartition);
      for (Entry<Integer, Long> partitionAndPoolId : positionAndMap.getValue().entrySet()) {
        if (partitionAndPoolId.getKey() < 1 || partitionAndPoolId.getKey() > containerModel.getPartitionCount()) {
          throw new RestException("Partition number %d is invalid for the %s container model"
              .formatted(partitionAndPoolId.getKey(), containerModel.getAlias()), Status.BAD_REQUEST);
        }
        Pool pool = null;
        if (partitionAndPoolId.getValue() != null) {
          pool = RestUtils.retrieve("pool", partitionAndPoolId.getValue(), poolService, Status.BAD_REQUEST);
        }
        poolsByPartition.put(partitionAndPoolId.getKey(), pool);
      }
    }
    input.setPoolLayout(poolLayout);

    return input;
  }

  private void validateInstrumentPosition(InstrumentModel model, String position) {
    if (model.getPositions() != null && !model.getPositions().isEmpty()) {
      for (InstrumentPosition instrumentPosition : model.getPositions()) {
        if (Objects.equals(instrumentPosition.getAlias(), position)) {
          return;
        }
      }
    }
    throw new RestException("Position '%s' is not valid for %s".formatted(position, model.getAlias()),
        Status.BAD_REQUEST);
  }

  private static String generateSampleSheetFilename(SampleSheet sampleSheet, SampleSheetInput input) {
    String firstPoolAlias = input.getPoolLayout().entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .flatMap(outerEntry -> outerEntry.getValue().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue))
        .filter(Objects::nonNull)
        .map(Pool::getAlias)
        .findFirst()
        .orElse(null);

    return "%s_%s_samplesheet.csv".formatted(
        LimsUtils.formatDate(LocalDate.now(ZoneId.systemDefault())),
        firstPoolAlias != null ? firstPoolAlias : sampleSheet.getName());
  }

}
