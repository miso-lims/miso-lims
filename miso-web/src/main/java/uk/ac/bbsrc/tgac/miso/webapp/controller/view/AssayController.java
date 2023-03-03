package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayMetric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.AssayService;
import uk.ac.bbsrc.tgac.miso.dto.AssayDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPageWithAuthorization;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/assay")
public class AssayController {

  @Autowired
  private AssayService assayService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ObjectMapper mapper;

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    model.put("title", "Assays");
    Stream<AssayDto> dtos = assayService.list().stream().map(AssayDto::from);
    ListItemsPage listPage = new ListItemsPageWithAuthorization("assay", authorizationManager, mapper);
    return listPage.list(model, dtos);
  }

  @GetMapping("/new")
  public ModelAndView create(@RequestParam(required = false) Long baseId, ModelMap model) throws IOException {
    model.put("title", "New Assay");
    Assay assay = new Assay();

    if (baseId != null) {
      Assay base = assayService.get(baseId);
      if (base == null) {
        throw new ClientErrorException("Assay with ID %d not found to copy".formatted(baseId));
      }
      assay.setAlias(base.getAlias());
      assay.setVersion(base.getVersion() + " COPY");
      assay.setDescription(base.getDescription());
      for (AssayTest test : base.getAssayTests()) {
        assay.getAssayTests().add(test);
      }
      for (AssayMetric baseMetric : base.getAssayMetrics()) {
        AssayMetric metric = new AssayMetric();
        metric.setAssay(assay);
        metric.setMetric(baseMetric.getMetric());
        metric.setMinimumThreshold(baseMetric.getMinimumThreshold());
        metric.setMaximumThreshold(baseMetric.getMaximumThreshold());
        assay.getAssayMetrics().add(metric);
      }
    }

    return setupForm(assay, PageMode.CREATE, model);
  }

  @GetMapping("/{id}")
  public ModelAndView edit(@PathVariable long id, @RequestParam(defaultValue = "true") boolean locked, ModelMap model)
      throws IOException {
    Assay assay = assayService.get(id);
    if (assay == null) {
      throw new NotFoundException("No assay found for ID: " + id);
    }
    PageMode pageMode = locked ? PageMode.VIEW : PageMode.EDIT;
    model.put("title", "Assay " + assay.getId());
    return setupForm(assay, pageMode, model);
  }

  private ModelAndView setupForm(Assay assay, PageMode pageMode, ModelMap model) throws IOException {
    model.put(PageMode.PROPERTY, pageMode.getLabel());
    model.put("isAdmin", authorizationManager.isAdminUser());
    model.put("assayDto", mapper.writeValueAsString(AssayDto.from(assay)));
    model.put("libraryQualificationMethods",
        mapper.writeValueAsString(MisoWebUtils.getLibraryQualificationMethodDtos(mapper)));
    return new ModelAndView("/WEB-INF/pages/editAssay.jsp", model);
  }

}
