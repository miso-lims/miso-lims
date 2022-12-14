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

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryDesignCodeService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDesignCodeDto;

@Controller
@RequestMapping("/librarydesigncode")
public class LibraryDesignCodeController extends AbstractTypeDataController<LibraryDesignCode, LibraryDesignCodeDto> {

  @Autowired
  private LibraryDesignCodeService libraryDesignCodeService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public LibraryDesignCodeController() {
    super("Library Design Codes", "librarydesigncode", "librarydesigncode", true);
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(libraryDesignCodeService.list(), model);
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
  protected ProviderService<LibraryDesignCode> getService() {
    return libraryDesignCodeService;
  }

  @Override
  protected LibraryDesignCodeDto toDto(LibraryDesignCode object) {
    return Dtos.asDto(object);
  }

  @Override
  protected LibraryDesignCodeDto makeDto() {
    return new LibraryDesignCodeDto();
  }

}
