package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.impl.DeliverableCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.DeliverableCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.DeliverableCategoryDto;

@Controller
@RequestMapping("/deliverablecategory")
public class DeliverableCategoryController
    extends AbstractTypeDataController<DeliverableCategory, DeliverableCategoryDto> {

  @Autowired
  private DeliverableCategoryService deliverableCategoryService;
  @Autowired
  private AuthorizationManager authorizationManager;

  public DeliverableCategoryController() {
    super("Deliverable Categories", "deliverablecategory", "deliverablecategory");
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<DeliverableCategory> getService() {
    return deliverableCategoryService;
  }

  @Override
  protected DeliverableCategoryDto toDto(DeliverableCategory object) {
    return DeliverableCategoryDto.from(object);
  }

  @Override
  protected DeliverableCategoryDto makeDto() {
    return new DeliverableCategoryDto();
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(deliverableCategoryService.list(), model);
  }

  @GetMapping("/bulk/new")
  public ModelAndView create(@RequestParam("quantity") Integer quantity, ModelMap model) throws IOException {
    return bulkCreate(quantity, model);
  }

  @PostMapping("/bulk/edit")
  public ModelAndView edit(@RequestParam Map<String, String> formData, ModelMap model) throws IOException {
    return bulkEdit(formData, model);
  }

}
