package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import static uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils.getStringInput;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.annotation.Resource;
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkCreateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/box")
public class EditBoxController {
  protected static final Logger log = LoggerFactory.getLogger(EditBoxController.class);

  @Autowired
  private BoxService boxService;
  @Autowired
  private ObjectMapper mapper;

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

  @Resource
  private Boolean boxScannerEnabled;

  @Resource
  private Map<String, BoxScanner> boxScanners;

  @ModelAttribute("scannerEnabled")
  public Boolean isScannerEnabled() {
    return boxScannerEnabled;
  }

  @ModelAttribute("scannerNames")
  public Set<String> getScannerNames() {
    return boxScanners.keySet();
  }

  @GetMapping(value = "/bulk/new")
  public ModelAndView newBoxes(@RequestParam("quantity") Integer quantity, ModelMap model) throws IOException {
    if (quantity == null || quantity <= 0)
      throw new RestException("Must specify quantity of boxes to create", Status.BAD_REQUEST);

    return new BulkCreateBoxBackend(quantity, mapper).create(model);
  }

  @PostMapping(value = "/bulk/edit")
  public ModelAndView editBoxes(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    String boxIds = getStringInput("ids", form, true);
    return new BulkEditBoxBackend(mapper).edit(boxIds, model);
  }

  @GetMapping(value = "/new")
  public ModelAndView newBox(ModelMap model) throws IOException {
    model.put("title", "New Box");
    model.put(PageMode.PROPERTY, PageMode.CREATE.getLabel());
    return setupForm(new BoxImpl(), model);
  }

  @GetMapping(value = "/{boxId}")
  public ModelAndView setupForm(@PathVariable Long boxId, ModelMap model) throws IOException {
    Box box = boxService.get(boxId);
    if (box == null)
      throw new NotFoundException("No box found for ID " + boxId.toString());
    if (box.getSize().getRows() == 8 && box.getSize().getColumns() == 12) {
      model.put("fragmentAnalyserCompatible", true);
    }
    model.put("title", "Box " + box.getId());
    model.put(PageMode.PROPERTY, PageMode.EDIT.getLabel());
    return setupForm(box, model);
  }

  private ModelAndView setupForm(Box box, ModelMap model) throws IOException {
    model.put("box", box);

    // add JSON
    Collection<BoxableView> contents = boxService.getBoxContents(box.getId());
    model.put("boxJSON", mapper.writer().writeValueAsString(Dtos.asDtoWithBoxables(box, contents)));

    return new ModelAndView("/WEB-INF/pages/editBox.jsp", model);
  }

  private final class BulkCreateBoxBackend extends BulkCreateTableBackend<BoxDto> {
    public BulkCreateBoxBackend(Integer quantity, ObjectMapper mapper) {
      super("box", BoxDto.class, "Boxes", new BoxDto(), quantity, mapper);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      // No configuration required
    }
  }

  private final class BulkEditBoxBackend extends BulkEditTableBackend<Box, BoxDto> {

    private BulkEditBoxBackend(ObjectMapper mapper) {
      super("box", BoxDto.class, "Boxes", mapper);
    }

    @Override
    protected BoxDto asDto(Box model) {
      return Dtos.asDto(model, false);
    }

    @Override
    protected Stream<Box> load(List<Long> boxIds) throws IOException {
      return boxService.listByIdList(boxIds).stream();
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.put(PageMode.PROPERTY, PageMode.EDIT.getLabel());
    }
  }
}
