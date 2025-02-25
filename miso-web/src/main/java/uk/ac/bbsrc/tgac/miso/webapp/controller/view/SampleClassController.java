package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleClassDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/sampleclass")
public class SampleClassController extends AbstractTypeDataController<SampleClass, SampleClassDto> {

  @Autowired
  private SampleClassService sampleClassService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ObjectMapper mapper;

  public SampleClassController() {
    super("Sample Classes", "sampleclass", "sampleclass");
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<SampleClass> getService() {
    return sampleClassService;
  }

  @Override
  protected SampleClassDto toDto(SampleClass object) {
    return Dtos.asDto(object);
  }

  @Override
  protected SampleClassDto makeDto() {
    return new SampleClassDto();
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(sampleClassService.list(), model);
  }

  @GetMapping("/new")
  public ModelAndView create(ModelMap model) throws IOException {
    model.put(PageMode.PROPERTY, PageMode.CREATE.getLabel());
    model.put("title", "New Sample Class");
    return sampleClassPage(new SampleClassImpl(), model);
  }

  @GetMapping("/{id}")
  public ModelAndView edit(@PathVariable("id") long id, ModelMap model) throws IOException {
    SampleClass sampleClass = sampleClassService.get(id);
    if (sampleClass == null) {
      throw new NotFoundException("No sample class found for ID: " + id);
    }
    model.put(PageMode.PROPERTY, PageMode.EDIT.getLabel());
    model.put("title", "Sample Class " + id);
    return sampleClassPage(sampleClass, model);
  }

  private ModelAndView sampleClassPage(SampleClass sampleClass, ModelMap model) throws IOException {
    model.put("isAdmin", authorizationManager.isAdminUser());
    model.put("sampleClassDto", mapper.writeValueAsString(Dtos.asDto(sampleClass)));
    return new ModelAndView("/WEB-INF/pages/editSampleClass.jsp", model);
  }

}
