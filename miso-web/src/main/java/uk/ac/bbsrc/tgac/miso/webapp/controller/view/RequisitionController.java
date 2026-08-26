package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.RequisitionService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.RequisitionDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
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
  private AuthorizationManager authorizationManager;
  @Autowired
  private JsonMapper mapper;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSampleMode;

  private static class ListRequisitionsPage extends ListItemsPage {

    private final AuthorizationManager authorizationManager;

    public ListRequisitionsPage(AuthorizationManager authorizationManager, JsonMapper mapper) {
      super("requisition", mapper);
      this.authorizationManager = authorizationManager;
    }

    @Override
    protected void writeConfiguration(JsonMapper mapper, ObjectNode config) throws IOException {
      config.put("isAdmin", authorizationManager.isAdminUser());
    }
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    model.put("title", "Requisitions");
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

    List<Sample> requisitionedSamples =
        sampleService.list(0, 0, false, "id", PaginationFilter.requisitionId(requisition.getId()));
    List<Library> requisitionedLibraries =
        libraryService.list(0, 0, false, "id", PaginationFilter.requisitionId(requisition.getId()));

    // used to ensure not all assays are available if all requisitioned items' projects have no
    // assigned assays
    model.put("numberOfRequisitionedItems", requisitionedSamples.size() + requisitionedLibraries.size());
    model.put("potentialAssayIds", mapper.writeValueAsString(
        getPotentialAssayIds(requisitionedSamples, requisitionedLibraries, requisition.getAssays())));

    return setupForm(requisition, PageMode.EDIT, model);
  }

  private ModelAndView setupForm(Requisition requisition, PageMode pageMode, ModelMap model) {
    model.put(PageMode.PROPERTY, pageMode.getLabel());
    model.put("requisition", requisition);
    model.put("requisitionDto", mapper.writeValueAsString(RequisitionDto.from(requisition)));
    return new ModelAndView("/WEB-INF/pages/editRequisition.jsp", model);
  }

  // function to get potential assay ids to for the requisition assay dropdown
  private List<Long> getPotentialAssayIds(List<Sample> samples, List<Library> libraries,
      Collection<Assay> requisitionAssays) {
    Map<Long, Integer> assayMap = new HashMap<Long, Integer>();
    List<Long> uniqueProjectIds = new ArrayList<>();

    Stream.concat(samples.stream().map(Sample::getProject), libraries.stream().map(lib -> lib.getSample().getProject()))
        .forEach(project -> {
          if (uniqueProjectIds.contains(project.getId())) {
            return;
          }
          uniqueProjectIds.add(project.getId());
          for (Assay element : project.getAssays()) {
            Long curAssayId = element.getId();
            if (!element.isArchived()) {
              Integer freq = assayMap.getOrDefault(curAssayId, 0);
              assayMap.put(curAssayId, freq + 1);
            }
          }
        });

    List<Long> assayIds = new ArrayList<>();
    for (Map.Entry<Long, Integer> entry : assayMap.entrySet()) {
      if (entry.getValue() == uniqueProjectIds.size()) {
        assayIds.add(entry.getKey());
      }
    }

    for (Assay requisitionAssay : requisitionAssays) {
      if (!assayIds.contains(requisitionAssay.getId())) {
        assayIds.add(requisitionAssay.getId());
      }
    }
    return assayIds;
  }

}


