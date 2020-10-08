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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.HierarchyEntity;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.dashi.RunLibraryQCTableRowDto;
import uk.ac.bbsrc.tgac.miso.dto.dashi.RunLibraryQcTableQcNodeDto;
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
  private RunPartitionService runPartitionService;
  @Autowired
  private RunPartitionAliquotService runPartitionAliquotService;

  @PostMapping("/metrics")
  public ModelAndView getRunLibraryQcTable(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    RunLibraryQcTableRequestDto data = validateRunLibraryQcTableRequest(form, mapper);
    List<RunLibraryQCTableRowDto> rows = new ArrayList<>();
    for (RunLibraryQcTableRequestLibraryDto item : data.getLibraryAliquots()) {
      long aliquotId = getAliquotIdFromName(item.getName());
      LibraryAliquot libraryAliquot = libraryAliquotService.get(aliquotId);
      if (libraryAliquot == null) {
        throw new ClientErrorException(String.format("Library aliquot %s not found", item.getName()));
      }
      List<RunPartitionAliquot> runLibs = runPartitionAliquotService.listByAliquotId(aliquotId);
      // RunPartitionAliquots may not already exist for all relationships, so we need to construct some of them
      List<RunPartition> runParts = runPartitionService.listByAliquot(libraryAliquot);
      for (RunPartition runPart : runParts) {
        if (runLibs.stream().noneMatch(runLib -> runLib.getRun().getId() == runPart.getRun().getId()
            && runLib.getPartition().getId() == runPart.getPartition().getId())) {
          runLibs.add(new RunPartitionAliquot(runPart.getRun(), runPart.getPartition(), libraryAliquot));
        }
      }

      runLibs = runLibs.stream()
          .filter(runLib -> item.getRunId() == null ? true : runLib.getRun().getId() == item.getRunId().longValue())
          .filter(runLib -> item.getPartition() == null ? true : runLib.getPartition().getPartitionNumber().equals(item.getPartition()))
          .collect(Collectors.toList());
      if (runLibs.isEmpty()) {
        throw new ClientErrorException("No matching run-libraries");
      }
      rows.add(makeQcTableRow(runLibs, item.getMetrics()));
    }
    model.put("title", "Run-Library Metrics");
    model.put("tableData", mapper.writeValueAsString(rows));
    return new ModelAndView("/WEB-INF/pages/runLibraryMetrics.jsp", model);
  }

  private static long getAliquotIdFromName(String name) {
    Matcher m = ALIQUOT_NAME_PATTERN.matcher(name);
    if (!m.matches()) {
      throw new ClientErrorException("Invalid library aliquot name: " + name);
    }
    return Long.parseLong(m.group(1));
  }

  private static RunLibraryQcTableRequestDto validateRunLibraryQcTableRequest(Map<String, String> form, ObjectMapper mapper)
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
    RunLibraryQcTableRequestLibraryDto template = data.getLibraryAliquots().get(0);
    for (RunLibraryQcTableRequestLibraryDto lib : data.getLibraryAliquots()) {
      if (lib.getMetrics() == null || lib.getMetrics().isEmpty()) {
        throw new ClientErrorException("No metrics specified");
      } else if (lib.getMetrics().size() != template.getMetrics().size()) {
        throw new ClientErrorException("Inconsistent metrics specified");
      }
      for (int i = 0; i < lib.getMetrics().size(); i++) {
        RunLibraryQcTableRequestMetricDto m1 = lib.getMetrics().get(i);
        RunLibraryQcTableRequestMetricDto m2 = template.getMetrics().get(i);
        if (m1.getTitle() == null || m1.getThresholdType() == null) {
          throw new ClientErrorException("Invalid metrics specified");
        } else if (!m1.getTitle().equals(m2.getTitle()) || !m1.getThresholdType().equals(m2.getThresholdType())
            || m1.getThreshold() != m2.getThreshold()) {
              throw new ClientErrorException("Inconsistent metrics specified");
        }
      }
    }
    return data;
  }

  private RunLibraryQCTableRowDto makeQcTableRow(List<RunPartitionAliquot> runLibs, List<RunLibraryQcTableRequestMetricDto> metrics)
      throws IOException {
    RunLibraryQCTableRowDto row = new RunLibraryQCTableRowDto();
    LibraryAliquot libraryAliquot = runLibs.get(0).getAliquot();
    row.setLibraryAliquot(Dtos.asDto(libraryAliquot, false));
    row.setMetrics(metrics.stream().map(RunLibraryQcTableRowMetricDto::fromRequestDto).collect(Collectors.toList()));
    List<RunLibraryQcTableQcNodeDto> qcNodes = new LinkedList<>();
    for (HierarchyEntity current = libraryAliquot; current != null; current = current.getParent()) {
      qcNodes.add(0, makeQcNode(current));
    }
    for (RunPartitionAliquot runLib : runLibs) {
      RunPartition runPartition = runPartitionService.get(runLib.getRun(), runLib.getPartition());
      if (qcNodes.stream()
          .noneMatch(qcNode -> "Pool".equals(qcNode.getEntityType()) && qcNode.getId() == runPartition.getPartition().getPool().getId())) {
        qcNodes.add(makeQcNode(runPartition.getPartition().getPool()));
      }
      if (qcNodes.stream().noneMatch(qcNode -> "Run".equals(qcNode.getEntityType()) && qcNode.getId() == runLib.getRun().getId())) {
        qcNodes.add(makeQcNode(runLib.getRun()));
      }
      qcNodes.add(makeQcNode(runPartition));
      qcNodes.add(makeQcNode(runLib));
    }
    row.setQcNodes(qcNodes);
    return row;
  }

  private static RunLibraryQcTableQcNodeDto makeQcNode(HierarchyEntity hierarchyEntity) {
    RunLibraryQcTableQcNodeDto node = new RunLibraryQcTableQcNodeDto();
    node.setEntityType(hierarchyEntity.getEntityType().getLabel());
    node.setTypeLabel(getTypeLabel(hierarchyEntity));
    node.setId(hierarchyEntity.getId());
    node.setName(hierarchyEntity.getName());
    node.setLabel(hierarchyEntity.getAlias());
    if (hierarchyEntity.getDetailedQcStatus() != null) {
      node.setQcStatusId(hierarchyEntity.getDetailedQcStatus().getId());
      node.setQcPassed(hierarchyEntity.getDetailedQcStatus().getStatus());
    }
    node.setQcNote(hierarchyEntity.getDetailedQcStatusNote());
    return node;
  }

  private static String getTypeLabel(HierarchyEntity hierarchyEntity) {
    switch (hierarchyEntity.getEntityType()) {
    case SAMPLE:
      if (LimsUtils.isDetailedSample((Sample) hierarchyEntity)) {
        return ((DetailedSample) hierarchyEntity).getSampleClass().getAlias();
      } else {
        return "Sample";
      }
    case LIBRARY:
      return "Library";
    case LIBRARY_ALIQUOT:
      return "Library Aliquot";
    default:
      throw new IllegalStateException("Unexpected entity type in hierarchy");
    }
  }

  private static RunLibraryQcTableQcNodeDto makeQcNode(Pool pool) {
    RunLibraryQcTableQcNodeDto node = new RunLibraryQcTableQcNodeDto();
    node.setEntityType("Pool");
    node.setTypeLabel("Pool");
    node.setId(pool.getId());
    node.setName(pool.getName());
    node.setLabel(pool.getAlias());
    node.setQcPassed(pool.getQcPassed());
    return node;
  }

  private static RunLibraryQcTableQcNodeDto makeQcNode(Run run) {
    RunLibraryQcTableQcNodeDto node = new RunLibraryQcTableQcNodeDto();
    node.setEntityType("Run");
    node.setTypeLabel("Run");
    node.setId(run.getId());
    node.setName(run.getName());
    node.setLabel(run.getAlias());
    node.setRunStatus(run.getHealth().getKey());
    if (run.getHealth() == HealthType.Completed) {
      node.setQcPassed(true);
    } else if (run.getHealth() == HealthType.Failed) {
      node.setQcPassed(false);
    }
    return node;
  }

  private static RunLibraryQcTableQcNodeDto makeQcNode(RunPartition runPartition) {
    Run run = runPartition.getRun();
    Partition partition = runPartition.getPartition();
    PlatformType platform = run.getPlatformType();

    RunLibraryQcTableQcNodeDto node = new RunLibraryQcTableQcNodeDto();
    node.setEntityType("RunPartition");
    node.setTypeLabel(runPartition.getRun().getPlatformType().getPartitionName());
    node.setIds(Lists.newArrayList(run.getId(), partition.getId()));
    RunPosition runPosition = runPartition.getRun().getRunPositions().stream()
        .filter(runPos -> runPos.getContainer().getId() == runPartition.getPartition().getSequencerPartitionContainer().getId())
        .findFirst().orElseThrow(() -> new IllegalStateException("No run position found matching partition container"));
    node.setLabel(run.getAlias() + " " + platform.getPartitionName() + " "
        + (runPosition.getPosition() == null ? "" : runPosition.getPosition().getAlias() + "-") + partition.getPartitionNumber());
    if (runPartition.getQcType() != null) {
      node.setQcStatusId(runPartition.getQcType().getId());
    }
    node.setQcNote(runPartition.getNotes());
    return node;
  }

  private static RunLibraryQcTableQcNodeDto makeQcNode(RunPartitionAliquot runLib) {
    Run run = runLib.getRun();
    Partition partition = runLib.getPartition();
    LibraryAliquot libraryAliquot = runLib.getAliquot();
    PlatformType platform = run.getPlatformType();

    RunLibraryQcTableQcNodeDto node = new RunLibraryQcTableQcNodeDto();
    node.setEntityType("RunPartitionLibrary");
    node.setTypeLabel("Run-Library");
    node.setIds(Lists.newArrayList(run.getId(), partition.getId(), libraryAliquot.getId()));
    RunPosition runPosition = runLib.getRun().getRunPositions().stream()
        .filter(runPos -> runPos.getContainer().getId() == runLib.getPartition().getSequencerPartitionContainer().getId())
        .findFirst().orElseThrow(() -> new IllegalStateException("No run position found matching partition container"));
    node.setLabel(run.getAlias() + " " + platform.getPartitionName() + " "
        + (runPosition.getPosition() == null ? "" : runPosition.getPosition().getAlias() + "-") + partition.getPartitionNumber()
        + " " + runLib.getAliquot().getAlias());
    node.setQcPassed(runLib.getQcPassed());
    node.setQcNote(runLib.getQcNote());
    return node;
  }

}
