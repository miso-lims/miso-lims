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

import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryIndexFamilyService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryIndexFamilyDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/libraryindexfamily")
public class LibraryIndexFamilyController
    extends AbstractTypeDataController<LibraryIndexFamily, LibraryIndexFamilyDto> {

  @Autowired
  private LibraryIndexFamilyService indexFamilyService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ObjectMapper mapper;

  public LibraryIndexFamilyController() {
    super("Library Index Families", "libraryindexfamily", "libraryindexfamily");
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
  protected ProviderService<LibraryIndexFamily> getService() {
    return indexFamilyService;
  }

  @Override
  protected LibraryIndexFamilyDto toDto(LibraryIndexFamily object) {
    return Dtos.asDto(object);
  }

  @Override
  protected LibraryIndexFamilyDto makeDto() {
    return new LibraryIndexFamilyDto();
  }

  @GetMapping("/new")
  public ModelAndView create(ModelMap model) throws IOException {
    model.put("title", "New Library Index Family");
    model.put(PageMode.PROPERTY, PageMode.CREATE.getLabel());
    return indexFamilyPage(new LibraryIndexFamily(), model);
  }

  @GetMapping("/{id}")
  public ModelAndView edit(@PathVariable long id, ModelMap model) throws IOException {
    LibraryIndexFamily family = indexFamilyService.get(id);
    if (family == null) {
      throw new NotFoundException("No library index family found for ID: " + id);
    }
    model.put("title", "Library Index Family " + id);
    model.put(PageMode.PROPERTY, PageMode.EDIT.getLabel());
    return indexFamilyPage(family, model);
  }

  private ModelAndView indexFamilyPage(LibraryIndexFamily indexFamily, ModelMap model) throws IOException {
    model.put("indexFamilyDto", mapper.writeValueAsString(Dtos.asDto(indexFamily)));
    return new ModelAndView("/WEB-INF/pages/editLibraryIndexFamily.jsp", model);
  }

}
