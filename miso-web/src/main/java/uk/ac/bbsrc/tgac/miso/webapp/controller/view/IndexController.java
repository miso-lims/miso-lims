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

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.IndexFamilyService;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.IndexDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/index")
public class IndexController extends AbstractTypeDataController<Index, IndexDto> {

  @Autowired
  private IndexService indexService;

  @Autowired
  private IndexFamilyService indexFamilyService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public IndexController() {
    super("Indices", "index", "index");
  }

  @GetMapping("/bulk/new")
  public ModelAndView create(@RequestParam("indexFamilyId") long indexFamilyId, @RequestParam("quantity") int quantity, ModelMap model)
      throws IOException {
    final IndexFamily family = indexFamilyService.get(indexFamilyId);
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
    IndexFamily family = null;
    for (Long id : LimsUtils.parseIds(idString)) {
      Index index = indexService.get(id);
      if (index != null) {
        if (family == null) {
          family = index.getFamily();
        }
        if (index.getFamily().getId() != family.getId()) {
          throw new ClientErrorException("Indices must all belong to the same index family");
        }
      }
    }
    final IndexFamily singleFamily = family;
    return bulkEdit(idString, model, (config, mapper) -> {
      config.set("indexFamily", mapper.valueToTree(Dtos.asDto(singleFamily)));
    });
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<Index> getService() {
    return indexService;
  }

  @Override
  protected IndexDto toDto(Index object) {
    return Dtos.asDto(object);
  }

  @Override
  protected IndexDto makeDto() {
    return new IndexDto();
  }

}
