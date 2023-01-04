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

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.ReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ReferenceGenomeDto;

@Controller
@RequestMapping("/referencegenome")
public class ReferenceGenomeController extends AbstractTypeDataController<ReferenceGenome, ReferenceGenomeDto> {

  @Autowired
  private ReferenceGenomeService referenceGenomeService;
  @Autowired
  private AuthorizationManager authorizationManager;

  public ReferenceGenomeController() {
    super("Reference Genomes", "referencegenome", "referencegenome", true);
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(referenceGenomeService.list(), model);
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
  protected ProviderService<ReferenceGenome> getService() {
    return referenceGenomeService;
  }

  @Override
  protected ReferenceGenomeDto toDto(ReferenceGenome object) {
    return Dtos.asDto(object);
  }

  @Override
  protected ReferenceGenomeDto makeDto() {
    return new ReferenceGenomeDto();
  }

}
