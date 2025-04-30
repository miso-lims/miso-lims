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

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleIndexFamilyService;
import uk.ac.bbsrc.tgac.miso.dto.SampleIndexFamilyDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/sampleindexfamily")
public class SampleIndexFamilyController extends AbstractTypeDataController<SampleIndexFamily, SampleIndexFamilyDto> {

  @Autowired
  private SampleIndexFamilyService indexFamilyService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ObjectMapper mapper;

  public SampleIndexFamilyController() {
    super("Sample Index Families", "sampleindexfamily", "sampleindexfamily");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(indexFamilyService.list(), model);
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<SampleIndexFamily> getService() {
    return indexFamilyService;
  }

  @Override
  protected SampleIndexFamilyDto toDto(SampleIndexFamily object) {
    return SampleIndexFamilyDto.from(object);
  }

  @Override
  protected SampleIndexFamilyDto makeDto() {
    return new SampleIndexFamilyDto();
  }

  @GetMapping("/new")
  public ModelAndView create(ModelMap model) throws IOException {
    model.put("title", "New Sample Index Family");
    model.put(PageMode.PROPERTY, PageMode.CREATE.getLabel());
    return indexFamilyPage(new SampleIndexFamily(), model);
  }

  @GetMapping("/{id}")
  public ModelAndView edit(@PathVariable long id, ModelMap model) throws IOException {
    SampleIndexFamily family = indexFamilyService.get(id);
    if (family == null) {
      throw new NotFoundException("No sample index family found for ID: " + id);
    }
    model.put("title", "Sample Index Family " + id);
    model.put(PageMode.PROPERTY, PageMode.EDIT.getLabel());
    return indexFamilyPage(family, model);
  }

  private ModelAndView indexFamilyPage(SampleIndexFamily indexFamily, ModelMap model) throws IOException {
    SampleIndexFamilyDto dto = SampleIndexFamilyDto.from(indexFamily);
    model.put("indexFamilyDto", mapper.writeValueAsString(dto));
    if (indexFamily.isSaved()) {
      model.put("indexFamilyId", indexFamily.getId());
      model.put("indices", dto.getIndices());
    }
    return new ModelAndView("/WEB-INF/pages/editSampleIndexFamily.jsp", model);
  }

}
