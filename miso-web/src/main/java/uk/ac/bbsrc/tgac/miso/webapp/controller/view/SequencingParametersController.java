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

import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencingParametersDto;

@Controller
@RequestMapping("/sequencingparameters")
public class SequencingParametersController extends AbstractTypeDataController<SequencingParameters, SequencingParametersDto> {

  @Autowired
  private SequencingParametersService sequencingParametersService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public SequencingParametersController() {
    super("Sequencing Parameters", "sequencingparameters", "sequencingparameters", true);
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(sequencingParametersService.list(), model);
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
  protected ProviderService<SequencingParameters> getService() {
    return sequencingParametersService;
  }

  @Override
  protected SequencingParametersDto toDto(SequencingParameters object) {
    return Dtos.asDto(object);
  }

  @Override
  protected SequencingParametersDto makeDto() {
    return new SequencingParametersDto();
  }

}
