package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringBlankOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PartitionDto;
import uk.ac.bbsrc.tgac.miso.dto.RunPartitionAliquotDto;
import uk.ac.bbsrc.tgac.miso.webapp.context.ExternalUriBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.util.JsonArrayCollector;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;
import uk.ac.bbsrc.tgac.miso.webapp.util.RunMetricsSource;

@Controller
@RequestMapping("/run")
public class EditRunController {

  private static final ServiceLoader<RunMetricsSource> METRICS = ServiceLoader.load(RunMetricsSource.class);

  protected static final Logger log = LoggerFactory.getLogger(EditRunController.class);

  /**
   * Get a stream of source of metrics
   * 
   * Normally, metrics collected by run scanner are stored in the MISO database, but it is possible to
   * provide others here.
   */
  public Stream<RunMetricsSource> getSources() {
    return Stream.concat(Stream.of(Run::getMetrics), StreamSupport.stream(METRICS.spliterator(), false));
  }

  @Autowired
  private RunService runService;
  @Autowired
  private RunPartitionService runPartitionService;
  @Autowired
  private RunPartitionAliquotService runPartitionAliquotService;
  @Autowired
  private InstrumentService instrumentService;
  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private SopService sopService;
  @Autowired(required = false)
  private IssueTrackerManager issueTrackerManager;
  @Autowired
  private ExternalUriBuilder externalUriBuilder;
  @Autowired
  private IndexChecker indexChecker;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ObjectMapper mapper;

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  @GetMapping("/new/{instrumentId}")
  public ModelAndView newUnassignedRun(@PathVariable Long instrumentId, ModelMap model) throws IOException {
    Instrument instrument = instrumentService.get(instrumentId);
    if (instrument == null) {
      throw new NotFoundException("No instrument found with ID " + instrumentId);
    } else if (instrument.getInstrumentModel().getInstrumentType() != InstrumentType.SEQUENCER) {
      throw new ClientErrorException("Selected instrument is not a sequencer");
    }
    Run run = instrument.getInstrumentModel().getPlatformType().createRun();
    run.setSequencer(instrument);
    return setupForm(run, model);
  }

  @GetMapping("/{runId}")
  public ModelAndView setupForm(@PathVariable Long runId, ModelMap model) throws IOException {
    Run run = runService.get(runId);
    if (run == null)
      throw new NotFoundException("No run found with ID " + runId);
    return setupForm(run, model);
  }

  @GetMapping("/alias/{runAlias}")
  public ModelAndView setupForm(@PathVariable String runAlias, ModelMap model) throws IOException {
    Run run = runService.getRunByAlias(runAlias);
    if (run == null)
      throw new NotFoundException("No run found with alias " + runAlias);
    return setupForm(run, model);
  }

  public ModelAndView setupForm(Run run, ModelMap model) throws IOException {
    if (run.getId() == Run.UNSAVED_ID) {
      model.put("title", "New Run");
      model.put("multiplexed", false);
      model.put("metrics", "[]");
      model.put("partitionNames", "[]");
    } else {
      model.put("title", "Run " + run.getId());
      model.put("metrics",
          getSources().filter(Objects::nonNull).map(source -> source.fetchMetrics(run))
              .filter(metrics -> !isStringBlankOrNull(metrics))
              .collect(new JsonArrayCollector()));
      if (run.getSequencerPartitionContainers().size() == 1) {
        model.put("partitionNames", mapper.writeValueAsString(
            run.getSequencerPartitionContainers().get(0).getPartitions().stream()
                .sorted(Comparator.comparing(Partition::getPartitionNumber))
                .map(partition -> partition.getPool() == null ? "N/A" : partition.getPool().getAlias())
                .collect(Collectors.toList())));
      } else {
        model.put("partitionNames", "[]");
      }
      model.put("runReportLinks", externalUriBuilder.getUris(run));
    }

    model.put("runPositions", run.getRunPositions().stream().map(Dtos::asDto).collect(Collectors.toList()));
    model.put("runPartitions",
        run.getSequencerPartitionContainers().stream().flatMap(container -> container.getPartitions().stream())
            .map(WhineyFunction.rethrow(partition -> {
              PartitionDto dto = Dtos.asDto(partition, false, indexChecker);
              RunPartition runPartition = runPartitionService.get(run, partition);
              if (runPartition != null) {
                if (runPartition.getQcType() != null) {
                  dto.setQcType(runPartition.getQcType().getId());
                  dto.setQcNotes(runPartition.getNotes());
                }
                if (runPartition.getPurpose() != null) {
                  dto.setRunPurposeId(runPartition.getPurpose().getId());
                }
              } else {
                dto.setQcNotes("");
              }
              return dto;
            })).collect(Collectors.toList()));
    model.put("runAliquots", getRunAliquots(run));
    MisoWebUtils.addIssues(issueTrackerManager, () -> issueTrackerManager.searchIssues(run.getAlias()), model);
    model.put("run", run);

    ObjectNode partitionConfig = mapper.createObjectNode();
    partitionConfig.put("platformType", run.getPlatformType().name());
    partitionConfig.put("instrumentModelId", run.getSequencer().getInstrumentModel().getId());
    partitionConfig.put("runId", run.getId());
    partitionConfig.put("isFull", run.isFull());
    partitionConfig.put("showContainer", true);
    partitionConfig.put("sequencingParametersId",
        run.getSequencingParameters() == null ? 0 : run.getSequencingParameters().getId());
    partitionConfig.put("showPool", true);
    model.put("partitionConfig", mapper.writeValueAsString(partitionConfig));
    model.put("experiments",
        experimentService.listAllByRunId(run.getId()).stream().map(expt -> Dtos.asDto(expt))
            .collect(Collectors.toList()));
    ObjectNode experimentConfig = mapper.createObjectNode();
    experimentConfig.put("runId", run.getId());
    model.put("experimentConfiguration", mapper.writeValueAsString(experimentConfig));

    model.put("runDto", mapper.writeValueAsString(Dtos.asDto(run)));

    ObjectNode formConfig = mapper.createObjectNode();
    User user = authorizationManager.getCurrentUser();
    formConfig.put("isAdmin", user.isAdmin());
    formConfig.put("isRunReviewer", user.isRunReviewer() || user.isAdmin());
    MisoWebUtils.addJsonArray(mapper, formConfig, "sops", sopService.listByCategory(SopCategory.RUN), Dtos::asDto);
    model.put("formConfig", mapper.writeValueAsString(formConfig));

    return new ModelAndView("/WEB-INF/pages/editRun.jsp", model);
  }

  private List<RunPartitionAliquotDto> getRunAliquots(Run run) throws IOException {
    List<RunPartitionAliquot> runPartitionAliquots = runPartitionAliquotService.listByRunId(run.getId());
    List<RunPartitionAliquotDto> dtos = new ArrayList<>();
    for (int i = 0; i < runPartitionAliquots.size(); i++) {
      RunPartitionAliquotDto dto = Dtos.asDto(runPartitionAliquots.get(i));
      // Add id for DataTables
      dto.setId(Long.valueOf(i));
      dtos.add(dto);
    }
    return dtos;
  }

}
