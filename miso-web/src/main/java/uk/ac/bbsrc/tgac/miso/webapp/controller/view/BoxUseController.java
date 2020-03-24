package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BoxUseService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.BoxUseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@Controller
@RequestMapping("/boxuse")
public class BoxUseController extends AbstractTypeDataController<BoxUse, BoxUseDto> {

  @Autowired
  private BoxUseService boxUseService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public BoxUseController() {
    super("Box Uses", "boxuse", "boxuse");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(boxUseService.list(), model);
  }

  @GetMapping("/bulk/new")
  public ModelAndView create(@RequestParam("quantity") Integer quantity, ModelMap model) throws IOException {
    return bulkCreate(quantity, model);
  }

  @GetMapping("/bulk/edit")
  public ModelAndView edit(@RequestParam("ids") String idString, ModelMap model) throws IOException {
    return bulkEdit(idString, model);
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<BoxUse> getService() {
    return boxUseService;
  }

  @Override
  protected BoxUseDto toDto(BoxUse object) {
    return Dtos.asDto(object);
  }

  @Override
  protected BoxUseDto makeDto() {
    return new BoxUseDto();
  }

}
