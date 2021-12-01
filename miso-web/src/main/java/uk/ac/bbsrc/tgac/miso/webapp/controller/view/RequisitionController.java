package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.RequisitionService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.RequisitionDto;
import uk.ac.bbsrc.tgac.miso.dto.RunPartitionAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.dto.run.RunDto;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/requisition")
public class RequisitionController {

  @Autowired
  private RequisitionService requisitionService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private RunService runService;
  @Autowired
  private RunPartitionAliquotService runPartitionAliquotService;
  @Autowired
  private AuthorizationManager authorizationManager;

  private final ListItemsPage listPage = new ListItemsPage("requisition") {

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("isAdmin", authorizationManager.isAdminUser());
    }

  };

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listPage.list(model);
  }

  @GetMapping("/new")
  public ModelAndView create(ModelMap model) throws IOException {
    Requisition requisition = new Requisition();
    model.put("title", "New Requisition");
    return setupForm(requisition, PageMode.CREATE, model);
  }

  @GetMapping("/{id}")
  public ModelAndView edit(@PathVariable long id, ModelMap model) throws IOException {
    Requisition requisition = requisitionService.get(id);
    if (requisition == null) {
      throw new NotFoundException("No requisition found for ID: " + id);
    }
    model.put("title", "Requisition " + id);
    List<Sample> samples = sampleService.list(0, 0, false, "id", PaginationFilter.requisitionId(id));

    Set<Sample> extractions = new HashSet<>();
    for (Sample sample : samples) {
      extractions.addAll(sampleService.getChildren(sample.getId(), SampleStock.CATEGORY_NAME));
    }
    List<SampleDto> extractionDtos = extractions.stream()
        .map(sam -> Dtos.asDto(sam, false))
        .collect(Collectors.toList());
    model.put("extractions", extractionDtos);
    
    Set<Library> libraries = new HashSet<>();
    for (Sample sample : samples) {
      libraries.addAll(libraryService.getSampleDescendants(sample.getId()));
    }
    List<LibraryDto> libraryDtos = libraries.stream()
        .map(lib -> Dtos.asDto(lib, false))
        .collect(Collectors.toList());
    model.put("libraries", libraryDtos);

    Set<Run> runs = new HashSet<>();
    Set<RunPartitionAliquot> runLibraries = new HashSet<>();
    for (Library library : libraries) {
      List<Run> libraryRuns = runService.listByLibraryId(library.getId());
      List<RunPartitionAliquot> libraryRunLibraries = runPartitionAliquotService.listByLibraryId(library.getId());
      for (Run run : libraryRuns) {
        for (SequencerPartitionContainer container : run.getSequencerPartitionContainers()) {
          for (Partition partition : container.getPartitions()) {
            if (partition.getPool() != null) {
              for (PoolElement poolElement : partition.getPool().getPoolContents()) {
                if (poolElement.getAliquot().getLibraryId().longValue() == library.getId()
                    && libraryRunLibraries.stream().noneMatch(runlib -> runlib.getRun().getId() == run.getId()
                        && runlib.getPartition().getId() == partition.getId())) {
                  libraryRunLibraries.add(new RunPartitionAliquot(run, partition, poolElement.getAliquot()));
                }
              }
            }
          }
        }
      }
      runs.addAll(libraryRuns);
      runLibraries.addAll(libraryRunLibraries);
    }

    List<RunDto> runDtos = runs.stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
    model.put("runs", runDtos);
    List<RunPartitionAliquotDto> runLibraryDtos = runLibraries.stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
    model.put("runLibraries", runLibraryDtos);

    return setupForm(requisition, PageMode.EDIT, model);
  }

  private ModelAndView setupForm(Requisition requisition, PageMode pageMode, ModelMap model) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    model.put(PageMode.PROPERTY, pageMode.getLabel());
    model.put("requisition", requisition);
    model.put("requisitionDto", mapper.writeValueAsString(RequisitionDto.from(requisition)));
    return new ModelAndView("/WEB-INF/pages/editRequisition.jsp", model);
  }

}
