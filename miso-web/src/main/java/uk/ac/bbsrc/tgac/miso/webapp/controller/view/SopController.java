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

import uk.ac.bbsrc.tgac.miso.core.data.SopField;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SopDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;
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
        config.put("isAdmin", authorizationManager.isAdminUser());
      }
    };

    return listSops.list(model);
  }

  @GetMapping("/new")
  public ModelAndView create(@RequestParam(name = "baseId", required = false) Long baseId,
      @RequestParam(name = "copyId", required = false) Long copyId, ModelMap model) throws IOException {

    authorizationManager.throwIfNonAdmin();

    Sop sop = new Sop();
    Long sourceId = baseId != null ? baseId : copyId;

    if (sourceId != null) {
      Sop source = sopService.get(sourceId);
      if (source == null) {
        throw new ClientErrorException("SOP with ID %d not found to copy".formatted(sourceId));
      }
      sop.setAlias(source.getAlias());
      sop.setVersion(source.getVersion());
      sop.setCategory(source.getCategory());
      sop.setUrl(source.getUrl());
      sop.setArchived(source.isArchived());
      if (source.getSopFields() != null) {
        for (SopField sourceField : source.getSopFields()) {
          SopField field = new SopField();
          field.setName(sourceField.getName());
          field.setUnits(sourceField.getUnits());
          field.setFieldType(sourceField.getFieldType());
          sop.getSopFields().add(field);
        }
      }
    }

    model.put("title", "New SOP");
    model.addAttribute(PageMode.PROPERTY, PageMode.CREATE.getLabel());
    return setupForm(sop, PageMode.CREATE, model);
  }

  @GetMapping("/{id}")
  public ModelAndView edit(@PathVariable long id, ModelMap model) throws IOException {
    authorizationManager.throwIfNonAdmin();

    Sop sop = sopService.get(id);
    if (sop == null) {
      throw new NotFoundException("No SOP found for ID: " + id);
    }

    model.put("title", "SOP " + id);
    model.addAttribute(PageMode.PROPERTY, PageMode.EDIT.getLabel());
    return setupForm(sop, PageMode.EDIT, model);
  }

  private ModelAndView setupForm(Sop sop, PageMode pageMode, ModelMap model) throws IOException {
    model.put(PageMode.PROPERTY, pageMode.getLabel());
    model.put("isAdmin", authorizationManager.isAdminUser());
    model.put("sopDto", mapper.writeValueAsString(Dtos.asDto(sop)));
    model.put("sopCategories", SopCategory.values());
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
