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

import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.IndexFamilyService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.IndexFamilyDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/indexfamily")
public class IndexFamilyController extends AbstractTypeDataController<IndexFamily, IndexFamilyDto> {

  @Autowired
  private IndexFamilyService indexFamilyService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ObjectMapper mapper;

  public IndexFamilyController() {
    super("Index Families", "indexfamily", "indexfamily");
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
  protected ProviderService<IndexFamily> getService() {
    return indexFamilyService;
  }

  @Override
  protected IndexFamilyDto toDto(IndexFamily object) {
    return Dtos.asDto(object);
  }

  @Override
  protected IndexFamilyDto makeDto() {
    return new IndexFamilyDto();
  }

  @GetMapping("/new")
  public ModelAndView create(ModelMap model) throws IOException {
    model.put("title", "New Index Family");
    model.put(PageMode.PROPERTY, PageMode.CREATE.getLabel());
    return indexFamilyPage(new IndexFamily(), model);
  }

  @GetMapping("/{id}")
  public ModelAndView edit(@PathVariable long id, ModelMap model) throws IOException {
    IndexFamily family = indexFamilyService.get(id);
    if (family == null) {
      throw new NotFoundException("No index family found for ID: " + id);
    }
    model.put("title", "Index Family " + id);
    model.put(PageMode.PROPERTY, PageMode.EDIT.getLabel());
    return indexFamilyPage(family, model);
  }

  private ModelAndView indexFamilyPage(IndexFamily indexFamily, ModelMap model) throws IOException {
    model.put("indexFamilyDto", mapper.writeValueAsString(Dtos.asDto(indexFamily)));
    return new ModelAndView("/WEB-INF/pages/editIndexFamily.jsp", model);
  }

}
