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

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleIndexFamilyService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleIndexService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.SampleIndexDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleIndexFamilyDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/sampleindex")
public class SampleIndexController extends AbstractTypeDataController<SampleIndex, SampleIndexDto> {

  @Autowired
  private SampleIndexService indexService;

  @Autowired
  private SampleIndexFamilyService indexFamilyService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public SampleIndexController() {
    super("Sample Indices", "sampleindex", "sampleindex");
  }

  @GetMapping("/bulk/new")
  public ModelAndView create(@RequestParam("indexFamilyId") long indexFamilyId, @RequestParam("quantity") int quantity,
      ModelMap model) throws IOException {
    final SampleIndexFamily family = indexFamilyService.get(indexFamilyId);
    if (family == null) {
      throw new ClientErrorException(String.format("Index family %d not found", indexFamilyId));
    }
    return bulkCreate(quantity, model, (config, mapper) -> {
      config.set("indexFamily", mapper.valueToTree(SampleIndexFamilyDto.from(family)));
    });
  }

  @PostMapping("/bulk/edit")
  public ModelAndView edit(@RequestParam Map<String, String> formData, ModelMap model) throws IOException {
    String idString = MisoWebUtils.getStringInput("ids", formData, true);
    SampleIndexFamily family = null;
    for (Long id : LimsUtils.parseIds(idString)) {
      SampleIndex index = indexService.get(id);
      if (index != null) {
        if (family == null) {
          family = index.getFamily();
        } else if (index.getFamily().getId() != family.getId()) {
          throw new ClientErrorException("Indices must all belong to the same index family");
        }
      }
    }
    final SampleIndexFamily singleFamily = family;
    return bulkEdit(idString, model, (config, mapper) -> {
      config.set("indexFamily", mapper.valueToTree(SampleIndexFamilyDto.from(singleFamily)));
    });
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<SampleIndex> getService() {
    return indexService;
  }

  @Override
  protected SampleIndexDto toDto(SampleIndex object) {
    return SampleIndexDto.from(object);
  }

  @Override
  protected SampleIndexDto makeDto() {
    return new SampleIndexDto();
  }

}
