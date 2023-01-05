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

import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.PipelineService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PipelineDto;

@Controller
@RequestMapping("/pipeline")
public class PipelineController extends AbstractTypeDataController<Pipeline, PipelineDto> {

  @Autowired
  private PipelineService pipelineService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public PipelineController() {
    super("Pipelines", "pipeline", "pipeline");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(pipelineService.list(), model);
  }

  @PostMapping("/bulk/new")
  public ModelAndView create(@RequestParam Map<String, String> formData, ModelMap model) throws IOException {
    return bulkCreate(formData, model);
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
  protected ProviderService<Pipeline> getService() {
    return pipelineService;
  }

  @Override
  protected PipelineDto toDto(Pipeline object) {
    return Dtos.asDto(object);
  }

  @Override
  protected PipelineDto makeDto() {
    return new PipelineDto();
  }

}
