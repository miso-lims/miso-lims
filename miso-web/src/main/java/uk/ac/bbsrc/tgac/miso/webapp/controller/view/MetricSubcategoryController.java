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

import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.MetricSubcategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.MetricSubcategoryDto;

@Controller
@RequestMapping("/metricsubcategory")
public class MetricSubcategoryController extends AbstractTypeDataController<MetricSubcategory, MetricSubcategoryDto> {

  @Autowired
  private MetricSubcategoryService metricSubcategoryService;
  @Autowired
  private AuthorizationManager authorizationManager;

  public MetricSubcategoryController() {
    super("Metric Subcategories", "metricsubcategory", "metricsubcategory");
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<MetricSubcategory> getService() {
    return metricSubcategoryService;
  }

  @Override
  protected MetricSubcategoryDto toDto(MetricSubcategory object) {
    return MetricSubcategoryDto.from(object);
  }

  @Override
  protected MetricSubcategoryDto makeDto() {
    return new MetricSubcategoryDto();
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(metricSubcategoryService.list(), model);
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
