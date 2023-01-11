package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SubprojectDto;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/subproject")
public class SubprojectController extends AbstractTypeDataController<Subproject, SubprojectDto> {

  @Autowired
  private SubprojectService service;
  @Autowired
  private ProjectService projectService;
  @Autowired
  private AuthorizationManager authorizationManager;

  public SubprojectController() {
    super("Subprojects", "subproject", "subproject");
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<Subproject> getService() {
    return service;
  }

  @Override
  protected SubprojectDto toDto(Subproject object) {
    return Dtos.asDto(object);
  }

  @Override
  protected SubprojectDto makeDto() {
    return new SubprojectDto();
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(service.list(), model);
  }

  @Override
  protected void addBaseConfig(ObjectNode config, ObjectMapper mapper) throws IOException {
    MisoWebUtils.addJsonArray(mapper, config, "projects", projectService.list(), Dtos::asDto);
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
