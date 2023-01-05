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

import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.TissuePieceTypeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TissuePieceTypeDto;

@Controller
@RequestMapping("/tissuepiecetype")
public class TissuePieceTypeController extends AbstractTypeDataController<TissuePieceType, TissuePieceTypeDto> {

  @Autowired
  private TissuePieceTypeService tissuePieceTypeService;
  @Autowired
  private AuthorizationManager authorizationManager;

  public TissuePieceTypeController() {
    super("Tissue Piece Types", "tissuepiecetype", "tissuepiecetype");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(tissuePieceTypeService.list(), model);
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
  protected ProviderService<TissuePieceType> getService() {
    return tissuePieceTypeService;
  }

  @Override
  protected TissuePieceTypeDto toDto(TissuePieceType object) {
    return Dtos.asDto(object);
  }

  @Override
  protected TissuePieceTypeDto makeDto() {
    return new TissuePieceTypeDto();
  }

}
