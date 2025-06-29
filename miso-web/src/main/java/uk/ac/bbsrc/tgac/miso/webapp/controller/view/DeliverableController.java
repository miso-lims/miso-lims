package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Deliverable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DeliverableCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.DeliverableCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.DeliverableService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.DeliverableCategoryDto;
import uk.ac.bbsrc.tgac.miso.dto.DeliverableDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/deliverable")
public class DeliverableController extends AbstractTypeDataController<Deliverable, DeliverableDto> {

  @Autowired
  private DeliverableService deliverableService;
  @Autowired
  private DeliverableCategoryService deliverableCategoryService;
  @Autowired
  private AuthorizationManager authorizationManager;

  public DeliverableController() {
    super("Deliverables", "deliverable", "deliverable");
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<Deliverable> getService() {
    return deliverableService;
  }

  @Override
  protected DeliverableDto makeDto() {
    return new DeliverableDto();
  }

  @Override
  protected DeliverableDto toDto(Deliverable object) {
    return Dtos.asDto(object);
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(deliverableService.list(), model);
  }

  @GetMapping("/bulk/new")
  public ModelAndView create(@RequestParam("quantity") Integer quantity, ModelMap model) throws IOException {
    return bulkCreate(quantity, model);
  }

  @PostMapping("/bulk/edit")
  public ModelAndView edit(@RequestParam Map<String, String> formData, ModelMap model) throws IOException {
    return bulkEdit(formData, model);
  }

  @Override
  protected void addHotConfig(ObjectNode config, ObjectMapper mapper) throws IOException {
    List<DeliverableCategory> categories = deliverableCategoryService.list();
    MisoWebUtils.addJsonArray(mapper, config, "categories", categories, DeliverableCategoryDto::from);
  }
}
