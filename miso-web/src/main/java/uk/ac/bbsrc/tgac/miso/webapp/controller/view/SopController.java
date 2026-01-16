package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SopDto;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
@RequestMapping("/sop")
public class SopController extends AbstractTypeDataController<Sop, SopDto> {

  @Autowired
  private SopService sopService;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private ObjectMapper mapper;

  public SopController() {
    super("SOPs", "sop", "sop");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    model.put("title", "SOPs");

    TabbedListItemsPage listSops = new TabbedListItemsPage(
        "sop",
        "category",
        Stream.of(SopCategory.values()),
        SopCategory::getLabel,
        SopCategory::name,
        mapper) {

      @Override
      protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
        config.put("isAdmin", authorizationManager.getCurrentUser().isAdmin());
      }
    };

    return listSops.list(model);
  }

  @GetMapping("/new")
  public ModelAndView create(@RequestParam(name = "baseId", required = false) Long baseId,
      @RequestParam(name = "copyId", required = false) Long copyId, ModelMap model) throws IOException {

    authorizationManager.throwIfNonAdmin();

    Long sourceId = baseId != null ? baseId : copyId;

    SopDto dto;
    if (sourceId != null) {
      Sop sop = sopService.get(sourceId);
      dto = sop == null ? makeDto() : Dtos.asDto(sop);
      dto.setId(0L);
      if (dto.getSopFields() != null) {
        dto.getSopFields().forEach(f -> {
          if (f != null) {
            f.setId(0L);
          }
        });
      }
    } else {
      dto = makeDto();
    }

    model.put("title", "Create SOP");
    model.put("pageMode", "create");
    model.put("sopJson", mapper.writeValueAsString(dto));
    model.put("sopCategories", SopCategory.values());
    model.put("isAdmin", authorizationManager.getCurrentUser().isAdmin());

    return new ModelAndView("/WEB-INF/pages/editSop.jsp", model);
  }

  @GetMapping("/{id:\\d+}")
  public ModelAndView edit(@PathVariable("id") long id, ModelMap model) throws IOException {
    authorizationManager.throwIfNonAdmin();

    Sop sop = sopService.get(id);
    if (sop == null) {
      return new ModelAndView("redirect:/sop/list");
    }

    SopDto dto = Dtos.asDto(sop);

    model.put("title", "SOP " + id);
    model.put("pageMode", "edit");
    model.put("sopJson", mapper.writeValueAsString(dto));
    model.put("sopCategories", SopCategory.values());
    model.put("isAdmin", authorizationManager.getCurrentUser().isAdmin());

    return new ModelAndView("/WEB-INF/pages/editSop.jsp", model);
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<Sop> getService() {
    return sopService;
  }

  @Override
  protected SopDto toDto(Sop object) {
    return Dtos.asDto(object);
  }

  @Override
  protected SopDto makeDto() {
    return new SopDto();
  }

}
