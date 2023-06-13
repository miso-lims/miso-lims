package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.RequisitionService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.AssayDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.RequisitionDto;
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
  @Autowired
  private ObjectMapper mapper;

  private static class ListRequisitionsPage extends ListItemsPage {

    private final AuthorizationManager authorizationManager;

    public ListRequisitionsPage(AuthorizationManager authorizationManager, ObjectMapper mapper) {
      super("requisition", mapper);
      this.authorizationManager = authorizationManager;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("isAdmin", authorizationManager.isAdminUser());
    }
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return new ListRequisitionsPage(authorizationManager, mapper).list(model);
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
    return setupEditForm(requisition, model);
  }


  @GetMapping("/alias/{alias}")
  public ModelAndView edit(@PathVariable String alias, ModelMap model) throws IOException {
    Requisition requisition = requisitionService.getByAlias(alias);
    if (requisition == null) {
      throw new NotFoundException("No requisition found with alias: " + alias);
    }
    return setupEditForm(requisition, model);
  }


  private ModelAndView setupEditForm(Requisition requisition, ModelMap model) throws IOException {
    model.put("title", "Requisition " + requisition.getId());

    List<Sample> samples = sampleService.list(0, 0, false, "id", PaginationFilter.requisitionId(requisition.getId()));

    // algorithm to go through all requisitioned samples' projects' assays, and return all the assayIds
    // of the mutual assays
    Map<Long, Integer> assayMap = new HashMap<Long, Integer>();
    for (int i = 0; i < samples.size(); i++) {
      List<AssayDto> assayLst = Dtos.asDto(samples.get(i).getProject(), true).getAssays();
      for (int j = 0; j < assayLst.size(); j++) {
        Long curAssayId = assayLst.get(j).getId();
        if (assayMap.containsKey(curAssayId)) {
          int freq = assayMap.get(curAssayId);
          assayMap.put(curAssayId, freq + 1);
        } else {
          assayMap.put(curAssayId, 1);
        }
      }
    }
    List<Long> assayIds = new ArrayList<>();
    for (Map.Entry<Long, Integer> entry : assayMap.entrySet()) {
      if (entry.getValue() == samples.size()) {
        assayIds.add(entry.getKey());
      }
    }
    model.put("potentialAssays", mapper.writeValueAsString(assayIds));

    List<Sample> supplementalSamples =
        sampleService.list(0, 0, false, "id", PaginationFilter.supplementalToRequisitionId(requisition.getId()));
    Set<Long> sampleIds = Stream.concat(samples.stream(), supplementalSamples.stream())
        .map(Sample::getId)
        .collect(Collectors.toSet());
    List<Sample> extractions = sampleService.getChildren(
        sampleIds,
        SampleStock.CATEGORY_NAME);
    List<SampleDto> extractionDtos = extractions.stream()
        .map(sam -> Dtos.asDto(sam, false))
        .collect(Collectors.toList());
    model.put("extractions", extractionDtos);

    List<Long> libraryIds = libraryService.listIdsByAncestorSampleIds(sampleIds);
    List<Run> runs = runService.listByLibraryIdList(libraryIds);
    List<RunDto> runDtos = runs.stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
    model.put("runs", runDtos);

    return setupForm(requisition, PageMode.EDIT, model);
  }

  private ModelAndView setupForm(Requisition requisition, PageMode pageMode, ModelMap model)
      throws JsonProcessingException {
    model.put(PageMode.PROPERTY, pageMode.getLabel());
    model.put("requisition", requisition);
    model.put("requisitionDto", mapper.writeValueAsString(RequisitionDto.from(requisition)));

    // if creating a requisition, return empty variable used in editRequisition.jsp to prevent errors
    if (!model.containsKey("potentialAssays")) {
      List<Long> emptyAssayIds = new ArrayList<>();
      model.put("potentialAssays", mapper.writeValueAsString(emptyAssayIds));
    }
    return new ModelAndView("/WEB-INF/pages/editRequisition.jsp", model);
  }

}


