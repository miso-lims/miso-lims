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

import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryIndexFamilyService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryIndexService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryIndexDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/libraryindex")
public class LibraryIndexController extends AbstractTypeDataController<LibraryIndex, LibraryIndexDto> {

  @Autowired
  private LibraryIndexService indexService;

  @Autowired
  private LibraryIndexFamilyService indexFamilyService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public LibraryIndexController() {
    super("Library Indices", "libraryindex", "libraryindex");
  }

  @GetMapping("/bulk/new")
  public ModelAndView create(@RequestParam("indexFamilyId") long indexFamilyId, @RequestParam("quantity") int quantity,
      ModelMap model)
      throws IOException {
    final LibraryIndexFamily family = indexFamilyService.get(indexFamilyId);
    if (family == null) {
      throw new ClientErrorException(String.format("Index family %d not found", indexFamilyId));
    }
    return bulkCreate(quantity, model, (config, mapper) -> {
      config.set("indexFamily", mapper.valueToTree(Dtos.asDto(family)));
    });
  }

  @PostMapping("/bulk/edit")
  public ModelAndView edit(@RequestParam Map<String, String> formData, ModelMap model) throws IOException {
    String idString = MisoWebUtils.getStringInput("ids", formData, true);
    LibraryIndexFamily family = null;
    for (Long id : LimsUtils.parseIds(idString)) {
      LibraryIndex index = indexService.get(id);
      if (index != null) {
        if (family == null) {
          family = index.getFamily();
        }
        if (index.getFamily().getId() != family.getId()) {
          throw new ClientErrorException("Indices must all belong to the same index family");
        }
      }
    }
    final LibraryIndexFamily singleFamily = family;
    return bulkEdit(idString, model, (config, mapper) -> {
      config.set("indexFamily", mapper.valueToTree(Dtos.asDto(singleFamily)));
    });
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<LibraryIndex> getService() {
    return indexService;
  }

  @Override
  protected LibraryIndexDto toDto(LibraryIndex object) {
    return Dtos.asDto(object);
  }

  @Override
  protected LibraryIndexDto makeDto() {
    return new LibraryIndexDto();
  }

}
