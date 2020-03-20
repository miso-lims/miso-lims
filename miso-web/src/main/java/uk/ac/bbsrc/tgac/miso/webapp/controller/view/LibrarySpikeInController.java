package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibrarySpikeInService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibrarySpikeInDto;

@Controller
@RequestMapping("/libraryspikein")
public class LibrarySpikeInController extends AbstractTypeDataController<LibrarySpikeIn, LibrarySpikeInDto> {

  @Autowired
  private LibrarySpikeInService librarySpikeInService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public LibrarySpikeInController() {
    super("Library Spike-Ins", "libraryspikein", "libraryspikein");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(librarySpikeInService.list(), model);
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
  protected ProviderService<LibrarySpikeIn> getService() {
    return librarySpikeInService;
  }

  @Override
  protected LibrarySpikeInDto toDto(LibrarySpikeIn object) {
    return Dtos.asDto(object);
  }

  @Override
  protected LibrarySpikeInDto makeDto() {
    return new LibrarySpikeInDto();
  }

}
