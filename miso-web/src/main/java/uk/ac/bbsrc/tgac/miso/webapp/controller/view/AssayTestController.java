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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.AssayTestService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.AssayTestDto;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/assaytest")
public class AssayTestController extends AbstractTypeDataController<AssayTest, AssayTestDto> {

  @Autowired
  private AssayTestService assayTestService;
  @Autowired
  private AuthorizationManager authorizationManager;

  public AssayTestController() {
    super("Assay Tests", "assaytest", "assaytest");
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<AssayTest> getService() {
    return assayTestService;
  }

  @Override
  protected AssayTestDto toDto(AssayTest object) {
    return AssayTestDto.from(object);
  }

  @Override
  protected AssayTestDto makeDto() {
    return new AssayTestDto();
  }

  @Override
  protected void addBaseConfig(ObjectNode config, ObjectMapper mapper) throws IOException {
    config.set("libraryQualificationMethods", MisoWebUtils.getLibraryQualificationMethodDtos(mapper));
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(assayTestService.list(), model);
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
