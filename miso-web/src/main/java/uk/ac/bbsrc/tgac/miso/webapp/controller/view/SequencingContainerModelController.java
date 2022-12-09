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

import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingContainerModelService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencingContainerModelDto;

@Controller
@RequestMapping("/containermodel")
public class SequencingContainerModelController extends AbstractTypeDataController<SequencingContainerModel, SequencingContainerModelDto> {

  @Autowired
  private SequencingContainerModelService containerModelService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public SequencingContainerModelController() {
    super("Container Models", "containermodel", "containermodel", true);
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(containerModelService.list(), model);
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
  protected ProviderService<SequencingContainerModel> getService() {
    return containerModelService;
  }

  @Override
  protected SequencingContainerModelDto toDto(SequencingContainerModel object) {
    return Dtos.asDto(object);
  }

  @Override
  protected SequencingContainerModelDto makeDto() {
    return new SequencingContainerModelDto();
  }

}
