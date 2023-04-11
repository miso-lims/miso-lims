package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcNodeType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.SampleQcNode;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.QcNodeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcNodeDto;
import uk.ac.bbsrc.tgac.miso.dto.dashi.RunLibraryQCTableRowDto;
import uk.ac.bbsrc.tgac.miso.dto.dashi.RunLibraryQcTableRequestDto;
import uk.ac.bbsrc.tgac.miso.dto.dashi.RunLibraryQcTableRequestLibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.dashi.RunLibraryQcTableRequestMetricDto;
import uk.ac.bbsrc.tgac.miso.dto.dashi.RunLibraryQcTableRowMetricDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;

@Controller
@RequestMapping("/runlibraries")
public class RunLibraryController {

  private static final Pattern ALIQUOT_NAME_PATTERN = Pattern.compile("LDI(\\d+)");

  @Autowired
  private LibraryAliquotService libraryAliquotService;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private QcNodeService qcNodeService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ObjectMapper mapper;

  @PostMapping("/metrics")
  public ModelAndView getRunLibraryQcTable(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    RunLibraryQcTableRequestDto data = validateRunLibraryQcTableRequest(form, mapper);

    List<RunLibraryQCTableRowDto> rows = new ArrayList<>();
    for (RunLibraryQcTableRequestLibraryDto item : data.getLibraryAliquots()) {
      long aliquotId = getAliquotIdFromName(item.getName());
      long partitionId = containerService.getPartitionIdByRunIdAndPartitionNumber(item.getRunId(), item.getPartition());
      SampleQcNode hierarchy = qcNodeService.getForRunLibrary(item.getRunId(), partitionId, aliquotId);
      if (hierarchy == null) {
        throw new NotFoundException("Run-library not found");
      }
      RunLibraryQCTableRowDto row = new RunLibraryQCTableRowDto();
      LibraryAliquot aliquot = libraryAliquotService.get(aliquotId);
      row.setLibraryAliquot(Dtos.asDto(aliquot, false));
      row.setMetrics(
          item.getMetrics().stream().map(RunLibraryQcTableRowMetricDto::fromRequestDto).collect(Collectors.toList()));
      row.setQcNodes(getNodes(hierarchy, item.getRunId(), partitionId, aliquotId));
      rows.add(row);
    }

    model.put("title", "Run-Library Metrics");
    model.put("tableData", mapper.writeValueAsString(rows));
    User user = authorizationManager.getCurrentUser();
    model.put("isRunReviewer", user.isRunReviewer() || user.isAdmin());
    return new ModelAndView("/WEB-INF/pages/runLibraryMetrics.jsp", model);
  }

  /**
   * Traverse the hierarchy and return a list of all the nodes on the direct path from the top sample
   * to the specified run-library. Nodes not on the direct path are excluded
   * 
   * @param node
   * @param runId
   * @param partitionId
   * @param aliquotId
   * @return
   */
  private List<QcNodeDto> getNodes(QcNode node, long runId, long partitionId, long aliquotId) {
    if (node.getChildren() != null && !node.getChildren().isEmpty()) {
      for (QcNode child : node.getChildren()) {
        List<QcNodeDto> list = getNodes(child, runId, partitionId, aliquotId);
        if (list != null) {
          list.add(0, Dtos.asDto(node));
          return list;
        }
      }
      return null;
    } else if (node.getEntityType() == QcNodeType.RUN_LIBRARY && idsMatch(node, runId, partitionId, aliquotId)) {
      List<QcNodeDto> list = new LinkedList<>();
      list.add(Dtos.asDto(node));
      return list;
    } else {
      return null;
    }
  }

  private static boolean idsMatch(QcNode node, long runId, long partitionId, long aliquotId) {
    return node.getIds() != null
        && node.getIds().length == 3
        && node.getIds()[0] == runId
        && node.getIds()[1] == partitionId
        && node.getIds()[2] == aliquotId;
  }

  private static long getAliquotIdFromName(String name) {
    Matcher m = ALIQUOT_NAME_PATTERN.matcher(name);
    if (!m.matches()) {
      throw new ClientErrorException("Invalid library aliquot name: " + name);
    }
    return Long.parseLong(m.group(1));
  }

  private static RunLibraryQcTableRequestDto validateRunLibraryQcTableRequest(Map<String, String> form,
      ObjectMapper mapper)
      throws IOException {
    RunLibraryQcTableRequestDto data = null;
    if (form.containsKey("data")) {
      try {
        String json = form.get("data");
        data = mapper.readValue(json, RunLibraryQcTableRequestDto.class);
      } catch (JsonProcessingException e) {
        throw new ClientErrorException("Invalid request data", e);
      }
    }

    if (data == null) {
      throw new ClientErrorException("Request data missing");
    } else if (data.getLibraryAliquots() == null || data.getLibraryAliquots().isEmpty()) {
      throw new ClientErrorException("No library aliquots specified");
    }
    for (RunLibraryQcTableRequestLibraryDto lib : data.getLibraryAliquots()) {
      if (lib.getMetrics() == null || lib.getMetrics().isEmpty()) {
        throw new ClientErrorException("No metrics specified");
      }
      for (int i = 0; i < lib.getMetrics().size(); i++) {
        RunLibraryQcTableRequestMetricDto m1 = lib.getMetrics().get(i);
        if (m1.getTitle() == null || m1.getThresholdType() == null) {
          throw new ClientErrorException("Invalid metrics specified");
        }
      }
    }
    return data;
  }

