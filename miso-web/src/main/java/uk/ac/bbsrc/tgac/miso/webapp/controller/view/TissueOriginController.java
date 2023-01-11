package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TissueOriginDto;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/tissueorigin")
public class TissueOriginController extends AbstractTypeDataController<TissueOrigin, TissueOriginDto> {

  @Autowired
  private TissueOriginService tissueOriginService;
  @Autowired
  private AuthorizationManager authorizationManager;

  public TissueOriginController() {
    super("Tissue Origins", "tissueorigin", "tissueorigin");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(tissueOriginService.list(), model);
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
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<TissueOrigin> getService() {
    return tissueOriginService;
  }

  @Override
  protected TissueOriginDto toDto(TissueOrigin object) {
    return Dtos.asDto(object);
  }

  @Override
  protected TissueOriginDto makeDto() {
    return new TissueOriginDto();
  }

}
