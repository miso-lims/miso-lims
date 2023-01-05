package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LabService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LabDto;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/lab")
public class LabController extends AbstractTypeDataController<Lab, LabDto> {

  @Autowired
  private LabService labService;
  @Autowired
  private AuthorizationManager authorizationManager;

  public LabController() {
    super("Labs", "lab", "lab");
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<Lab> getService() {
    return labService;
  }

  @Override
  protected LabDto toDto(Lab object) {
    return Dtos.asDto(object);
  }

  @Override
  protected LabDto makeDto() {
    return new LabDto();
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(labService.list(), model);
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