  @GetMapping("/{runId}-{partitionId}-{aliquotId}/qc-hierarchy")
  public ModelAndView getQcHierarchy(@PathVariable long runId, @PathVariable long partitionId,
      @PathVariable long aliquotId, ModelMap model)
      throws IOException {
    SampleQcNode hierarchy = qcNodeService.getForRunLibrary(runId, partitionId, aliquotId);
    if (hierarchy == null) {
      throw new NotFoundException("Run-library not found");
    }

    model.put("title", "Run-Library Hierarchy");
    model.put("selectedType", "Run-Library");
    model.put("selectedId", "[" + runId + ", " + partitionId + ", " + aliquotId + "]");
    model.put("hierarchy", mapper.writeValueAsString(Dtos.asHierarchyDto(hierarchy)));

    return new ModelAndView("/WEB-INF/pages/qcHierarchy.jsp", model);
  }

  // Uncomment and adjust constants for convenient endpoint for testing Run-Library Metrics page
  // @GetMapping("/metricstest")
  // public ModelAndView test(ModelMap model) throws IOException {
  // final long runId = 4862L;
  // final int partitionNumber = 1;
  // final String aliquot1Name = "LDI48170";

  // final String aliquot2Name = "LDI73998";
  // final long run2Id = 4862L;


  // RunLibraryQcTableRequestDto dto = new RunLibraryQcTableRequestDto();

  // dto.setReport("Some Report");
  // List<RunLibraryQcTableRequestLibraryDto> aliquots = new ArrayList<>();
  // dto.setLibraryAliquots(aliquots);

  // RunLibraryQcTableRequestLibraryDto aliquot = new RunLibraryQcTableRequestLibraryDto();
  // RunLibraryQcTableRequestLibraryDto aliquot2 = new RunLibraryQcTableRequestLibraryDto();
  // aliquots.add(aliquot);
  // aliquots.add(aliquot2);


  // aliquot.setName(aliquot1Name);
  // aliquot.setRunId(runId);
  // aliquot.setPartition(partitionNumber);
  // List<RunLibraryQcTableRequestMetricDto> metrics = new ArrayList<>();
  // aliquot.setMetrics(metrics);

  // aliquot2.setName(aliquot2Name);
  // aliquot2.setRunId(run2Id);
  // aliquot2.setPartition(partitionNumber);
  // List<RunLibraryQcTableRequestMetricDto> metrics2 = new ArrayList<>();
  // aliquot2.setMetrics(metrics2);

  // RunLibraryQcTableRequestMetricDto metric1 = new RunLibraryQcTableRequestMetricDto();
  // metrics.add(metric1);
  // metric1.setTitle("Something");
  // metric1.setThreshold(100D);
  // metric1.setThresholdType("gt");
  // metric1.setValue(120D);

  // RunLibraryQcTableRequestMetricDto metric2 = new RunLibraryQcTableRequestMetricDto();
  // metrics.add(metric2);
  // metric2.setTitle("Another Thing");
  // metric2.setThreshold(100D);
  // metric2.setThresholdType("le");
  // metric2.setValue(120D);

  // RunLibraryQcTableRequestMetricDto metric3 = new RunLibraryQcTableRequestMetricDto();
  // metrics2.add(metric3);
  // metric3.setTitle("Another Thing");
  // metric3.setThreshold(150D);
  // metric3.setThresholdType("gt");
  // metric3.setValue(562.6D);

  // RunLibraryQcTableRequestMetricDto metric4 = new RunLibraryQcTableRequestMetricDto();
  // metrics2.add(metric4);
  // metric4.setTitle("Another Thing 2");
  // metric4.setThreshold(10000D);
  // metric4.setThresholdType("gt");
  // metric4.setValue(46363D);

  // RunLibraryQcTableRequestMetricDto metric5 = new RunLibraryQcTableRequestMetricDto();
  // metrics2.add(metric5);
  // metric5.setTitle("Another Thing 3");
  // metric5.setThreshold(35D);
  // metric5.setThresholdType("lt");
  // metric5.setValue(3.8D);

  // Map<String, String> form = new HashMap<>();
  // form.put("data", mapper.writeValueAsString(dto));
  // return getRunLibraryQcTable(form, model);
  // }

}
